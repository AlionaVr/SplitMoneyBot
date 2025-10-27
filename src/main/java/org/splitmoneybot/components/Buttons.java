package org.splitmoneybot.components;

import org.splitmoneybot.entity.AppCurrency;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class Buttons {
    public static final KeyboardButton ADD_EXPENSE = new KeyboardButton("Add expense");
    public static final KeyboardButton MY_EXPENSES = new KeyboardButton("My expenses");
    public static final KeyboardButton HELP = new KeyboardButton("Help");

    public static ReplyKeyboardMarkup getMainMenuKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(ADD_EXPENSE);
        row1.add(MY_EXPENSES);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(HELP);

        keyboard.add(row1);
        keyboard.add(row2);

        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        return markup;
    }

    public static InlineKeyboardMarkup getCurrencyKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        for (AppCurrency currency : AppCurrency.values()) {
            row.add(InlineKeyboardButton.builder()
                    .text(currency.toString())
                    .callbackData(currency.toString())
                    .build());
        }
        keyboard.add(row);
        markup.setKeyboard(keyboard);
        return markup;
    }
}
