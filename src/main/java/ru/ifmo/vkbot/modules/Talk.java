package ru.ifmo.vkbot.modules;

import java.util.HashSet;
import java.util.Set;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;

/**
 *
 * @author RinesThaix
 */
public class Talk {
    
    public static Set<Long> talkingWith = new HashSet();

    public static class StartTalking extends BotModule {

        public StartTalking(VkBot vkbot) {
            super(vkbot);
        }

        @Override
        public void handle(Message m, String[] args) {
            if(talkingWith.contains(m.getSender()))
                getMC().sendAttached(m.getDialog(), "Вообще-то мы с тобой уже разговариваем!", m.getMessageId());
            else {
                talkingWith.add(m.getSender());
                getMC().sendAttached(m.getDialog(), "Ну давай поговорим :3", m.getMessageId());
            }
        }
        
    }

    public static class EndTalking extends BotModule {

        public EndTalking(VkBot vkbot) {
            super(vkbot);
        }

        @Override
        public void handle(Message m, String[] args) {
            if(!talkingWith.contains(m.getSender()))
                getMC().sendAttached(m.getDialog(), "Мы с тобой и так не разговариваем!", m.getMessageId());
            else {
                talkingWith.remove(m.getSender());
                getMC().sendAttached(m.getDialog(), "Ну и фи с тобой!", m.getMessageId());
            }
        }
        
    }
    
}
