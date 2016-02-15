package ru.ifmo.vkbot.modules;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.utils.PostExecutor;

/**
 *
 * @author RinesThaix
 */
public class Who extends BotModule {
    
    private final static List<String> prefixes = Arrays.asList(new String[]{
        "Я думаю.. это ",
        "Это.. ну.. наверное, это ",
        "Конечно же ",
        "Я не знаю, но.. возможно, ",
        "==> "
    });

    public Who(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        if(m.getDialog() > 0) {
            System.out.println(getMC() == null);
            getMC().send(m.getDialog(), "Боюсь, эта функция не работает в чатах один на один. Мне правда жаль :(");
            return;
        }
        String s = toString(args);
        Random r = new Random(s.toLowerCase().hashCode());
        int id = r.nextInt(m.getMembers().length);
        long uid = m.getMembers()[id];
        JSONArray answer = VkBot.parse(PostExecutor.buildAndGet("users.get", "uid", uid, "lang", "ru"));
        JSONObject info = (JSONObject) answer.get(0);
        String fullName = (String) info.get("first_name") + " " + (String) info.get("last_name");
        getMC().sendAttached(m.getDialog(), prefixes.get(getRandom().nextInt(prefixes.size())) + fullName, m.getMessageId());
    }

}
