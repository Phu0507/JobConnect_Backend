package com.jobconnect_backend.converters;

import com.jobconnect_backend.dto.response.AttachmentResponse;
import com.jobconnect_backend.entities.Attachment;
import com.jobconnect_backend.populators.AttachmentPopulator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AttachmentConverter {
    private final AttachmentPopulator attachmentPopulator;
    public AttachmentResponse convertToAttachmentDTO(Attachment attachment) {
        AttachmentResponse dto = new AttachmentResponse();
         attachmentPopulator.populate(attachment, dto);
         return dto;
    }
}
