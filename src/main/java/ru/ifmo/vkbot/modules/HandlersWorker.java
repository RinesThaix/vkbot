package ru.ifmo.vkbot.modules;

import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;

/**
 *
 * @author RinesThaix
 */
public class HandlersWorker {

    public static class UpdateCustomHandler extends BotModule {

        public UpdateCustomHandler(VkBot vkbot) {
            super(vkbot, Group.MODERATOR);
        }

        @Override
        public void handle(Message m, String[] args) {
            if(args.length < 2) {
                getMC().sendAttached(m.getDialog(), "Недостаточно аргументов!", m.getMessageId());
                return;
            }
            String handler = args[0].toLowerCase().replace("_", " ");
            String[] parsed = parseQuotes(args, 1, 1);
            if(parsed == null) {
                getMC().sendAttached(m.getDialog(), "Ты ошибся в синтаксисе команды!", m.getMessageId());
                return;
            }
            String text = parsed[0];
            if(text.isEmpty()) {
                getMC().sendAttached(m.getDialog(), "Невозможно добавить пустой ответ!", m.getMessageId());
                return;
            }
            if(getMC().getLinker().getHandler("#" + handler) == null) {
                getMC().getLinker().createHandler(handler, text);
                getMC().sendAttached(m.getDialog(), "Новый текстовый модуль #" + handler + " успешно создан!\n"
                        + "Пожалуйста, учтите, что обучение этому модулю можно будет "
                        + "начать только после моей перезагрузки.", m.getMessageId());
                return;
            }
            ((CustomHandler) getMC().getLinker().getHandler("#" + handler)).update(text);
            getMC().sendAttached(m.getDialog(), "Новый ответ для текстового модуля #" + handler + " успешно добавлен!", m.getMessageId());
        }
        
    }

    public static class Study extends BotModule {

        public Study(VkBot vkbot) {
            super(vkbot, Group.MODERATOR);
        }

        @Override
        public void handle(Message m, String[] args) {
            if(args.length < 2) {
                getMC().sendAttached(m.getDialog(), "Недостаточно аргументов!", m.getMessageId());
                return;
            }
            String handler = args[0].toLowerCase().replace("_", " ");
            String[] parsed = parseQuotes(args, 1, 1);
            if(parsed == null) {
                getMC().sendAttached(m.getDialog(), "Ты ошибся в синтаксисе команды!", m.getMessageId());
                return;
            }
            String text = parsed[0];
            if(text.isEmpty()) {
                getMC().sendAttached(m.getDialog(), "Невозможно обработать пустое обращение!", m.getMessageId());
                return;
            }
            if(getMC().getLinker().getHandler(handler) == null) {
                getMC().sendAttached(m.getDialog(), "Текстового модуля " + handler + " не существует!", m.getMessageId());
                return;
            }
            getVkBot().getClassificationController().study(text.replace("|", ""), handler);
            getMC().sendAttached(m.getDialog(), "Я запомню, спасибо.", m.getMessageId());
        }
        
    }
    
    public static class ListAllHandlers extends BotModule {

        public ListAllHandlers(VkBot vkbot) {
            super(vkbot, Group.MODERATOR, false);
        }

        @Override
        public void handle(Message m, String[] args) {
            StringBuilder sb = new StringBuilder();
            sb.append("Список активных модулей:\n");
            for(String s : getMC().getLinker().getHandlers())
                sb.append(s).append(", ");
            String s = sb.toString();
            s = s.substring(0, s.length() - 2);
            s += ".";
            getMC().sendAttached(m.getDialog(), s, m.getMessageId());
        }
        
    }
    
}
