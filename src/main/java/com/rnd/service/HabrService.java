package com.rnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class HabrService {

    @Autowired
    private DocumentBuilder documentBuilder;

    private URI rssURI;
    private List<String> articleLinks = new ArrayList<>();

    public HabrService(URI uri) {
        this.rssURI = uri;
    }

    public List<String> getArticleLinks() {
        return articleLinks;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 */1 * * *")
    public void loadArticleLinks() {
        try {
            Document doc = documentBuilder.parse(rssURI.toString());
            Element documentElement = doc.getDocumentElement();

            final String channelTagName = "channel";
            final String articleTagName = "item";
            for (int index = 0; index < documentElement.getChildNodes().getLength(); ++index) {
                Node node = documentElement.getChildNodes().item(index);
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element array = (Element) node;
                String nameArray = array.getNodeName();
                if (Objects.equals(channelTagName, nameArray)) {
                    articleLinks = parseItems(array.getElementsByTagName(articleTagName));
                }
            }
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> parseItems(NodeList items) {
        final int indexFirstElement = 0;
        final String linkTagName = "guid";
        List<String> itemsLinks = new ArrayList<>();
        for (int i = 0; i < items.getLength(); ++i) {
            Node node = items.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element) node;
            itemsLinks.add(element.getElementsByTagName(linkTagName).item(indexFirstElement).getTextContent());
        }
        return itemsLinks;
    }

}
