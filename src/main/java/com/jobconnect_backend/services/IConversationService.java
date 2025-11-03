package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.response.ConversationResponse;

public interface IConversationService {
    ConversationResponse createConversation(Integer jobSeekerId, Integer employerId);
}
