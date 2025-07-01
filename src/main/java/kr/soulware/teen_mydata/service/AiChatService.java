package kr.soulware.teen_mydata.service;

import java.util.function.Consumer;

/**
 * AI 채팅 서비스 인터페이스
 * 다양한 AI 서비스(GPT, Claude, Gemini 등)를 추상화
 */
public interface AiChatService {

    /**
     * AI에게 메시지를 전송하고 스트리밍 응답을 받습니다.
     *
     * @param userMessage 사용자 메시지
     * @param onChunk     응답 청크를 처리할 콜백 함수
     */
    void streamAiAnswer(String userMessage, Consumer<String> onChunk);

}