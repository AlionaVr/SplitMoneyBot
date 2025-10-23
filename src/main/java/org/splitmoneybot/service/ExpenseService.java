package org.splitmoneybot.service;

import lombok.RequiredArgsConstructor;
import org.splitmoneybot.entity.AppUser;
import org.splitmoneybot.entity.ConversationState;
import org.splitmoneybot.entity.Expense;
import org.splitmoneybot.entity.State;
import org.splitmoneybot.repository.ConversationStateRepository;
import org.splitmoneybot.repository.ExpenseRepository;
import org.splitmoneybot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ConversationStateRepository conversationStateRepository;
    private final UserRepository userRepository;

    public void startExpenseCreation(Long chatId) {
        ConversationState conversation = conversationStateRepository
                .findByChatId(chatId)
                .orElse(ConversationState.builder()
                        .chatId(chatId)
                        .build());

        conversation.setState(State.AWAITING_AMOUNT);

        conversationStateRepository.save(conversation);
    }

    public String processAmount(Long chatId, String amountText) {
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                return "Amount must be greater than 0. Please enter a valid amount:";
            }

            ConversationState conversation = conversationStateRepository
                    .findByChatId(chatId)
                    .orElseThrow(() -> new IllegalStateException("Conversation state not found"));

            conversation.setTempAmount(amount);
            conversation.setState(State.AWAITING_DESCRIPTION);
            conversationStateRepository.save(conversation);

            return "Amount recorded: " + amount + "\nPlease enter a description for this expense:";
        } catch (NumberFormatException e) {
            return "Invalid amount format. Please enter a valid number:";
        }
    }

    public String processDescription(Long chatId, String description) {
        ConversationState conversation = conversationStateRepository
                .findByChatId(chatId)
                .orElseThrow(() -> new IllegalStateException("Conversation state not found"));

        conversation.setTempDescription(description);
        conversation.setState(State.AWAITING_CURRENCY);
        conversationStateRepository.save(conversation);
        return "Description recorded: " + description +
                "\nPlease enter currency (or type 'skip' to use USD):";
    }

    public String processCurrency(Long chatId, String currency) {
        ConversationState conversation = conversationStateRepository
                .findByChatId(chatId)
                .orElseThrow(() -> new IllegalStateException("Conversation state not found"));
        if (!currency.equalsIgnoreCase("skip")) {
            conversation.setTempCurrency(currency.toUpperCase());
        }
        AppUser user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Expense expense = new Expense();
        expense.setPaidBy(user);
        expense.setAmount(conversation.getTempAmount());
        expense.setDescription(conversation.getTempDescription());
        expense.setCurrency(conversation.getTempCurrency());
        expense.setCreatedAt(java.time.LocalDateTime.now());

        expenseRepository.save(expense);

        // Сбрасываем состояние
        conversation.setState(State.IDLE);
        conversation.setTempAmount(null);
        conversation.setTempDescription(null);
        conversation.setTempCurrency(null);

        conversationStateRepository.save(conversation);

        return String.format("Expense created successfully!\n\n" +
                        "Amount: %.2f %s\n" +
                        "Description: %s\n" +
                        "Paid by: %s",
                expense.getAmount(),
                expense.getCurrency(),
                expense.getDescription(),
                user.getFirstName() != null ? user.getFirstName() : user.getUsername());
    }

    public State getCurrentState(Long chatId) {
        return conversationStateRepository
                .findByChatId(chatId)
                .map(ConversationState::getState)
                .orElse(State.IDLE);
    }
}
