package ru.ifmo.vkbot.modules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;

/**
 *
 * @author RinesThaix
 */
public class News extends BotModule {

    public News(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        String url = "http://ria.ru/lenta/";
        try {
            Document doc = Jsoup.connect(url).get();
            Elements all = doc.select("div.list_item");
            List<String> news = new ArrayList<>();
            for(Element item : all)
                news.add("- " + item.select("h3").text() + "\n");
            List<String> list = new ArrayList<>();
            Set<Integer> used = new HashSet<>();
            if(all.size() <= 10) {
                for(String s : news)
                    list.add(s);
            }else
                while(list.size() < 10) {
                    int id = getRandom().nextInt(news.size());
                    while(used.contains(id))
                        id = getRandom().nextInt(news.size());
                    list.add(news.get(id));
                    used.add(id);
                }
            StringBuilder sb = new StringBuilder();
            sb.append("Новости к этому часу:\n");
            for(String s : list)
                sb.append(s);
            getMC().send(m.getDialog(), sb.toString());
        }catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

}
