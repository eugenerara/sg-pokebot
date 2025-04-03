package com.sgmonsta.bots.sgpokebot.service;

import com.google.zxing.WriterException;
import com.sgmonsta.bots.sgpokebot.util.Constant;
import com.sgmonsta.bots.sgpokebot.util.QRCodeGenerator;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sgmonsta.bots.sgpokebot.util.Constant.SHOPEE_CHAT_ID;

@Service
public class ShopeeCommandService {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ResponseService responseService;

    public ShopeeCommandService(ResponseService responseService) {
        this.responseService = responseService;
    }

    public Integer initialize(Long chatId, Integer messageThreadId) {
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

        // Send the message and get the message ID
        Integer messageId = responseService.sendTextMessage(chatId, "Please select which item is currently available on Shopee: ", messageThreadId, null, inlineKeyboardMarkup).getMessageId();

        // Schedule the timeout to close the keyboard after 1 minute
        scheduler.schedule(() -> timeoutKeyboard(chatId, messageId), 1, TimeUnit.MINUTES);

        return messageId;
    }

    public void handleCallback(Long chatId, Integer existingMessageId, String callbackData) {
        try {
            // Generate QR code image
            BufferedImage qrImage = QRCodeGenerator.generateQRCodeImage(callbackData);

            // Convert BufferedImage to InputFile
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", os);
            InputFile inputFile = new InputFile(new ByteArrayInputStream(os.toByteArray()), "qr.png");

            // Send the QR code image
            Integer imageMessageId = responseService.sendImageMessage(chatId, inputFile, SHOPEE_CHAT_ID, "QR Code has been generated.").getMessageId();
            scheduler.schedule(() -> responseService.deleteExistingMessage(chatId, imageMessageId), 1, TimeUnit.MINUTES);

        } catch (WriterException | IOException e) {
            e.printStackTrace();
        } finally {
            MessageEntity messageEntityBuilder = MessageEntity.builder().offset(13).length(callbackData.length()).type("spoiler").build();
            Integer messageId = responseService.sendTextMessage(chatId, "Shopee Link: " + callbackData, SHOPEE_CHAT_ID, List.of(messageEntityBuilder), null).getMessageId();
            scheduler.schedule(() -> responseService.deleteExistingMessage(chatId, messageId), 1, TimeUnit.MINUTES);
            responseService.deleteExistingMessage(chatId, existingMessageId);
        }
    }


    public void timeoutKeyboard(Long chatId, Integer messageId) {
        // Create a message to edit the original message and remove the keyboard due to timeout
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text("Selection timed out.")
                .build();

        responseService.sendEditMessage(editMessage);
    }

}