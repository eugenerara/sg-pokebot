package com.sgmonsta.bots.sgpokebot.service;

import com.sgmonsta.bots.sgpokebot.util.Constant;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ShopeeCommandService {
    public SendMessage initialize(Long chatId) {
        // Create a list of keyboard rows
        List<InlineKeyboardRow> keyboard = new ArrayList<>();

        // Iterate over the constants map to create buttons
        for (Map.Entry<String, String> entry : Constant.SHOPEE_LINKS.entrySet()) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(entry.getKey())
                    .callbackData(entry.getValue())
                    .build();

            InlineKeyboardRow row = new InlineKeyboardRow();
            row.add(button);
            keyboard.add(row);
        }

        // Create the inline keyboard markup
        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();

        // Create a message with the inline keyboard
        return SendMessage.builder()
                .chatId(chatId)
                .text("Please select which item is currently available on Shopee: ")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

    public EditMessageText handleCallback(Long chatId, Integer messageId, String callbackData) {
        // Create a message to edit the original message and remove the keyboard
        return EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text("You selected: " + callbackData)
                .build();
    }

    public EditMessageText closePreviousKeyboard(Long chatId, Integer messageId) {
        // Create a message to edit the original message and remove the keyboard
        return EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text("Previous selection closed.")
                .build();
    }

    public EditMessageText timeoutKeyboard(Long chatId, Integer messageId) {
        // Create a message to edit the original message and remove the keyboard due to timeout
        return EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text("Selection timed out.")
                .build();
    }
}