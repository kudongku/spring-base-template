package kr.soulware.teen_mydata.controller;

import kr.soulware.teen_mydata.dto.request.ChatMessage;
import kr.soulware.teen_mydata.service.ChatGptService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@RequiredArgsConstructor
@Controller
public class WebSocketController {

    private final ChatGptService chatGptService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage.{roomId}")
    public void sendMessage(
            @DestinationVariable String roomId,
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        String email = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("email");
        chatMessage.setSender(email);
        chatMessage.setRoomId(roomId);

        // AI 1:1 채팅방이면 사용자의 메시지와 AI 답변 모두 브로드캐스트
        if (roomId.startsWith("gpt_") && chatMessage.getType() == ChatMessage.MessageType.CHAT) {
            // 1. 사용자의 메시지 먼저 전송
            messagingTemplate.convertAndSend("/topic/room." + roomId, chatMessage);
            // 2. AI 답변 생성 및 전송
            String aiAnswer = chatGptService.askChatGpt(chatMessage.getContent());
            ChatMessage aiMessage = new ChatMessage();
            aiMessage.setType(ChatMessage.MessageType.CHAT);
            aiMessage.setSender("AI");
            aiMessage.setContent(aiAnswer);
            aiMessage.setRoomId(roomId);
            messagingTemplate.convertAndSend("/topic/room." + roomId, aiMessage);
            return;
        }

        // 유저간 1:1 채팅방은 기존대로 한 번만 전송
        messagingTemplate.convertAndSend("/topic/room." + roomId, chatMessage);
    }

    @MessageMapping("/chat.addUser.{roomId}")
    @SendTo("/topic/room.{roomId}")
    public ChatMessage addUser(
            @DestinationVariable String roomId,
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        String email = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("email");
        headerAccessor.getSessionAttributes().put("username", email);
        chatMessage.setSender(email);
        chatMessage.setRoomId(roomId);
        return chatMessage;
    }
} 