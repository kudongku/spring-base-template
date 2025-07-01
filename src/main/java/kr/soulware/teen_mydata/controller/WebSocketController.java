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

/**
 * WebSocket 메시지 처리를 담당하는 컨트롤러
 * 이 클래스는 STOMP 프로토콜을 통해 실시간 채팅과 AI 대화 기능을 제공합니다.
 * 클라이언트로부터 받은 메시지를 처리하고, 적절한 응답을 전송합니다.
 * 주요 기능:
 * - 실시간 채팅 메시지 처리
 * - AI 챗봇과의 대화 처리 (스트리밍 방식)
 * - 채팅방 참여자 관리
 * - 메시지 저장 및 브로드캐스트
 */
@RequiredArgsConstructor
@Controller
public class WebSocketController {

    /**
     * AI 챗봇 서비스 - GPT API와의 통신 및 스트리밍 응답 처리
     */
    private final AiChatService aiChatService;

    /**
     * STOMP 메시지 전송을 위한 템플릿
     * 특정 사용자나 채팅방에 메시지를 전송할 때 사용
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 채팅 메시지 저장 및 관리 서비스
     */
    private final ChatMessageService chatMessageService;

    /**
     * 채팅 메시지 전송 처리
     * 클라이언트가 채팅방에 메시지를 보낼 때 호출됩니다.
     * 일반 채팅과 AI 채팅을 구분하여 처리합니다.
     *
     * @param roomId         채팅방 ID (예: "room1", "gpt_room1")
     * @param chatMessage    전송할 채팅 메시지 객체
     * @param headerAccessor WebSocket 세션 정보 접근자 (사용자 인증 정보 포함)
     */
    @MessageMapping("/chat.sendMessage.{roomId}")  // 클라이언트에서 보내는 메시지 경로
    public void sendMessage(
        @DestinationVariable String roomId,  // URL 경로에서 roomId 추출
        @Payload ChatMessage chatMessage,    // 메시지 본문
        SimpMessageHeaderAccessor headerAccessor  // 세션 정보
    ) {
        // 1단계: 사용자 인증 정보 추출 및 메시지 설정
        String email = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("email");
        chatMessage.setSender(email);    // 메시지 발신자 설정
        chatMessage.setRoomId(roomId);   // 채팅방 ID 설정

        // 2단계: 메시지를 데이터베이스에 저장
        chatMessageService.saveMessage(chatMessage);

        // 3단계: AI 채팅방인지 확인하여 분기 처리
        if (roomId.startsWith("gpt_") && chatMessage.getType() == ChatMessage.MessageType.CHAT) {
            // AI 채팅방 처리 (스트리밍 방식)

            // 3-1: 사용자 메시지를 먼저 채팅방에 브로드캐스트
            messagingTemplate.convertAndSend("/topic/room." + roomId, chatMessage);

            // 3-2: AI 답변을 스트리밍으로 청크(chunk)별 전송
            aiChatService.streamAiAnswer(chatMessage.getContent(), chunk -> {
                // AI 응답 메시지 객체 생성
                ChatMessage aiChunk = new ChatMessage();
                aiChunk.setType(ChatMessage.MessageType.CHAT);
                aiChunk.setSender("AI");
                aiChunk.setContent(chunk);
                aiChunk.setRoomId(roomId);

                // AI 응답 청크를 데이터베이스에 저장
                chatMessageService.saveMessage(aiChunk);

                // AI 응답 청크를 채팅방에 브로드캐스트
                messagingTemplate.convertAndSend("/topic/room." + roomId, aiChunk);
            });
            return;  // AI 채팅 처리 완료 후 메서드 종료
        }

        // 4단계: 일반 채팅방 처리 - 메시지를 채팅방에 브로드캐스트
        messagingTemplate.convertAndSend("/topic/room." + roomId, chatMessage);
    }

    /**
     * 채팅방 참여자 추가 처리
     * 사용자가 채팅방에 입장할 때 호출됩니다.
     * 입장 메시지를 생성하고 채팅방의 모든 참여자에게 알립니다.
     *
     * @param roomId         채팅방 ID
     * @param chatMessage    입장 메시지 객체
     * @param headerAccessor WebSocket 세션 정보 접근자
     * @return 입장 알림 메시지 (자동으로 채팅방에 브로드캐스트됨)
     */
    @MessageMapping("/chat.addUser.{roomId}")  // 클라이언트에서 보내는 입장 메시지 경로
    @SendTo("/topic/room.{roomId}")           // 응답을 자동으로 채팅방에 브로드캐스트
    public ChatMessage addUser(
        @DestinationVariable String roomId,    // URL 경로에서 roomId 추출
        @Payload ChatMessage chatMessage,      // 입장 메시지
        SimpMessageHeaderAccessor headerAccessor  // 세션 정보
    ) {
        // 1단계: 사용자 인증 정보 추출
        String email = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("email");

        // 2단계: 세션에 사용자명 저장 (향후 메시지 처리 시 사용)
        headerAccessor.getSessionAttributes().put("username", email);

        // 3단계: 입장 메시지 설정
        chatMessage.setSender(email);    // 입장한 사용자 정보 설정
        chatMessage.setRoomId(roomId);   // 채팅방 ID 설정

        // 4단계: 입장 메시지 반환 (자동으로 채팅방에 브로드캐스트됨)
        return chatMessage;
    }
} 