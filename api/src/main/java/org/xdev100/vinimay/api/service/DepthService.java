package org.xdev100.vinimay.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xdev100.vinimay.api.model.MessageFromOrderBook;
import org.xdev100.vinimay.api.model.MessageToEngine;

import java.util.concurrent.CompletableFuture;

@Service
public class DepthService {

    @Autowired
    private RedisManager redisManager;
    public CompletableFuture<MessageFromOrderBook> getDepth(MessageToEngine.GetDepth depth) throws  JsonProcessingException{
        return redisManager.sendAndAwait(MessageToEngine.from(depth));
    }
}
