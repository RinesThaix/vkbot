package ru.ifmo.vkbot.modules;

import java.util.Random;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;

/**
 *
 * @author RinesThaix
 */
public class Infa extends BotModule {

    public Infa(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        if(args.length == 0) {
            getMC().sendAttached(m.getDialog(), "Недостаточно аргументов!", m.getMessageId());
            return;
        }
        String s = toString(args);
        Random r = new Random(s.toLowerCase().hashCode() + m.getSender());
        int prob = r.nextInt(101);
        getMC().sendAttached(m.getDialog(), "Вероятность этого -- " + prob + "%", m.getMessageId());
    }

}
