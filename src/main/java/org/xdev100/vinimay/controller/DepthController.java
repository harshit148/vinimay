package org.xdev100.vinimay.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xdev100.vinimay.model.MessageFromOrderBook;
import org.xdev100.vinimay.model.MessageToEngine;
import org.xdev100.vinimay.service.DepthService;

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
