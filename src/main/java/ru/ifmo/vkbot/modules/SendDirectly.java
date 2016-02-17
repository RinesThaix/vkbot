package ru.ifmo.vkbot.modules;

import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;

/**
 *
 * @author RinesThaix
 */
public class SendDirectly extends BotModule {

    public SendDirectly(VkBot vkbot) {
        super(vkbot, Group.ADMINISTRATOR, false);
    }

    @Override
    public void handle(Message m, String[] args) {
        if(args.length < 2) {
            getMC().sendAttached(m.getDialog(), "Недостаточно аргументов.", m.getMessageId());
            return;
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < args.length; ++i)
            sb.append(args[i]).append(" ");
        String msg = sb.toString();
        if(msg.isEmpty())
            return;
        msg = msg.substring(0, msg.length() - 1);
        long uid = -1;
        try {
            uid = Long.parseLong(args[0]);
        }catch(NumberFormatException ex) {
            getMC().sendAttached(m.getDialog(), "Идентификатор пользователя указан неверно!", m.getMessageId());
        }
        getMC().send(uid, msg);
    }

}
