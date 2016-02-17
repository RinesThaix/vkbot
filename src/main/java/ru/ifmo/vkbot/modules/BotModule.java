package ru.ifmo.vkbot.modules;

import java.util.Random;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.controllers.MessagesController;
import ru.ifmo.vkbot.structures.Message;

/**
 *
 * @author RinesThaix
 */
public abstract class BotModule {
    
    private final static Random r = new Random();
    
    private final VkBot vkbot;
    private final Group needed;
    private final boolean loggable;
    
    public BotModule(VkBot vkbot) {
        this(vkbot, Group.USER, false);
    }
    
    public BotModule(VkBot vkbot, Group needed) {
        this(vkbot, needed, true);
    }
    
    public BotModule(VkBot vkbot, Group needed, boolean loggable) {
        this.vkbot = vkbot;
        this.needed = needed;
        this.loggable = loggable;
    }
    
    public boolean handle0(Message m, String[] args) {
        Group g = vkbot.getGroup(m.getSender());
        if(g.compareTo(needed) < 0) {
            getMC().sendAttached(m.getDialog(), "Прости, но у тебя недостаточно прав для исполнения этой команды :(", m.getMessageId());
            return false;
        }
        handle(m, args);
        return true;
    }

    public abstract void handle(Message m, String[] args);
    
    protected VkBot getVkBot() {
        return vkbot;
    }
    
    protected MessagesController getMC() {
        return vkbot.getMessagesController();
    }
    
    protected Random getRandom() {
        return r;
    }
    
    public boolean isLoggable() {
        return loggable;
    }
    
    protected String toString(String[] args) {
        if(args.length == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for(String s : args)
            sb.append(s).append(" ");
        String s = sb.toString();
        s = s.substring(0, s.length() - 1);
        return s;
    }
    
    protected String[] parseQuotes(String[] args, int amount, int startArg) {
        int current = startArg, next = -1;
        String[] answer = new String[amount];
        for(int count = 0; count < amount; ++count) {
            StringBuilder sb = new StringBuilder();
            for(int i = current; i < args.length; ++i) {
                String s = args[i];
                if(i == current) {
                    if(!s.startsWith("\""))
                        return null;
                    if(s.endsWith("\"") && s.length() > 1) {
                        next = i + 1;
                        sb.append(s.replace("\"", ""));
                        break;
                    }
                }else if(s.endsWith("\"")) {
                    sb.append(s.replace("\"", ""));
                    next = i + 1;
                    break;
                }
                sb.append(s.replace("\"", "")).append(" ");
            }
            answer[count] = sb.toString();
            current = next;
        }
        return answer;
    }
    
    public static enum Group {
        USER,
        MODERATOR,
        ADMINISTRATOR;
    }
    
}
