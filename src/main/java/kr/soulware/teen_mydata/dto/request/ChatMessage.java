package kr.soulware.teen_mydata.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    public enum MessageType { CHAT, JOIN, LEAVE }
    private MessageType type;
    private String content;
    private String sender; // JWT에서 추출
    private String roomId; // 1:1 채팅방 ID (유저간: userA_userB, AI: gpt_userA)
} 