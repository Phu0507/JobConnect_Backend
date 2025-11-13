package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.config.AwsS3Service;
import com.jobconnect_backend.dto.request.ResumeRequest;
import com.jobconnect_backend.entities.*;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.JobSeekerProfileRepository;
import com.jobconnect_backend.repositories.ResumeRepository;
import com.jobconnect_backend.services.IResumeService;
import com.jobconnect_backend.utils.ValidateField;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements IResumeService {
    private final ResumeRepository resumeRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final ValidateField validateField;
    private final AwsS3Service awsS3Service;
    @Override
    public void createResume(Integer profileId, ResumeRequest request, BindingResult result) throws IOException {
        Map<String, String> errors = validateField.getErrors(result);

        if (!errors.isEmpty()) {
            throw new BadRequestException("Please complete all required fields to proceed.", errors);
        }

        JobSeekerProfile profile = jobSeekerProfileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException("Profile not found"));

        boolean isResumeExist = resumeRepository.existsByResumeNameAndJobSeekerProfileProfileIdAndDeletedIsFalse(request.getResumeName(), profile.getProfileId());

        if (isResumeExist) {
            throw new BadRequestException("Resume name has been used");
        }

        String resumeFileName = request.getResume().getOriginalFilename();
        InputStream inputStream = request.getResume().getInputStream();
        String contentType = request.getResume().getContentType();
        String extension = resumeFileName.substring(resumeFileName.lastIndexOf("."));
        String baseName = resumeFileName.substring(0, resumeFileName.lastIndexOf("."));
        String s3Key = baseName + "_" + System.currentTimeMillis() + extension;
        String s3Url = awsS3Service.uploadFileToS3(inputStream, s3Key, contentType);

        Resume resume = Resume.builder()
                .jobSeekerProfile(profile)
                .resumeName(request.getResumeName())
                .resumePath(s3Url)
                .uploadedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        resumeRepository.save(resume);
    }

    @Override
    public void deleteResume(Integer resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new BadRequestException("Resume not found"));
        if (resume.isDeleted()) {
            throw new BadRequestException("Resume has been deleted");
        }
        resume.setDeleted(true);
        resumeRepository.save(resume);
    }

    @Override
    public void autoCreateResume(Integer profileId, String resumeName) {
        if (resumeName == null || resumeName.trim().isEmpty()) {
            throw new BadRequestException("Resume name cannot be empty");
        }

        JobSeekerProfile profile = jobSeekerProfileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException("Profile not found"));

        boolean isExist = resumeRepository
                .existsByResumeNameAndJobSeekerProfileProfileIdAndDeletedIsFalse(
                        resumeName, profile.getProfileId());

        if (isExist) {
            throw new BadRequestException("Resume name has been used");
        }

        // ================== BUILD SKILLS ==================
        String skillsHtml = "";
        String skillsPlain = "các công nghệ backend";
        if (profile.getSkills() != null && !profile.getSkills().isEmpty()) {
            skillsHtml = profile.getSkills().stream()
                    .map(skill -> "<li>" + escapeHtml(skill.getName()) + "</li>")
                    .collect(Collectors.joining("\n"));

            skillsPlain = profile.getSkills().stream()
                    .map(Skill::getName)
                    .collect(Collectors.joining(", "));
        }

        // ================== BUILD EXPERIENCES ==================
        String experiencesHtml = "";
        if (profile.getWorkExperiences() != null && !profile.getWorkExperiences().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");

            List<WorkExperience> sortedExperiences = profile.getWorkExperiences().stream()
                    .sorted(Comparator.comparing(WorkExperience::getStartDate).reversed())
                    .toList();

            experiencesHtml = sortedExperiences.stream()
                    .map(exp -> {
                        String timeRange = "";
                        if (exp.getStartDate() != null) {
                            timeRange = exp.getStartDate().format(formatter) + " - ";
                            if (exp.getEndDate() != null) {
                                timeRange += exp.getEndDate().format(formatter);
                            } else {
                                timeRange += "Hiện tại";
                            }
                        }

                        String expSkills = "";
                        if (exp.getSkills() != null && !exp.getSkills().isEmpty()) {
                            expSkills = exp.getSkills().stream()
                                    .map(Skill::getName)
                                    .collect(Collectors.joining(", "));
                        }

                        String expCategories = "";
                        if (exp.getCategories() != null && !exp.getCategories().isEmpty()) {
                            expCategories = exp.getCategories().stream()
                                    .map(JobCategory::getName)
                                    .collect(Collectors.joining(", "));
                        }

                        return """
                    <div class="experience-item">
                        <div class="experience-header">
                            <h3>%s – %s</h3>
                            <span class="time">%s</span>
                        </div>
                        <div class="meta">
                            <span class="job-type">%s</span>
                            %s
                        </div>
                        <p class="desc">%s</p>
                        %s
                    </div>
                    """.formatted(
                                escapeHtml(exp.getJobPosition().getName()),
                                escapeHtml(exp.getCompany().getCompanyName()),
                                escapeHtml(timeRange),
                                exp.getJobType() != null ? exp.getJobType().name() : "",
                                !expCategories.isEmpty()
                                        ? "<span class=\"category\">Lĩnh vực: " + escapeHtml(expCategories) + "</span>"
                                        : "",
                                escapeHtml(exp.getDescription() != null ? exp.getDescription() : ""),
                                !expSkills.isEmpty()
                                        ? "<div class=\"exp-skills\"><b>Kỹ năng sử dụng:</b> " + escapeHtml(expSkills) + "</div>"
                                        : ""
                        );
                    })
                    .collect(Collectors.joining("\n"));
        }

        // ================== LẤY EMAIL / PHONE TỪ USER ==================
        User user = profile.getUser();
        String email = user != null && user.getEmail() != null ? user.getEmail() : "";
        String phone = user != null && user.getPhone() != null ? user.getPhone() : "";

        // ================== SUMMARY / EDUCATION / CERT / PROJECTS ==================
        String title = profile.getTitle() != null ? profile.getTitle() : "Backend Developer";
        String birthDay = profile.getBirthDay() != null
                ? profile.getBirthDay().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "";

        String summaryText = """
%s với kinh nghiệm phát triển hệ thống web, xây dựng API và làm việc với cơ sở dữ liệu.
Thành thạo %s. Có khả năng làm việc nhóm, phân tích yêu cầu và tối ưu hiệu năng hệ thống.
Mong muốn phát triển sự nghiệp trong lĩnh vực phát triển phần mềm với trọng tâm backend và cloud.
""".formatted(
                title,
                skillsPlain
        );

        String careerObjectiveText = """
Mong muốn phát triển sự nghiệp với vai trò %s, nâng cao chuyên môn về %s và tham gia vào các dự án thực tế có độ phức tạp cao.
Hướng đến việc trở thành một lập trình viên chuyên nghiệp, đóng góp vào sự phát triển bền vững của doanh nghiệp.
""".formatted(
                title,
                skillsPlain
        );

        String careerObjectiveHtml = escapeHtml(careerObjectiveText).replace("\n", "<br/>");

        String summaryHtml = escapeHtml(summaryText).replace("\n", "<br/>");

        // Các phần tĩnh – bạn có thể sửa wording tùy ý
        String educationHtml = """
<ul>
  <li>Cử nhân Công nghệ thông tin hoặc ngành liên quan đến phát triển phần mềm.</li>
</ul>
""";

        String certificationsHtml = """
<ul>
  <li>Chứng chỉ / khoá học lập trình Java hoặc Spring Boot (nếu có).</li>
  <li>Khoá học về cơ sở dữ liệu và SQL.</li>
  <li>Khoá học / chứng chỉ về dịch vụ Cloud (AWS / GCP / Azure).</li>
</ul>
""";

        String projectsHtml = """
<ul>
  <li>
    <b>Dự án JobConnect Backend:</b> Xây dựng hệ thống tuyển dụng với Spring Boot, REST API, JWT, S3, chat real-time.<br/>
    Link: <a href="https://github.com/your-github/jobconnect-backend" target="_blank">https://github.com/your-github/jobconnect-backend</a>
  </li>
  <br/>
  <li>
    <b>Dự án E-commerce Backend:</b> API giỏ hàng, đơn hàng, thanh toán, quản lý sản phẩm, Node.js/Java.<br/>
    Link: <a href="https://github.com/your-github/ecommerce-backend" target="_blank">https://github.com/your-github/ecommerce-backend</a>
  </li>
</ul>
""";

        // ================== LOAD TEMPLATE TỪ FILE ==================
        String template = loadTemplate("templates/auto_resume_template.html");

        // Avatar HTML
        String avatarHtml = "";
        if (profile.getAvatar() != null && !profile.getAvatar().isEmpty()) {
            avatarHtml = "<img class=\"avatar\" src=\"" + escapeHtml(profile.getAvatar()) + "\" />";
        }

        // Thay placeholder trong template
        String html = template
                .replace("${AVATAR}", avatarHtml)
                .replace("${FIRST_NAME}", escapeHtml(profile.getFirstName()))
                .replace("${LAST_NAME}", escapeHtml(profile.getLastName()))
                .replace("${TITLE}", escapeHtml(title))
                .replace("${ADDRESS}", escapeHtml(profile.getAddress() != null ? profile.getAddress() : ""))
                .replace("${EMAIL}", escapeHtml(email))
                .replace("${PHONE}", escapeHtml(phone))
                .replace("${BIRTHDAY}", escapeHtml(birthDay))
                .replace("${SUMMARY}", summaryHtml)
                .replace("${CAREER_OBJECTIVE}", careerObjectiveHtml)
                .replace("${SKILL_ITEMS}", skillsHtml)
                .replace("${EXPERIENCE_ITEMS}", experiencesHtml)
                .replace("${EDUCATION}", educationHtml)
                .replace("${CERTIFICATIONS}", certificationsHtml)
                .replace("${PROJECTS}", projectsHtml);

        // ================== HTML -> PDF (CÓ FONT TIẾNG VIỆT) ==================
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();

        builder.useFastMode();
        builder.withHtmlContent(html, null);

        builder.useFont(
                () -> getClass().getResourceAsStream("/fonts/DejaVuSans.ttf"),
                "DejaVuSans"
        );

        builder.toStream(outputStream);

        try {
            builder.run();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF resume", e);
        }

        byte[] pdfData = outputStream.toByteArray();

        // ================== Upload PDF lên S3 ==================
        String s3Key = "resume_auto_" + resumeName + System.currentTimeMillis() + ".pdf";
        InputStream inputStream = new ByteArrayInputStream(pdfData);
        String s3Url = awsS3Service.uploadFileToS3(inputStream, s3Key, "application/pdf");

        // ================== Lưu database ==================
        Resume resume = Resume.builder()
                .jobSeekerProfile(profile)
                .resumeName(resumeName)
                .resumePath(s3Url)
                .uploadedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        resumeRepository.save(resume);
    }

    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            try (InputStream is = resource.getInputStream()) {
                byte[] bytes = is.readAllBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot load resume template: " + path, e);
        }
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

}
