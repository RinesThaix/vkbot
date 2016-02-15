package ru.ifmo.vkbot.modules;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.controllers.MessagesController.Attachment;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.utils.PostExecutor;

/**
 *
 * @author RinesThaix
 */
public class Administration extends BotModule {
    
    public final static List<String> admins = new ArrayList();
    
    public static void loadAdmins(List<Long> ids) {
        for(long uid : ids) {
            JSONArray answer = VkBot.parse(PostExecutor.buildAndGet("users.get", "uid", uid, "lang", "ru"));
            JSONObject info = (JSONObject) answer.get(0);
            String fullName = (String) info.get("first_name") + " " + (String) info.get("last_name");
            admins.add(fullName + " (https://vk.com/id" + uid + ")");
        }
    }

    public Administration(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("Меня создал Кооооостик :3\n\n");
        sb.append("Список моих администраторов:\n");
        for(String s : admins)
            sb.append("- ").append(s).append("\n");
        getMC().sendWithAttachment(m.getDialog(), sb.toString(), m.getMessageId(), Attachment.PHOTO, new long[]{59649933, 401470335});
    }

}
