package ru.ifmo.vkbot.modules;

import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.controllers.MessagesController.Attachment;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.utils.ImagesWorker;

import java.util.Arrays;

/**
 *
 * @author RinesThaix
 */
public class MemeCreate extends BotModule {

    public MemeCreate(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        // говнокод
        boolean caps = "capsoff".equals(args[args.length - 1].toLowerCase()) || "некапс".equals(args[args.length - 1].toLowerCase());
        if (caps) {
            args = Arrays.copyOf(args, args.length - 1); // Мне лень разбираться в коде дальше, поэтому я просто оставлю это так
        } else {
            for (int i = 0; i < args.length; ++i) args[i] = args[i].toUpperCase();  // Вынес апперкейс сюда
        }
        // /говнокод
        if(m.getPhotos().isEmpty()) {
            if(args.length < 2) {
                getMC().sendAttached(m.getDialog(), "Недостаточно аргументов!", m.getMessageId());
                return;
            }
            String template = args[0].toLowerCase();
            if(getVkBot().getMemesTemplatesController().exists(template)) {
                String[] texts = parseQuotes(args, 2, 1);
                if(texts == null) {
                    getMC().sendAttached(m.getDialog(), "Ты ошибся в синтаксисе команды!", m.getMessageId());
                    return;
                }
                long pid = ImagesWorker.addText(template, texts[0], texts[1]);
                //Убран массив, так как метод sendWithAttachment всё равно принимает varargs
                getMC().sendWithAttachment(m.getDialog(), "Держи!", m.getMessageId(), Attachment.PHOTO, getVkBot().getAssistantId(), pid);
            }else
                getMC().sendAttached(m.getDialog(), "Шаблона с указанным названием не существует!", m.getMessageId());
        }else {
            if(args.length < 1) {
                getMC().sendAttached(m.getDialog(), "Недостаточно аргументов!", 0);
                return;
            }
            String[] texts = parseQuotes(args, 2, 0);
            if(texts == null) {
                getMC().sendAttached(m.getDialog(), "Ты ошибся в синтаксисе команды!", m.getMessageId());
                return;
            }
            long pid = ImagesWorker.addTextRemotely(m.getPhotos().iterator().next().getSource(), texts[0], texts[1]);
            getMC().sendWithAttachment(m.getDialog(), "Держи!", 0, Attachment.PHOTO, getVkBot().getAssistantId(), pid);
        }
    }

}
