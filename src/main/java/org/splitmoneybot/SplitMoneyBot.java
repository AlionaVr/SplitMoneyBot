package org.splitmoneybot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.splitmoneybot.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@RequiredArgsConstructor
public class SplitMoneyBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String TGTOKEN;
    @Value("${telegram.bot.name}")
    private String TGNAME;

    private UserService userService;

    @Override
    public String getBotUsername() {
        return TGNAME;
    }

    @Override
    public String getBotToken() {
        return TGTOKEN;
    }

    // method for receiving messages from users
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();
            if (message.equals("/start")) {
                String botMessage = "Hello, I'm a bot for splitting expenses.";
                sendMsg(chatId, botMessage);
                //TODO
                //userService.registerOrUpdateUser();
            } else if (message.equals("/expense")) {
                String botMessage = "Please enter the expense amount.";
                sendMsg(chatId, botMessage);
                //TODO
                //expenseService.addExpense();
            } else {
                String botMessage = "Sorry, I don't understand.";
                sendMsg(chatId, botMessage);
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
