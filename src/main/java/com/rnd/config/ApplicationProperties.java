package com.rnd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class ApplicationProperties {

    @Value("${habr-bot.service.rss}")
    private URI habrRss;

    @Value("${chat.websocket}")
    private URI chatURI;

    @Value("${chat.authURI}")
    private URI authURI;

    @Value("${chat.successURI}")
    private URI successURI;

    @Value("${chat.user.login}")
    private String username;

    @Value("${chat.user.password}")
    private String password;

    public URI getHabrRss() {
        return habrRss;
    }

    public void setHabrRss(URI habrRss) {
        this.habrRss = habrRss;
    }

    public URI getChatURI() {
        return chatURI;
    }

    public void setChatURI(URI chatURI) {
        this.chatURI = chatURI;
    }

    public URI getAuthURI() {
        return authURI;
    }

    public void setAuthURI(URI authURI) {
        this.authURI = authURI;
    }

    public URI getSuccessURI() {
        return successURI;
    }

    public void setSuccessURI(URI successURI) {
        this.successURI = successURI;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
