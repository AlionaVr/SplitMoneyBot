package org.splitmoneybot.components;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {
    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand("start", "Start the bot"),
            new BotCommand("help", "bot info"),
            new BotCommand("expense", "Add an expense"),
            new BotCommand("myexpenses", "Show your expenses")
    );
    String HELP_TEXT = "This bot will help to count your expenses. " +
            "The following commands are available to you:\n\n" +
            "/start - start the bot\n" +
            "/help - help menu";
}
