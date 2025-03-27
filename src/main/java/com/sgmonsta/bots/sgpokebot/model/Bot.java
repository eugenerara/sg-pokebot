package com.sgmonsta.bots.sgpokebot.model;

import com.sgmonsta.bots.sgpokebot.service.ShopeeCommandService;
import com.sgmonsta.bots.sgpokebot.util.Constant;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class Bot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final ShopeeCommandService shopeeCommandService;
    private Integer lastMessageId;

    public Bot(ShopeeCommandService shopeeCommandService) {
        this.shopeeCommandService = shopeeCommandService;
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public String getBotToken() {
        return Constant.POKEMON_BOT_TOKEN;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/shopee")) {
                if (lastMessageId != null) {
                    EditMessageText closeMessage = shopeeCommandService.closePreviousKeyboard(chatId, lastMessageId);
                    try {
                        telegramClient.execute(closeMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

                SendMessage sendMessage = shopeeCommandService.initialize(chatId);

                try {
                    lastMessageId = telegramClient.execute(sendMessage).getMessageId();
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            String callbackData = update.getCallbackQuery().getData();

            EditMessageText editMessage = shopeeCommandService.handleCallback(chatId, messageId, callbackData);

            try {
                telegramClient.execute(editMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }
}