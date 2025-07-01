package kr.soulware.teen_mydata.controller;

import kr.soulware.teen_mydata.dto.request.ChatMessage;
import kr.soulware.teen_mydata.service.ChatGptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@RequiredArgsConstructor
@Controller
public class WebSocketController {

    private ChatGptService chatGptService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getType() == ChatMessage.MessageType.CHAT) {
            String aiAnswer = chatGptService.askChatGpt(chatMessage.getContent());
            ChatMessage aiMessage = new ChatMessage();
            aiMessage.setType(ChatMessage.MessageType.CHAT);
            aiMessage.setSender("AI");
            aiMessage.setContent(aiAnswer);
            return aiMessage;
        }

        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", chatMessage.getSender());
        return chatMessage;
    }
} 