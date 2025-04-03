package com.sgmonsta.bots.sgpokebot.service;

import com.sgmonsta.bots.sgpokebot.util.Constant;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Service
public class ResponseService {
    private final TelegramClient telegramClient;

    public ResponseService() {
        this.telegramClient = new OkHttpTelegramClient(Constant.POKEMON_BOT_TOKEN);
    }

    public Message sendMessage(SendMessage message) {
        try {
            return telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message sendPhoto(SendPhoto photo) {
        try {
            return telegramClient.execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendEditMessage(EditMessageText editMessage) {
        try {
            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(DeleteMessage deleteMessage) {
        try {
            telegramClient.execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public Message sendTextMessage(Long chatId, String text, Integer messageThreadId, List<MessageEntity> entities, InlineKeyboardMarkup replyKeyboardMarkup) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .messageThreadId(messageThreadId)
                .entities(entities)
                .replyMarkup(replyKeyboardMarkup)
                .build();
       return sendMessage(message);
    }

    public Message sendImageMessage(Long chatId, InputFile image, Integer messageThreadId,  String caption) {
        SendPhoto photo = SendPhoto.builder()
                .chatId(chatId.toString())
                .messageThreadId(messageThreadId)
                .photo(image)
                .caption(caption)
                .build();
        return sendPhoto(photo);
    }

    public void deleteExistingMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .build();

        deleteMessage(deleteMessage);
    }
}