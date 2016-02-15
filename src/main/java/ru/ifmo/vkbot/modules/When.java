package ru.ifmo.vkbot.modules;

import java.util.Random;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.utils.RDate;

/**
 *
 * @author RinesThaix
 */
public class When extends BotModule {

    public When(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        RDate date = new RDate(1455289173000l);
        Random r = new Random(toString(args).toLowerCase().hashCode());
        int deltaDays = r.nextInt(20000) - 10000;
        date.addDays(deltaDays);
        long deltaSeconds = r.nextInt(1000000);
        date.setTime(date.getTime() + deltaSeconds * 1000);
        getMC().sendAttached(m.getDialog(), date.toString(), m.getMessageId());
    }

}
