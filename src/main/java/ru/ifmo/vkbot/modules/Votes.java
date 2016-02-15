package ru.ifmo.vkbot.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.structures.Vote;

/**
 *
 * @author RinesThaix
 */
public class Votes {
    
    private final static Map<Long, Vote> votes = new HashMap();
    
    public static class VoteCreation extends BotModule {

        public VoteCreation(VkBot vkbot) {
            super(vkbot);
        }

        @Override
        public void handle(Message m, String[] args) {
            long did = m.getDialog();
            Vote previous = votes.get(did);
            if(args.length < 2) {
                getMC().sendAttached(did, "Недостаточно аргументов!", m.getMessageId());
                return;
            }
            String name = args[0].replace("_", " ");
            String[] options = new String[args.length - 1];
            for(int i = 1; i < args.length; ++i)
                options[i - 1] = args[i].replace("_", " ");
            votes.put(did, new Vote(name, options));
            StringBuilder sb = new StringBuilder();
            sb.append("Создано новое голосование:\n");
            sb.append(name).append("\n\n");
            for(int i = 0; i < options.length; ++i)
                sb.append("(").append(i + 1).append(") ").append(options[i]).append("\n");
            if(previous != null) {
                sb.append("\nПредыдущее голосование было экстернально завершено со следующими результатами:\n");
                List<String> ops = previous.getOptions();
                List<Integer> results = previous.getResults();
                for(int i = 0; i < ops.size(); ++i)
                    sb.append("(").append(i + 1).append(") ").append(ops.get(i)).append(" - ").append(results.get(i)).append("\n");
            }
            getMC().sendAttached(did, sb.toString(), m.getMessageId());
        }
        
    }
    
    public static class VoteVote extends BotModule {

        public VoteVote(VkBot vkbot) {
            super(vkbot);
        }

        @Override
        public void handle(Message m, String[] args) {
            long did = m.getDialog();
            if(votes.get(did) == null) {
                getMC().sendAttached(did, "Здесь нет никаких открытых голосований!", m.getMessageId());
                return;
            }
            if(args.length != 1) {
                getMC().sendAttached(did, "Неверное число аргументов!", m.getMessageId());
                return;
            }
            int option = -1;
            try {
                option = Integer.parseInt(args[0]);
            }catch(NumberFormatException ex) {}
            Vote v = votes.get(did);
            if(v.getOptionsSize() < option || option <= 0) {
                getMC().sendAttached(did, "Варианта с таким номером нет! Не пытайся меня обмануть :c", m.getMessageId());
                return;
            }
            if(!v.vote(m.getSender(), option - 1)) {
                getMC().sendAttached(did, "Твой голос изменен.", m.getMessageId());
                return;
            }
            getMC().sendAttached(did, "Твой голос успешно засчитан :3", m.getMessageId());
        }
        
    }
    
    public static class VoteShow extends BotModule {

        public VoteShow(VkBot vkbot) {
            super(vkbot);
        }

        @Override
        public void handle(Message m, String[] args) {
            long did = m.getDialog();
            if(votes.get(did) == null) {
                getMC().sendAttached(did, "Здесь нет никаких открытых голосований!", m.getMessageId());
                return;
            }
            Vote v = votes.get(did);
            StringBuilder sb = new StringBuilder();
            sb.append("Сейчас идет следующее голосование:\n");
            sb.append(v.getName()).append("\n\n");
            List<String> ops = v.getOptions();
            List<Integer> results = v.getResults();
            for(int i = 0; i < ops.size(); ++i)
                sb.append("(").append(i + 1).append(") ").append(ops.get(i)).append(" - ").append(results.get(i)).append("\n");
            getMC().sendAttached(did, sb.toString(), m.getMessageId());
        }
        
    }
    
}
