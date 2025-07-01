package kr.soulware.teen_mydata.controller;

import kr.soulware.teen_mydata.dto.request.ChatMessage;
import kr.soulware.teen_mydata.service.AiChatService;
import kr.soulware.teen_mydata.service.ChatMessageService;
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

    private final AiChatService aiChatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendMessage.{roomId}")
    public void sendMessage(
        @DestinationVariable String roomId,
        @Payload ChatMessage chatMessage,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        String email = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("email");
        chatMessage.setSender(email);
        chatMessage.setRoomId(roomId);

        // 메시지 저장
        chatMessageService.saveMessage(chatMessage);

        if (roomId.startsWith("gpt_") && chatMessage.getType() == ChatMessage.MessageType.CHAT) {
            // 1. 사용자의 메시지 먼저 전송
            messagingTemplate.convertAndSend("/topic/room." + roomId, chatMessage);
            // 2. AI 답변을 스트리밍으로 chunk별 전송
            aiChatService.streamAiAnswer(chatMessage.getContent(), chunk -> {
                ChatMessage aiChunk = new ChatMessage();
                aiChunk.setType(ChatMessage.MessageType.CHAT);
                aiChunk.setSender("AI");
                aiChunk.setContent(chunk);
                aiChunk.setRoomId(roomId);
                // chunk별로 DB 저장
                chatMessageService.saveMessage(aiChunk);
                messagingTemplate.convertAndSend("/topic/room." + roomId, aiChunk);
            });
            return;
        }
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