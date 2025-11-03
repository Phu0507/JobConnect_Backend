package com.jobconnect_backend.controllers;

import com.jobconnect_backend.defaults.DefaultValue;
import com.jobconnect_backend.dto.request.SendFileMessageRequest;
import com.jobconnect_backend.dto.request.SendTextMessageRequest;
import com.jobconnect_backend.dto.response.ConversationResponse;
import com.jobconnect_backend.dto.response.MessageResponse;
import com.jobconnect_backend.services.IConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ConversationController {
    @Autowired
    private IConversationService conversationServiceImpl;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/conversation")
    public ResponseEntity<ConversationResponse> createConversation(@RequestParam Integer jobSeekerId, @RequestParam Integer employerId) {
        ConversationResponse conversation = conversationServiceImpl.createConversation(jobSeekerId, employerId);
        messagingTemplate.convertAndSend(DefaultValue.WS_TOPIC_DATA_CONVERSATION + jobSeekerId, conversation);
        messagingTemplate.convertAndSend(DefaultValue.WS_TOPIC_DATA_CONVERSATION + employerId, conversation);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<ConversationResponse>> getUserConversations(@PathVariable Integer userId) {
        List<ConversationResponse> conversations = conversationServiceImpl.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<ConversationResponse> getConversation(
            @PathVariable Integer conversationId,
            @RequestParam Integer userId) {

        ConversationResponse conversation = conversationServiceImpl.getConversationById(conversationId, userId);
        return ResponseEntity.ok(conversation);
    }

    @MessageMapping("/message/text")
    public void sendTextMessage(@RequestBody SendTextMessageRequest request) {
        MessageResponse message = conversationServiceImpl.sendTextMessage(request);
        messagingTemplate.convertAndSend(DefaultValue.WS_TOPIC_CONVERSATION + request.getConversationId(), message);
    }

    @MessageMapping("/message/file")
    public void sendFileMessage(@RequestBody SendFileMessageRequest request) {
        MessageResponse message = conversationServiceImpl.sendFileMessage(request);
        messagingTemplate.convertAndSend(DefaultValue.WS_TOPIC_CONVERSATION + request.getConversationId(), message);
    }
}
