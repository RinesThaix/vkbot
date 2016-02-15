package ru.ifmo.vkbot.modules;

import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;

/**
 *
 * @author RinesThaix
 */
public class Sleep extends BotModule {

    public Sleep(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        if(!getVkBot().isAdministrator(m.getSender())) {
            getMC().send(m.getDialog(), "Самозванец!!");
            return;
        }
        getMC().sendAttached(m.getDialog(), "Слушаюсь, сир!", m.getMessageId());
        getVkBot().disable();
    }

}
