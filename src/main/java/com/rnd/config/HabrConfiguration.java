package com.rnd.config;

import com.rnd.controller.WebSocketBotHandler;
import com.rnd.service.ChatService;
import com.rnd.service.HabrService;
import com.rnd.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class HabrConfiguration {

    @Autowired
    private ApplicationProperties properties;

    @Bean
    public HabrService habrService() {
        return new HabrService(properties.getHabrRss());
    }

    @Bean
    public TopicService topicService() {
        return new TopicService();
    }

    @Bean
    public ChatService chatService() {
        return new ChatService(
                properties.getChatURI(),
                properties.getAuthURI(),
                properties.getSuccessURI(),
                properties.getUsername(),
                properties.getPassword()
        );
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new WebSocketBotHandler();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SockJsClient sockJsClient() {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        return new SockJsClient(transports);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(8);
        return taskExecutor;
    }

}
