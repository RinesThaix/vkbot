package ru.ifmo.vkbot.modules;

import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.controllers.MessagesController.Attachment;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.utils.ImagesWorker;

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
        if(m.getPhotos().isEmpty()) {
            if(args.length < 2) {
                getMC().sendAttached(m.getDialog(), "Недостаточно аргументов!", m.getMessageId());
                return;
            }
            String template = args[0].toLowerCase();
            if(getVkBot().getMemesTemplatesController().exists(template)) {
                String upper = args[1].replace("_", " "), lower = "";
                if(args.length >= 3)
                    lower = args[2].replace("_", " ");
                long pid = ImagesWorker.addText(template, upper, lower);
                //Убран массив, так как метод sendWithAttachment всё равно принимает varargs
                getMC().sendWithAttachment(m.getDialog(), "Держи!", m.getMessageId(), Attachment.PHOTO, getVkBot().getAssistantId(), pid);
            }else
                getMC().sendAttached(m.getDialog(), "Шаблона с указанным названием не существует!", m.getMessageId());
        }else {
            if(args.length < 1) {
                getMC().sendAttached(m.getDialog(), "Недостаточно аргументов!", 0);
                return;
            }
            String upper = args[0].replace("_", " "), lower = "";
            if(args.length >= 2)
                lower = args[1].replace("_", " ");
            long pid = ImagesWorker.addTextRemotely(m.getPhotos().iterator().next().getSource(), upper, lower);
            getMC().sendWithAttachment(m.getDialog(), "Держи!", 0, Attachment.PHOTO, getVkBot().getAssistantId(), pid);
        }
    }

}
