package ru.ifmo.vkbot.controllers;

import org.json.simple.JSONArray;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.utils.Logger;
import ru.ifmo.vkbot.utils.PostExecutor;

/**
 *
 * @author RinesThaix
 */
public class FriendsController {
    
    private final static int free_limit = 100;
    
    private final VkBot vkbot;
    
    public FriendsController(VkBot vkbot) {
        Logger.log("Loading FriendsController..");
        this.vkbot = vkbot;
    }

    public void check() {
        JSONArray answer = VkBot.parse(PostExecutor.buildAndGet("friends.get"));
        int fr = answer.size();
        if(fr < free_limit)
            addFriends(free_limit - fr);
    }
    
    public void addFriends(int amount) {
        JSONArray answer = VkBot.parse(PostExecutor.buildAndGet("friends.getRequests", "count", 10));
        for(int i = 0; i < answer.size() && amount > 0; ++i, --amount) {
            long uid = (long) answer.get(i);
            if(vkbot.getBanController().isBanned(uid))
                continue;
            String line = PostExecutor.buildAndGet("friends.add", "uid", uid);
            if(line.contains("response"))
                vkbot.getMessagesController().send(uid, "Привет :3");
            try {
                Thread.sleep(300l);
            }catch(InterruptedException ex) {}
        }
    }
    
}
