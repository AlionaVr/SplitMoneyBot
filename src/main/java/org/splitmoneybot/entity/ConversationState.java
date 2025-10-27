package org.splitmoneybot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "conversation_states")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long chatId;

    @Enumerated(EnumType.STRING)
    private State state;

    // temporary data storage
    private Double tempAmount;
    private String tempDescription;

    @Enumerated(EnumType.STRING)
    private AppCurrency tempCurrency;

}
