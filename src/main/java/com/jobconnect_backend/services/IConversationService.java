package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.response.ConversationResponse;

import java.util.List;

public interface IConversationService {
    ConversationResponse createConversation(Integer jobSeekerId, Integer employerId);
    List<ConversationResponse> getUserConversations(Integer userId);
    ConversationResponse getConversationById(Integer conversationId, Integer userId);
}
