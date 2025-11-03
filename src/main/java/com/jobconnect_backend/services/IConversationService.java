package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.request.SendTextMessageRequest;
import com.jobconnect_backend.dto.response.ConversationMeta;
import com.jobconnect_backend.dto.response.ConversationResponse;
import com.jobconnect_backend.dto.response.MessageResponse;

import java.util.List;

public interface IConversationService {
    ConversationResponse createConversation(Integer jobSeekerId, Integer employerId);
    List<ConversationResponse> getUserConversations(Integer userId);
    ConversationResponse getConversationById(Integer conversationId, Integer userId);
    ConversationMeta getConversationMetaById(Integer conversationId, Integer userId);
    MessageResponse sendTextMessage(SendTextMessageRequest sendTextMessageRequest);
}
