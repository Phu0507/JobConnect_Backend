package com.jobconnect_backend.repositories;

import com.jobconnect_backend.entities.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    Attachment findByMessageId(Integer messageId);
}
