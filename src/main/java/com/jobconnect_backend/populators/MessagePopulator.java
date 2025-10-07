package com.jobconnect_backend.populators;

import com.jobconnect_backend.converters.AttachmentConverter;
import com.jobconnect_backend.dto.response.MessageResponse;
import com.jobconnect_backend.entities.Attachment;
import com.jobconnect_backend.entities.Message;
import com.jobconnect_backend.repositories.AttachmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class MessagePopulator {
    private final AttachmentRepository attachmentRepository;
    private final AttachmentConverter attachmentConverter;
    public void populate(Message source, MessageResponse target) {
        target.setMessageId(source.getId());
        target.setSenderId(source.getSender().getUserId());
        target.setSenderName(source.getSender().getJobSeekerProfile() != null
                ? source.getSender().getJobSeekerProfile().getFirstName() + " " +
                source.getSender().getJobSeekerProfile().getLastName()
                : source.getSender().getCompany() != null
                ? source.getSender().getCompany().getCompanyName()
                : "Unknown");
        target.setContent(source.getContent());
        target.setSentAt(source.getSentAt());

        Attachment attachment = attachmentRepository.findByMessageId(source.getId());
        if (attachment != null) {
            target.setAttachment(attachmentConverter.convertToAttachmentDTO(attachment));
        } else {
            target.setAttachment(null);
        }

        target.setMessageType(source.getMessageType());
        target.setIsRead(source.getIsRead());
        target.setConversationId(source.getConversation().getId());
    }

}
