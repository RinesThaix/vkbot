package ru.ifmo.vkbot.modules;

import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;

/**
 *
 * @author RinesThaix
 */
public class Bans {

    public static class Ban extends BotModule {

        public Ban(VkBot vkbot) {
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
            long uid = -1;
            try {
                uid = Long.parseLong(args[0]);
            }catch(NumberFormatException ex) {
                getMC().sendAttached(m.getDialog(), "Неверный идентификатор!", m.getMessageId());
            }
            if(getVkBot().getBanController().isBanned(uid)) {
                getMC().sendAttached(m.getDialog(), "Пользователь с указанным идентификатором уже забанен!", m.getMessageId());
                return;
            }
            getVkBot().getBanController().ban(uid);
            getMC().sendAttached(m.getDialog(), "Готово!", m.getMessageId());
        }
        
    }

    public static class Unban extends BotModule {

        public Unban(VkBot vkbot) {
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
            long uid = -1;
            try {
                uid = Long.parseLong(args[0]);
            }catch(NumberFormatException ex) {
                getMC().sendAttached(m.getDialog(), "Неверный идентификатор!", m.getMessageId());
            }
            if(!getVkBot().getBanController().isBanned(uid)) {
                getMC().sendAttached(m.getDialog(), "Пользователь с указанным идентификатором и так не в бане!", m.getMessageId());
                return;
            }
            getVkBot().getBanController().unban(uid);
            getMC().sendAttached(m.getDialog(), "Готово!", m.getMessageId());
        }
        
    }
    
}
