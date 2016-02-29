package ru.ifmo.vkbot.modules;

import java.util.List;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.utils.Configuration;
import ru.ifmo.vkbot.utils.Logger;

/**
 *
 * @author RinesThaix
 */
public class ModersManagement {

    public static class AddModerator extends BotModule {

        public AddModerator(VkBot vkbot) {
            super(vkbot, Group.ADMINISTRATOR);
        }

        @Override
        public void handle(Message m, String[] args) {
            if(args.length != 1) {
                getMC().sendAttached(m.getDialog(), "Неверное число аргументов!", m.getMessageId());
                return;
            }
            long uid = 0;
            try {
                uid = Long.parseLong(args[0]);
            }catch(NumberFormatException ex) {}
            if(uid <= 0) {
                getMC().sendAttached(m.getDialog(), "Идентификатор пользователя должен быть натуральным числом!", m.getMessageId());
                return;
            }
            if(getVkBot().getGroup(uid).compareTo(Group.USER) > 0) {
                getMC().sendAttached(m.getDialog(), "Пользователь с данным идентификатором уже является моим модератором или администратором!", m.getMessageId());
                return;
            }
            Staff.addModerator(uid);
            List<Long> staff = getVkBot().getModerators();
            staff.add(uid);
            Configuration config = getVkBot().getConfig();
            config.setList("staff", staff);
            try {
                config.save();
                getMC().sendAttached(m.getDialog(), "Новый модератор успешно зарегистрирован!", m.getMessageId());
            }catch(Exception ex) {
                Logger.warn("Could not save configuration file!", ex);
            }
        }
        
    }

    public static class RemoveModerator extends BotModule {

        public RemoveModerator(VkBot vkbot) {
            super(vkbot, Group.ADMINISTRATOR);
        }

        @Override
        public void handle(Message m, String[] args) {
            if(args.length != 1) {
                getMC().sendAttached(m.getDialog(), "Неверное число аргументов!", m.getMessageId());
                return;
            }
            long uid = 0;
            try {
                uid = Long.parseLong(args[0]);
            }catch(NumberFormatException ex) {}
            if(uid <= 0) {
                getMC().sendAttached(m.getDialog(), "Идентификатор пользователя должен быть натуральным числом!", m.getMessageId());
                return;
            }
            if(getVkBot().getGroup(uid) != Group.MODERATOR) {
                getMC().sendAttached(m.getDialog(), "Пользователь с данным идентификатором и так не является модератором!", m.getMessageId());
                return;
            }
            Staff.removeModerator(uid);
            List<Long> staff = getVkBot().getModerators();
            staff.remove(uid);
            Configuration config = getVkBot().getConfig();
            config.setList("staff", staff);
            try {
                config.save();
                getMC().sendAttached(m.getDialog(), "Модератор с указанным идентификатором успешно удален!", m.getMessageId());
            }catch(Exception ex) {
                Logger.warn("Could not save configuration file!", ex);
            }
        }
        
    }
    
}
