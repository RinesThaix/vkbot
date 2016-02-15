package ru.ifmo.vkbot.modules;

import java.util.Arrays;
import java.util.List;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;

/**
 *
 * @author RinesThaix
 */
public class Hello extends BotModule {
    
    private final static List<String> hellos = Arrays.asList(new String[]{
        "Здравствуй!",
        "Привет <3",
        "Привеееетик :3",
        "Мурмурмур",
        "Мурр"
    });

    public Hello(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        getMC().send(m.getDialog(), hellos.get(getRandom().nextInt(hellos.size())));
    }

}
