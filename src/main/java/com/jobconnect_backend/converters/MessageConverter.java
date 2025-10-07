package com.jobconnect_backend.converters;

import com.jobconnect_backend.dto.response.MessageResponse;
import com.jobconnect_backend.entities.Message;
import com.jobconnect_backend.populators.MessagePopulator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
@AllArgsConstructor
@Component
public class MessageConverter {
    private final MessagePopulator messagePopulator;

    public MessageResponse convertToMessageResponse(Message source) {
        MessageResponse target = new MessageResponse();
        messagePopulator.populate(source, target);
        return target;
    }
}

