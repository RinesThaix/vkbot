package ru.ifmo.vkbot.modules;

import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;

/**
 *
 * @author RinesThaix
 */
public class MemesManagement {

    public static class MemeTemplateAdd extends BotModule {

        public MemeTemplateAdd(VkBot vkbot) {
            super(vkbot, Group.ADMINISTRATOR);
        }

        @Override
        public void handle(Message m, String[] args) {
            if(args.length != 1) {
                getMC().sendAttached(m.getDialog(), "Неверное число аргументов!", m.getMessageId());
                return;
            }
            if(m.getPhotos().isEmpty()) {
                getMC().sendAttached(m.getDialog(), "Чтобы добавить новый шаблон, прикрепи соответствующее фото к своему сообщению.", m.getMessageId());
                return;
            }
            String template = args[0].toLowerCase();
            if(getVkBot().getMemesTemplatesController().exists(template)) {
                getMC().sendAttached(m.getDialog(), "Шаблон с указанным названием уже существует!", m.getMessageId());
                return;
            }
            getVkBot().getMemesTemplatesController().addTemplate(template, m.getPhotos().iterator().next().getSource());
            getMC().sendAttached(m.getDialog(), "Шаблон успешно добавлен!", m.getMessageId());
        }
        
    }

    public static class MemeTemplateRemove extends BotModule {

        public MemeTemplateRemove(VkBot vkbot) {
            super(vkbot, Group.ADMINISTRATOR);
        }

        @Override
        public void handle(Message m, String[] args) {
            if(args.length != 1) {
                getMC().sendAttached(m.getDialog(), "Неверное число аргументов!", m.getMessageId());
                return;
            }
            String template = args[0].toLowerCase();
            if(!getVkBot().getMemesTemplatesController().exists(template)) {
                getMC().sendAttached(m.getDialog(), "Шаблона с указанным названием не существует!", m.getMessageId());
                return;
            }
            getVkBot().getMemesTemplatesController().removeTemplate(template);
            getMC().sendAttached(m.getDialog(), "Шаблон успешно удален!", m.getMessageId());
        }
        
    }

    public static class MemeTemplateList extends BotModule {

        public MemeTemplateList(VkBot vkbot) {
            super(vkbot);
        }

        @Override
        public void handle(Message m, String[] args) {
            StringBuilder sb = new StringBuilder();
            sb.append("Шаблоны для мемов:\n");
            for(String s : getVkBot().getMemesTemplatesController().getTemplates())
                sb.append("- ").append(s).append("\n");
            getMC().send(m.getDialog(), sb.toString());
        }
        
    }
    
}
