package ru.ifmo.vkbot.modules;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.utils.Logger;
import ru.ifmo.vkbot.utils.PostExecutor;

/**
 *
 * @author RinesThaix
 */
@SuppressWarnings("deprecation")
public class Find extends BotModule {

    public Find(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        if(args.length == 0) {
            getMC().sendAttached(m.getDialog(), "Недостаточно аргументов!", m.getMessageId());
            return;
        }
        StringBuilder query = new StringBuilder();
        for(String s : args)
            query.append(s).append(" ");
        String s = query.toString();
        s = s.substring(0, s.length() - 1);
        s = PostExecutor.encode(s);
        String answer = PostExecutor.executeGet("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" + s);
        try {
            StringBuilder sb = new StringBuilder();
            JSONObject json = (JSONObject) VkBot.getParser().parse(answer);
            json = (JSONObject) json.get("responseData");
            if(json == null) {
                getMC().sendAttached(m.getDialog(), "Некорректный запрос.", m.getMessageId());
                return;
            }
            JSONArray array = (JSONArray) json.get("results");
            sb.append("Вот, что мне удалось найти:\n");
            for(int i = 0; i < Math.min(3, array.size()); ++i) {
                json = (JSONObject) array.get(i);
                String url = (String) json.get("url");
                String title = (String) json.get("titleNoFormatting");
                String description = (String) json.get("content");
                sb.append("(").append(i + 1).append(") ").append(title).append("\n")
                        .append("Ссылка: ").append(url).append("\n").append(description).append("\n\n");
            }
            getMC().sendAttached(m.getDialog(), sb.toString().replaceAll("<[^>]*>", ""), m.getMessageId());
        }catch(Exception ex) {
            Logger.warn("Could not parse google search!", ex);
            getMC().sendAttached(m.getDialog(), "В данный момент обработать этот запрос не представляется возможным.", m.getMessageId());
        }
    }

}
