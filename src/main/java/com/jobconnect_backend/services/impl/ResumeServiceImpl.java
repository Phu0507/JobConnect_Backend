package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.config.AwsS3Service;
import com.jobconnect_backend.dto.request.ResumeRequest;
import com.jobconnect_backend.entities.JobSeekerProfile;
import com.jobconnect_backend.entities.Resume;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.JobSeekerProfileRepository;
import com.jobconnect_backend.repositories.ResumeRepository;
import com.jobconnect_backend.services.IResumeService;
import com.jobconnect_backend.utils.ValidateField;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Map;

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

        boolean isExist = resumeRepository.existsByResumeNameAndJobSeekerProfileProfileIdAndDeletedIsFalse(
                resumeName, profile.getProfileId());

        if (isExist) {
            throw new BadRequestException("Resume name has been used");
        }

        //HTML template tự tạo (dùng dữ liệu từ profile)
        String html = """
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; padding: 20px; }
                h1 { color: #333; margin-bottom: 5px; }
            </style>
        </head>
        <body>
            <h1>%s %s</h1>
            <div><b>Title:</b> %s</div>
            <div><b>Address:</b> %s</div>
        </body>
        </html>
        """.formatted(
                profile.getFirstName(),
                profile.getLastName(),
                profile.getTitle(),
                profile.getAddress()
        );

        //Convert HTML → PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();

        builder.useFastMode();
        builder.withHtmlContent(html, null);
        builder.toStream(outputStream);

        try {
            builder.run();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF resume", e);
        }

        byte[] pdfData = outputStream.toByteArray();

        //Upload PDF lên S3
        String s3Key = "resume_auto_" + resumeName + System.currentTimeMillis() + ".pdf";
        InputStream inputStream = new ByteArrayInputStream(pdfData);

        String s3Url = awsS3Service.uploadFileToS3(inputStream, s3Key, "application/pdf");

        //Lưu database
        Resume resume = Resume.builder()
                .jobSeekerProfile(profile)
                .resumeName(resumeName)
                .resumePath(s3Url)
                .uploadedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        resumeRepository.save(resume);
    }

}
