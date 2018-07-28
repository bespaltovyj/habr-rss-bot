package com.rnd.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.sockjs.client.SockJsClient;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SockJsClient sockJsClient;

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Autowired
    private TaskExecutor executor;

    private URI chatURI;
    private URI loginURI;
    private URI succesURI;
    private String login;
    private String password;


    public ChatService(URI chatURI, URI loginURI, URI succesURI, String login, String password) {
        this.chatURI = chatURI;
        this.loginURI = loginURI;
        this.succesURI = succesURI;
        this.login = login;
        this.password = password;
    }

    @PostConstruct
    public void connectToChat() {
        executor.execute(new Connector());
    }

    private HttpHeaders sendAuthRequest() {
        return restTemplate.execute(
                loginURI,
                HttpMethod.POST,
                writeEntityToRequest(login, password),
                httpHeadersResponseExtractor()
        );
    }

    private void doHandshake(HttpHeaders httpHeaders) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(httpHeaders);
        sockJsClient.doHandshake(webSocketHandler, headers, chatURI);
    }

    private HttpHeaders createCookieHttpHeadersFromRawStrings(List<String> rawStrings) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put(
                HttpHeaders.COOKIE,
                rawStrings.stream().map(this::parseSetCookieHeader).collect(Collectors.toList())
        );
        return httpHeaders;
    }

    private String parseSetCookieHeader(String header) {
        final String separator = ";";
        return header.split(separator)[0];
    }



    private class Connector implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    logger.info("Try authenticate to " + loginURI);
                    if (tryConnecting()) {
                        logger.info("Is authenticated to " + loginURI);
                        return;
                    }
                    Thread.sleep(60000);
                } catch (Exception ignored) {

                }
            }
        }

        private boolean tryConnecting() throws InterruptedException {
            HttpHeaders headers = sendAuthRequest();
            if (!checkAuthenticated(headers)) {
                return false;
            }
            List<String> rawStringSetCookie = headers.get(HttpHeaders.SET_COOKIE);
            doHandshake(createCookieHttpHeadersFromRawStrings(rawStringSetCookie));
            return true;
        }

        private boolean checkAuthenticated(HttpHeaders httpHeaders) {
            return Objects.equals(succesURI, httpHeaders.getLocation());
        }

    }

    private static RequestCallback writeEntityToRequest(String username, String password) {
        return request -> {
            FormHttpMessageConverter converter = new FormHttpMessageConverter();

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("username", username);
            map.add("password", password);

            converter.write(map, MediaType.APPLICATION_FORM_URLENCODED, request);
        };
    }

    private static ResponseExtractor<HttpHeaders> httpHeadersResponseExtractor() {
        return HttpMessage::getHeaders;
    }

}
