package kr.soulware.teen_mydata.service;

import kr.soulware.teen_mydata.entity.ChatMessage;
import kr.soulware.teen_mydata.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public void saveMessage(kr.soulware.teen_mydata.dto.request.ChatMessage chatMessage) {
        ChatMessage entity = new ChatMessage();
        entity.setRoomId(chatMessage.getRoomId());
        entity.setSender(chatMessage.getSender());
        entity.setContent(chatMessage.getContent());
        entity.setType(chatMessage.getType().name());
        entity.setTimestamp(System.currentTimeMillis());
        chatMessageRepository.save(entity);
    }

    public List<kr.soulware.teen_mydata.dto.request.ChatMessage> getMessages(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId)
            .stream()
            .map(entity -> {
                kr.soulware.teen_mydata.dto.request.ChatMessage msg = new kr.soulware.teen_mydata.dto.request.ChatMessage();
                msg.setRoomId(entity.getRoomId());
                msg.setSender(entity.getSender());
                msg.setContent(entity.getContent());
                msg.setType(kr.soulware.teen_mydata.dto.request.ChatMessage.MessageType.valueOf(entity.getType()));
                return msg;
            })
            .collect(Collectors.toList());
    }
} 