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
    private final boolean loggable;
    
    public BotModule(VkBot vkbot) {
        this(vkbot, false);
    }
    
    public BotModule(VkBot vkbot, boolean loggable) {
        this.vkbot = vkbot;
        this.loggable = loggable;
    }

    public abstract void handle(Message m, String[] args);
    
    public final boolean checkAdministrator(Message m) {
        if(!vkbot.isAdministrator(m.getSender())) {
            getMC().sendAttached(m.getDialog(), "Это действие доступно только моим администраторам!", m.getMessageId());
            return false;
        }
        return true;
    }
    
    public final boolean checkModerator(Message m) {
        if(!vkbot.isModerator(m.getSender())) {
            getMC().sendAttached(m.getDialog(), "Это действие доступно только моим модераторам и администраторам!", m.getMessageId());
            return false;
        }
        return true;
    }
    
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
    
}
