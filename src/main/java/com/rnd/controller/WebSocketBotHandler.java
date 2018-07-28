package com.rnd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rnd.domain.Message;
import com.rnd.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketBotHandler extends TextWebSocketHandler {

    @Autowired
    private TopicService topicService;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        Message message = mapper.readValue(textMessage.getPayload(), Message.class);
        final String linkTopic = topicService.getNextTopic(message.getUserLogin());
        message.setContent(linkTopic);
        session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
    }
}
