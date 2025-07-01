package kr.soulware.teen_mydata.service;

import kr.soulware.teen_mydata.dto.request.ChatMessage;
import kr.soulware.teen_mydata.entity.ChatMessageEntity;
import kr.soulware.teen_mydata.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public void saveMessage(ChatMessage chatMessage) {
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setRoomId(chatMessage.getRoomId());
        entity.setSender(chatMessage.getSender());
        entity.setContent(chatMessage.getContent());
        entity.setType(chatMessage.getType().name());
        entity.setTimestamp(System.currentTimeMillis());
        chatMessageRepository.save(entity);
    }

    public List<ChatMessage> getMessages(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId)
            .stream()
            .map(entity -> {
                ChatMessage msg = new ChatMessage();
                msg.setRoomId(entity.getRoomId());
                msg.setSender(entity.getSender());
                msg.setContent(entity.getContent());
                msg.setType(ChatMessage.MessageType.valueOf(entity.getType()));
                return msg;
            })
            .collect(Collectors.toList());
    }
} 