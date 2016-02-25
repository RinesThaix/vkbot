package ru.ifmo.vkbot.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.json.simple.JSONArray;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.utils.Configuration;
import ru.ifmo.vkbot.utils.Logger;
import ru.ifmo.vkbot.utils.PostExecutor;

/**
 *
 * @author RinesThaix
 */
public class BanController {
    
    private final static Set<Long> banned = new HashSet();
    
    private Configuration config;
    
    public BanController() {
        load();
    }
    
    private void load() {
        try {
            Logger.log("Loading BanController..");
            config = new Configuration("bans");
            String banned;
            if((banned = config.getString("banned", null)) != null) {
                String[] spl = banned.split(" ");
                for(String s : spl) {
                    BanController.banned.add(Long.parseLong(s));
                }
            }
        }catch(IOException ex) {
            Logger.warn("Could not load BanController!");
        }
    }
    
    public void save() {
        try {
            if(banned.isEmpty())
                return;
            StringBuilder sb = new StringBuilder();
            for(Long l : banned)
                sb.append(l).append(" ");
            String s = sb.toString();
            s = s.substring(0, s.length() - 1);
            config.setString("banned", s);
            config.save();
        }catch(IOException ex) {
            Logger.warn("Could not save BanController!");
        }
    }
    
    public void ban(long uid) {
        banned.add(uid);
    }
    
    public void unban(long uid) {
        banned.remove(uid);
    }
    
    public boolean isBanned(long uid) {
        return banned.contains(uid);
    }
    
    public Collection<Long> getBans() {
        return banned;
    }

    public void check() {
        JSONArray answer = VkBot.parse(PostExecutor.buildAndGet("friends.getRequests", "out", 1, "count", 3));
        for(int i = 0; i < answer.size(); ++i) {
            long uid = (long) answer.get(i);
            ban(uid);
            PostExecutor.buildAndGet("friends.delete", "uid", uid);
            try {
                Thread.sleep(300l);
            }catch(InterruptedException ex) {}
        }
    }
}
