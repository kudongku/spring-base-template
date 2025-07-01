package kr.soulware.teen_mydata.repository;

import kr.soulware.teen_mydata.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);
}
