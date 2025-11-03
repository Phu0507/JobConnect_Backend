package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.MessageConverter;
import com.jobconnect_backend.defaults.DefaultValue;
import com.jobconnect_backend.dto.request.SendTextMessageRequest;
import com.jobconnect_backend.dto.response.ConversationMeta;
import com.jobconnect_backend.dto.response.ConversationResponse;
import com.jobconnect_backend.dto.response.MessageResponse;
import com.jobconnect_backend.entities.Conversation;
import com.jobconnect_backend.entities.Message;
import com.jobconnect_backend.entities.User;
import com.jobconnect_backend.entities.enums.MessageType;
import com.jobconnect_backend.entities.enums.Role;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements IConversationService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final MessageConverter messageConverter;
    private final SimpMessagingTemplate messagingTemplate;

    //tạo mới cuộc trò chuyện (conversation) giữa ứng viên và nhà tuyển dụng
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

    //danh sách các cuộc trò chuyện của người dùng
    @Override
    public List<ConversationResponse> getUserConversations(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        List<Conversation> conversations;

        if (user.getRole() == Role.JOBSEEKER) {
            conversations = conversationRepository.findByJobSeeker(user);
        } else if (user.getRole() == Role.COMPANY) {
            conversations = conversationRepository.findByCompany(user);
        } else {
            throw new BadRequestException("Unsupported role");
        }

        return conversations.stream().map(conversation -> {
            User otherUser = conversation.getJobSeeker().getUserId().equals(userId)
                    ? conversation.getCompany()
                    : conversation.getJobSeeker();

            Message lastMessage = messageRepository.findTopByConversationIdOrderBySentAtDesc(conversation.getId());

            //đếm số tin nhắn chưa đọc
            int unreadCount = conversation.getJobSeeker().getUserId().equals(userId)
                    ? conversation.getUnreadCountJobSeeker()
                    : conversation.getUnreadCountCompany();

            return ConversationResponse.builder()
                    .conversationId(conversation.getId())
                    .createdAt(conversation.getCreateAt())
                    .lastMessageAt(conversation.getLastMessageAt())
                    .lastMessage(lastMessage != null ? lastMessage.getContent() : "")
                    .senderId(lastMessage != null ? lastMessage.getSender().getUserId() : null)
                    .roleId(otherUser.getRole() == Role.JOBSEEKER ? otherUser.getJobSeekerProfile().getProfileId() : otherUser.getCompany().getCompanyId())
                    .senderName(otherUser.getRole() == Role.JOBSEEKER ? otherUser.getJobSeekerProfile().getFirstName() + " " + otherUser.getJobSeekerProfile().getLastName() : otherUser.getCompany().getCompanyName())
                    .senderAvatar(otherUser.getRole() == Role.JOBSEEKER ? otherUser.getJobSeekerProfile().getAvatar() : otherUser.getCompany().getLogoPath())
                    .unreadCount(unreadCount)
                    .build();
        }).toList();
    }

    //để lấy thông tin chi tiết của một cuộc trò chuyện
    @Override
    public ConversationResponse getConversationById(Integer conversationId, Integer userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BadRequestException("Conversation not found"));

        User jobSeeker = conversation.getJobSeeker();
        User company = conversation.getCompany();

        boolean isCurrentUserJobSeeker = jobSeeker.getUserId().equals(userId);
        User otherUser = isCurrentUserJobSeeker ? company : jobSeeker;
        Message lastMessage = messageRepository.findTopByConversationIdOrderBySentAtDesc(conversation.getId());
        int unreadCount = conversation.getJobSeeker().getUserId().equals(userId)
                ? conversation.getUnreadCountJobSeeker()
                : conversation.getUnreadCountCompany();

        return ConversationResponse.builder()
                .conversationId(conversation.getId())
                .createdAt(conversation.getCreateAt())
                .lastMessageAt(conversation.getLastMessageAt())
                .lastMessage(lastMessage != null ? lastMessage.getContent() : "")
                .senderId(otherUser.getUserId())
                .roleId(otherUser.getRole() == Role.JOBSEEKER ? otherUser.getJobSeekerProfile().getProfileId() : otherUser.getCompany().getCompanyId())
                .senderName(otherUser.getCompany() != null
                        ? otherUser.getCompany().getCompanyName()
                        : otherUser.getJobSeekerProfile().getFirstName() + " " + otherUser.getJobSeekerProfile().getLastName())
                .senderAvatar(otherUser.getCompany() != null
                        ? otherUser.getCompany().getLogoPath()
                        : otherUser.getJobSeekerProfile().getAvatar())
                .unreadCount(unreadCount)
                .build();
    }

    @Override
    public ConversationMeta getConversationMetaById(Integer conversationId, Integer userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BadRequestException("Conversation not found"));
        User jobSeeker = conversation.getJobSeeker();
        User company = conversation.getCompany();

        boolean isCurrentUserJobSeeker = jobSeeker.getUserId().equals(userId);
        User otherUser = isCurrentUserJobSeeker ? company : jobSeeker;
        int unreadCount = isCurrentUserJobSeeker
                ? conversation.getUnreadCountJobSeeker()
                : conversation.getUnreadCountCompany();

        return ConversationMeta.builder()
                .conversationId(conversation.getId())
                .senderId(otherUser.getUserId())
                .roleId(otherUser.getRole() == Role.JOBSEEKER ? otherUser.getJobSeekerProfile().getProfileId() : otherUser.getCompany().getCompanyId())
                .senderName(otherUser.getCompany() != null
                        ? otherUser.getCompany().getCompanyName()
                        : otherUser.getJobSeekerProfile().getFirstName() + " " + otherUser.getJobSeekerProfile().getLastName())
                .senderAvatar(otherUser.getCompany() != null
                        ? otherUser.getCompany().getLogoPath()
                        : otherUser.getJobSeekerProfile().getAvatar())
                .unreadCount(unreadCount)
                .build();
    }

    @Override
    public MessageResponse sendTextMessage(SendTextMessageRequest sendTextMessageRequest) {
        Conversation conversation = conversationRepository.findById(sendTextMessageRequest.getConversationId()).orElseThrow();
        Message message = Message.builder()
                .conversation(conversation)
                .sender(userRepository.findById(sendTextMessageRequest.getSenderId()).orElseThrow(() -> new BadRequestException("User not found")))
                .content(sendTextMessageRequest.getContent())
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .messageType(MessageType.TEXT)
                .build();

        messageRepository.save(message);

        Integer recipientId = conversation.getJobSeeker().getUserId().equals(sendTextMessageRequest.getSenderId())
                ? conversation.getCompany().getUserId()
                : conversation.getJobSeeker().getUserId();

        if (conversation.getJobSeeker().getUserId().equals(sendTextMessageRequest.getSenderId())) {
            conversation.setUnreadCountCompany(conversation.getUnreadCountCompany() + 1);
        } else {
            conversation.setUnreadCountJobSeeker(conversation.getUnreadCountJobSeeker() + 1);
        }

        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);
//        ConversationMeta meta = getConversationMetaById(conversation.getId(), sendTextMessageRequest.getSenderId());

        Long totalUnread = conversationRepository.countUnreadConversations(recipientId);
        messagingTemplate.convertAndSend(DefaultValue.WS_TOPIC_COUNT_UNREAD + recipientId, totalUnread);
//        messagingTemplate.convertAndSend(JobFindConstant.WS_TOPIC_DATA_CONVERSATION + recipientId, meta);
//        messagingTemplate.convertAndSend(JobFindConstant.WS_TOPIC_DATA_CONVERSATION + conversation.getId(), meta);
        return messageConverter.convertToMessageResponse(message);
    }
}
