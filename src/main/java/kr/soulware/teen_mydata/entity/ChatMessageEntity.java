package kr.soulware.teen_mydata.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "chat_message")
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String type; // CHAT, JOIN, LEAVE 등

    @Column(nullable = false)
    private Long timestamp; // System.currentTimeMillis() 등
} 