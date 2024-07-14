package org.xdev100.vinimay.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.xdev100.vinimay.engine.service.MessageListenerService;

@SpringBootApplication
public class EngineApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EngineApplication.class, args);
        MessageListenerService listenerService = context.getBean(MessageListenerService.class);
        listenerService.listenForMessaages();
        //SpringApplication.run(EngineApplication.class, args);
    }

}
