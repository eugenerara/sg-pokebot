package com.sgmonsta.bots.sgpokebot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UtilService {

    public void getChatId(Integer messageThreadId) {
        log.info("Getting topic id {}", messageThreadId);
    }
}

