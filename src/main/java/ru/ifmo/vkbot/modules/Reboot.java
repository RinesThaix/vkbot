package ru.ifmo.vkbot.modules;

import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;

/**
 *
 * @author RinesThaix
 */
public class Reboot extends BotModule {

    public Reboot(VkBot vkbot) {
        super(vkbot, Group.ADMINISTRATOR);
    }

    @Override
    public void handle(Message m, String[] args) {
        getMC().sendAttached(m.getDialog(), "Ухожу в перезагрузку!", m.getMessageId());
        getVkBot().reboot();
    }

}
