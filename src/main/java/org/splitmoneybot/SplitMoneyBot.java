package org.splitmoneybot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.splitmoneybot.entity.ConversationState;
import org.splitmoneybot.entity.State;
import org.splitmoneybot.service.ExpenseService;
import org.splitmoneybot.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@RequiredArgsConstructor
public class SplitMoneyBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String TGTOKEN;
    @Value("${telegram.bot.name}")
    private String TGNAME;

    private final UserService userService;
    private final ExpenseService expenseService;

    @Override
    public String getBotUsername() {
        return TGNAME;
    }

    @Override
    public String getBotToken() {
        return TGTOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();

            User tgUser = update.getMessage().getFrom();
            userService.registerOrUpdateUser(
                    tgUser.getId(),
                    tgUser.getUserName(),
                    tgUser.getFirstName(),
                    tgUser.getLastName());

            // check current state
            State currentState = expenseService.getCurrentState(chatId);

            switch (currentState) {
                case AWAITING_AMOUNT:
                    String amountResponse = expenseService.processAmount(chatId, message);
                    sendMsg(chatId, amountResponse);
                    return;

                case AWAITING_DESCRIPTION:
                    String descriptionResponse = expenseService.processDescription(chatId, message);
                    sendMsg(chatId, descriptionResponse);
                    return;

                case AWAITING_CURRENCY:
                    String currencyResponse = expenseService.processCurrency(chatId, message);
                    sendMsg(chatId, currencyResponse);
                    return;
                case IDLE:
                default:
                    if (message.equals("/start")) {
                        String botMessage = "Hello, I'm a bot for splitting expenses.";
                        sendMsg(chatId, botMessage);
                    } else if (message.equals("/expense")) {
                        expenseService.startExpenseCreation(chatId);
                        String botMessage = "Please enter the expense amount.";
                        sendMsg(chatId, botMessage);
                    } else if (message.equals("/myExpenses")) {
                        String botMessage = expenseService.showUserExpenses(chatId);
                        sendMsg(chatId, botMessage); }
                    else {
                        String botMessage = "Sorry, I don't understand.";
                        sendMsg(chatId, botMessage);
                    }
            }
        }
    }

    public void sendMsg(Long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .parseMode(ParseMode.MARKDOWN)
                .chatId(chatId)
                .text(message)
                .build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Exception during sending message: " + e.getMessage());
        }
    }
}
