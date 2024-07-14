package org.xdev100.vinimay.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xdev100.vinimay.api.service.DepthService;
import org.xdev100.vinimay.api.model.MessageFromOrderBook;
import org.xdev100.vinimay.api.model.MessageToEngine;

import java.util.concurrent.CompletableFuture;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/depth")
public class DepthController {

    @Autowired
    private DepthService depthService;

    @GetMapping
    public CompletableFuture<ResponseEntity<MessageFromOrderBook>> getDepth(@Valid @RequestBody MessageToEngine.GetDepth depth) throws JsonProcessingException {
        return depthService.getDepth(depth).thenApply(ResponseEntity::ok);
    }

}
