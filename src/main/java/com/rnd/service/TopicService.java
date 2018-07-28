package com.rnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Service
public class TopicService {

    @Autowired
    private HabrService habrService;

    private Map<String, LinkedList<String>> uniqueListArticles = new HashMap<>();

    public String  getNextTopic(String login) {
        LinkedList<String> articles = uniqueListArticles.get(login);
        if (articles == null || articles.isEmpty()) {
            articles = new LinkedList<>(habrService.getArticleLinks());
            uniqueListArticles.put(login, articles);
        }
        return articles.pollFirst();
    }

    @Scheduled(cron = "0 0 */1 * * *")
    public void clearMap(){
        uniqueListArticles.clear();
    }
}
