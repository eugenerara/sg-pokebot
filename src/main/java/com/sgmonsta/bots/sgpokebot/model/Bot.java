package com.sgmonsta.bots.sgpokebot.model;

import com.sgmonsta.bots.sgpokebot.service.ShopeeCommandService;
import com.sgmonsta.bots.sgpokebot.service.UtilService;
import com.sgmonsta.bots.sgpokebot.util.Constant;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class Bot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final ShopeeCommandService shopeeCommandService;
    private final UtilService utilService;

    public Bot(ShopeeCommandService shopeeCommandService, UtilService utilService) {
        this.shopeeCommandService = shopeeCommandService;
        this.utilService = utilService;
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
            Integer messageThreadId = update.getMessage().getMessageThreadId(); // Get the messageThreadId


            if (messageText.startsWith("/shopee")) {
                shopeeCommandService.initialize(chatId, messageThreadId);
            } else if (messageText.startsWith("/getchatid")) {
                utilService.getChatId(messageThreadId);
            }
        } else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            String callbackData = update.getCallbackQuery().getData();

            shopeeCommandService.handleCallback(chatId, messageId, callbackData);
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }
}