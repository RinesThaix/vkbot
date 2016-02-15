package ru.ifmo.vkbot.modules;

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
public class MusicSearch extends BotModule {

    public MusicSearch(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        String s = toString(args);
        if(s.isEmpty()) {
            getMC().sendAttached(m.getDialog(), "Ну и что ты ищешь?", m.getMessageId());
            return;
        }
        JSONArray answer = VkBot.parse(PostExecutor.buildAndGet("audio.search", "q", s, "auto_complete", 1, "sort", 2, "count", 5));
        long[] aids = new long[(answer.size() - 1) << 1];
        for(int i = 1; i < answer.size(); ++i) {
            JSONObject audio = (JSONObject) answer.get(i);
            aids[(i - 1) << 1] = (long) audio.get("owner_id");
            aids[((i - 1) << 1) + 1] = (long) audio.get("aid");
        }
        getMC().sendWithAttachment(m.getDialog(), "Вот, что мне удалось найти:", m.getMessageId(), Attachment.AUDIO, aids);
    }

}
