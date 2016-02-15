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
public class Secrets {
    
    public static class McSkakov extends BotModule {

        public McSkakov(VkBot vkbot) {
            super(vkbot);
        }

        @Override
        public void handle(Message m, String[] args) {
            getMC().sendWithAttachment(m.getDialog(), "Встречайте: Mc Skakov!", m.getMessageId(), Attachment.PHOTO, new long[]{59649933, 401558093});
        }
        
    }
    
    public static class SWAG extends BotModule {

        public SWAG(VkBot vkbot) {
            super(vkbot);
        }

        @Override
        public void handle(Message m, String[] args) {
            getMC().sendWithAttachment(m.getDialog(), "Secretly We Are Gays", m.getMessageId(), Attachment.PHOTO, new long[]{59649933, 401554155});
        }
        
    }
    
    public static class Skalenium extends BotModule {

        public Skalenium(VkBot vkbot) {
            super(vkbot);
        }

        @Override
        public void handle(Message m, String[] args) {
            getMC().sendWithAttachment(m.getDialog(), "!!!", m.getMessageId(), Attachment.VIDEO, new long[]{229484766, 171221845});
        }
        
    }
    
    public static class Telephone extends BotModule {

        public Telephone(VkBot vkbot) {
            super(vkbot);
        }

        @Override
        public void handle(Message m, String[] args) {
            JSONArray answer = VkBot.parse(PostExecutor.buildAndGet("audio.search", "q", "88005553535", "auto_complete", 1, "sort", 2, "count", 1));
            JSONObject audio = (JSONObject) answer.get(1);
            long[] aids = new long[]{(long) audio.get("owner_id"), (long) audio.get("aid")};
            getMC().sendWithAttachment(m.getDialog(), "Проще позвонить, чем у кого-то занимать!", m.getMessageId(), Attachment.AUDIO, aids);
        }
        
    }
}
