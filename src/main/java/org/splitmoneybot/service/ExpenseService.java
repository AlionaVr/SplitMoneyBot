package org.splitmoneybot.service;

import lombok.RequiredArgsConstructor;
import org.splitmoneybot.entity.*;
import org.splitmoneybot.repository.ConversationStateRepository;
import org.splitmoneybot.repository.ExpenseRepository;
import org.splitmoneybot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

            ConversationState conversation = checkAndGetConversationState(chatId);

            conversation.setTempAmount(amount);
            conversation.setState(State.AWAITING_DESCRIPTION);
            conversationStateRepository.save(conversation);

            return "Amount recorded: " + amount;
        } catch (NumberFormatException e) {
            return "Invalid amount format. Please enter a valid number:";
        }
    }

    public String processDescription(Long chatId, String description) {
        ConversationState conversation = checkAndGetConversationState(chatId);

        conversation.setTempDescription(description);
        conversation.setState(State.AWAITING_CURRENCY);
        conversationStateRepository.save(conversation);
        return "Description recorded: " + description;
    }

    public String processCurrency(Long chatId, String currencyString) {
        ConversationState conversation = checkAndGetConversationState(chatId);
        if (!currencyString.equalsIgnoreCase("skip")) {
            AppCurrency currency = AppCurrency.valueOf(currencyString.toUpperCase());
            conversation.setTempCurrency(currency);
        }
        AppUser user = getUserByChatId(chatId);

        Expense expense = Expense.builder()
                .paidBy(user).amount(conversation.getTempAmount())
                .description(conversation.getTempDescription())
                .currency(conversation.getTempCurrency())
                .createdAt(LocalDateTime.now())
                .build();

        expenseRepository.save(expense);

        conversationStateRepository.delete(conversation);

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

    private ConversationState checkAndGetConversationState(Long chatId) {
        return conversationStateRepository
                .findByChatId(chatId)
                .orElseThrow(() -> new IllegalStateException("Conversation state not found"));
    }

    private AppUser getUserByChatId(Long chatId) {
        return userRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalStateException("User not found for chatId: " + chatId));
    }

    public String showUserExpenses(Long chatId) {
        AppUser user = getUserByChatId(chatId);
        List<Expense> userExpenses = expenseRepository.findAllByPaidBy(user);

        if (userExpenses.isEmpty()) {
            return "You don't have any expenses yet.\nUse /expense to create one.";
        }

        StringBuilder message = new StringBuilder("Your expenses:\n\n");
        double totalAmount = 0;

        for (int i = 0; i < userExpenses.size(); i++) {
            Expense expense = userExpenses.get(i);
            message.append(String.format("%d. %.2f %s - %s\n",
                    i + 1,
                    expense.getAmount(),
                    expense.getCurrency(),
                    expense.getDescription()));
            totalAmount += expense.getAmount();
        }

        message.append(String.format("Total: %.2f", totalAmount));

        return message.toString();

    }
}
