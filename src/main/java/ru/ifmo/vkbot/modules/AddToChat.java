package ru.ifmo.vkbot.modules;

import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.utils.PostExecutor;

/**
 *
 * @author RinesThaix
 */
public class AddToChat extends BotModule {

    public AddToChat(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        if(!checkAdministrator(m))
            return;
        if(args.length != 1) {
            getMC().sendAttached(m.getDialog(), "Недостаточно аргументов!", m.getMessageId());
            return;
        }
        long chat = -1;
        try {
            chat = Long.parseLong(args[0]);
        }catch(NumberFormatException ex) {
            getMC().sendAttached(m.getDialog(), "Идентификатор задан неверно!", m.getMessageId());
            return;
        }
        if(chat < 0) {
            chat = -chat;
            String line = PostExecutor.buildAndGet("messages.addChatUser", "uid", m.getSender(), "chat_id", chat);
            if(line.contains("response"))
                getMC().sendAttached(m.getDialog(), "Сделано, сир.", m.getMessageId());
            else
                getMC().sendAttached(m.getDialog(), "Я почему-то не смогла. Простите меня, сир :(", m.getMessageId());
        }else {
            String line = PostExecutor.buildAndGet("messages.createChat", "uids", m.getSender() + "," + chat, "title", "Милаши");
            if(line.contains("response"))
                getMC().sendAttached(m.getDialog(), "Сделано, сир.", m.getMessageId());
            else
                getMC().sendAttached(m.getDialog(), "Я почему-то не смогла. Простите меня, сир :(", m.getMessageId());
        }
    }

}
