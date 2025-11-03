package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.MessageConverter;
import com.jobconnect_backend.dto.response.ConversationResponse;
import com.jobconnect_backend.entities.Conversation;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.AttachmentRepository;
import com.jobconnect_backend.repositories.ConversationRepository;
import com.jobconnect_backend.repositories.MessageRepository;
import com.jobconnect_backend.repositories.UserRepository;
import com.jobconnect_backend.services.IConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements IConversationService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final MessageConverter messageConverter;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public ConversationResponse createConversation(Integer jobSeekerId, Integer employerId) {
        Conversation conversation = Conversation.builder()
                .jobSeeker(userRepository.findById(jobSeekerId).orElseThrow(() -> new BadRequestException("User not found")))
                .company(userRepository.findById(employerId).orElseThrow(() -> new BadRequestException("User not found")))
                .createAt(LocalDateTime.now())
                .lastMessageAt(LocalDateTime.now())
                .unreadCountJobSeeker(0)
                .unreadCountCompany(0)
                .build();

        Conversation savedConversation = conversationRepository.save(conversation);

        return ConversationResponse.builder()
                .conversationId(savedConversation.getId())
                .createdAt(savedConversation.getCreateAt())
                .lastMessageAt(savedConversation.getLastMessageAt())
                .lastMessage("")
                .unreadCount(0)
                .build();
    }
}
