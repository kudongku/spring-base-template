package kr.soulware.teen_mydata.controller;

import kr.soulware.teen_mydata.dto.request.ChatMessage;
import kr.soulware.teen_mydata.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatHistoryController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/history/{roomId}")
    public List<ChatMessage> getChatHistory(@PathVariable String roomId) {
        return chatMessageService.getMessages(roomId);
    }

}
