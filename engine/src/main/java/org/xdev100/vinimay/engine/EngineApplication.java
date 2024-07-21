package org.xdev100.vinimay.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.xdev100.vinimay.engine.service.MessageListenerService;

@SpringBootApplication(exclude = {
        WebMvcAutoConfiguration.class,
        WebClientAutoConfiguration.class
})
public class EngineApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EngineApplication.class, args);
        MessageListenerService listenerService = context.getBean(MessageListenerService.class);
        listenerService.listenForMessages();
        //SpringApplication.run(EngineApplication.class, args);
    }

}
