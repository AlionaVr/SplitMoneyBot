package org.splitmoneybot.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.splitmoneybot.SplitMoneyBot;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import static org.splitmoneybot.components.BotCommands.LIST_OF_COMMANDS;

@Configuration
@Data
@Slf4j
public class BotConfig {

    private final SplitMoneyBot tgBot;

    @EventListener(ContextRefreshedEvent.class)
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(tgBot);
            log.info("Bot registered");
            tgBot.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
            log.info("Bot commands registered");
        } catch (TelegramApiException e) {
            log.error("Exception during bot registration: " + e.getMessage());
        }
    }
}
