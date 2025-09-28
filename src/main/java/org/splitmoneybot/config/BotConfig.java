package org.splitmoneybot.config;

import lombok.Data;
import org.splitmoneybot.SplitMoneyBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {

    private final SplitMoneyBot tgBot;
    @Value("${telegram.bot.token}")
    private String TGTOKEN;
    @Value("${telegram.bot.name}")
    private String TGNAME;

    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(tgBot);
        } catch (TelegramApiException e) {

        }
    }
}
