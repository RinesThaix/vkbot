package ru.ifmo.vkbot.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.modules.*;
import ru.ifmo.vkbot.modules.Bans.*;
import ru.ifmo.vkbot.modules.HandlersWorker.*;
import ru.ifmo.vkbot.modules.MemesManagement.*;
import ru.ifmo.vkbot.modules.ModersManagement.*;
import ru.ifmo.vkbot.modules.Secrets.*;
import ru.ifmo.vkbot.modules.Talk.*;
import ru.ifmo.vkbot.modules.Votes.*;
import ru.ifmo.vkbot.structures.*;
import ru.ifmo.vkbot.utils.Logger;
import ru.ifmo.vkbot.utils.Pair;
import ru.ifmo.vkbot.utils.PostExecutor;

/**
 *
 * @author RinesThaix
 */
public class MessagesController extends Thread {
    
    private final static Set<Long> readen = new HashSet();
    private final static Queue<Message> queue = new LinkedList();
    private final static ExecutorService executor = Executors.newCachedThreadPool();
    
    private final VkBot vkbot;
    private final MessagesLinker linker;
    private boolean enabled = true;
    
    public MessagesController(VkBot vkbot) {
        Logger.log("Loading MessagesController..");
        this.vkbot = vkbot;
        this.linker = new MessagesLinker(this);
        this.start();
    }
    
    private int counter = 0;
    
    @Override
    public void run() {
        while(enabled) {
            try {
                executor.execute(new Runnable() {

                    @Override
                    public void run() {
                        if(++counter == 10) {
                            counter = 0;
                            vkbot.getBanController().check();
                            vkbot.getFriendsController().check();
                        }
                        Collection<Message> messages = getLastMessages();
                        queue.addAll(messages);
                        for(int i = 0; i < 3 && !queue.isEmpty(); ++i)
                            linker.handle(queue.poll());
                    }

                });
                try {
                    Thread.sleep(666l);
                }catch(InterruptedException ex) {}
            }catch(Exception ex) {
                Logger.warn("Exception occured!!!", ex);
            }
        }
    }
    
    public void disable() {
        this.enabled = false;
    }
    
    public MessagesLinker getLinker() {
        return linker;
    }
    
    private Collection<Message> getLastMessages() {
        Set<Message> messages = new HashSet(200);
        JSONArray items = VkBot.parse(PostExecutor.buildAndGet("messages.get"));
        long current = System.currentTimeMillis();
        for(int i = 1; i < items.size(); ++i) {
            JSONObject message = (JSONObject) items.get(i);
            long id = (long) message.get("mid");
            if(isReaden(id))
                continue;
            addReaden(id);
            long date = (long) message.get("date") * 1000;
            if(current - date > 10000l)
                continue;
            long dialog = message.get("chat_id") == null ? (long) message.get("uid") : -(long) message.get("chat_id");
            Message m = new Message((String) message.get("body"), id, (long) message.get("uid"), dialog);
            if(message.get("chat_active") != null) {
                String members = (String) message.get("chat_active");
                String[] spl = members.split(",");
                long[] lmembers = new long[spl.length];
                for(int j = 0; j < lmembers.length; ++j)
                    lmembers[j] = Long.parseLong(spl[j]);
                m.setMembers(lmembers);
            }
            if(message.get("attachments") != null) {
                JSONArray array = (JSONArray) message.get("attachments");
                for(int j = 0; j < array.size(); ++j) {
                    JSONObject att = (JSONObject) array.get(j);
                    String type = (String) att.get("type");
                    att = (JSONObject) att.get(type);
                    long owner_id = (long) att.get("owner_id");
                    long attid = (long) att.get(type.charAt(0) + "id");
                    if(type.equals("photo"))
                        m.addPhoto(new Photo(owner_id, attid, (String) att.get("src_big")));
                    else if(type.equals("audio"))
                        m.addAudio(new Audio(owner_id, attid, null));
                    else
                        m.addVideo(new Video(owner_id, attid, null));
                }
            }
            messages.add(m);
        }
        return messages;
    }
    
    private boolean isReaden(long mid) {
        return readen.contains(mid);
    }
    
    private void addReaden(long mid) {
        readen.add(mid);
    }
    
    public void send(long dialog, String message) {
        if(dialog == 0) {
            Logger.log("Sent (to console): %s", message);
            return;
        }
        Logger.log("Sent (to %d): %s", dialog, message);
        if(dialog > 0)
            PostExecutor.buildAndGet("messages.send", "user_id", dialog, "message", message);
        else
            PostExecutor.buildAndGet("messages.send", "chat_id", -dialog, "message", message);
    }
    
    public void sendAttached(long dialog, String message, long mid) {
        if(dialog == 0) {
            Logger.log("Sent (to console): %s", message);
            return;
        }
        Logger.log("Sent (to %d): %s", dialog, message);
        if(dialog > 0)
            PostExecutor.buildAndGet("messages.send", "user_id", dialog, "message", message, "forward_messages", mid);
        else
            PostExecutor.buildAndGet("messages.send", "chat_id", -dialog, "message", message, "forward_messages", mid);
    }
    
    public void sendWithAttachment(long dialog, String message, long mid, Attachment a, long... aids) {
        if(dialog == 0) {
            Logger.log("Sent (to console): %s", message);
            return;
        }
        Logger.log("Sent (to %d): %s", dialog, message);
        String att = a.name().toLowerCase();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < aids.length; i += 2) {
            if(i != 0)
                sb.append(",");
            sb.append(att).append(aids[i]).append("_").append(aids[i + 1]);
        }
        if(dialog > 0)
            if(mid != 0)
                PostExecutor.buildAndGet("messages.send", "user_id", dialog, "message", message, "forward_messages", mid, "attachment", sb.toString());
            else
                PostExecutor.buildAndGet("messages.send", "user_id", dialog, "message", message, "attachment", sb.toString());
        else if(mid != 0)
            PostExecutor.buildAndGet("messages.send", "chat_id", -dialog, "message", message, "forward_messages", mid, "attachment", sb.toString());
        else
            PostExecutor.buildAndGet("messages.send", "chat_id", -dialog, "message", message, "attachment", sb.toString());
    }
    
    public static enum Attachment {
        PHOTO,
        AUDIO,
        VIDEO;
    }
    
    public static class MessagesLinker {
        
        private final static Map<String, Object> handlers = new HashMap();
        
        private final VkBot vkbot;
        private final MessagesController msgc;
        
        public MessagesLinker(MessagesController msgc) {
            this.vkbot = msgc.vkbot;
            this.msgc = msgc;
            loadModules();
        }
        
        public void handle(Message m) {
            if(vkbot.getBanController().isBanned(m.getSender()))
                return;
            String ltext = m.getMessage().toLowerCase();
            boolean handle = false;
            if(ltext.startsWith("милая, ") || ltext.startsWith("милаш, ")) {
                handle = true;
                StringBuilder sb = new StringBuilder();
                String[] spl = m.getMessage().split(" ");
                for(int i = 1; i < spl.length; ++i)
                    sb.append(spl[i]).append(" ");
                String msg = sb.toString();
                if(msg.isEmpty())
                    return;
                msg = msg.substring(0, msg.length() - 1);
                m.updateMessage(msg);
            }else if(m.getDialog() >= 0 || Talk.talkingWith.contains(m.getSender()))
                handle = true;
            if(!handle) {
                Logger.log("Silently Received (chat %d, sender %d): %s", m.getDialog(), m.getSender(), m.getMessage());
                return;
            }
            Logger.log("Received (chat %d, sender %d): %s", m.getDialog(), m.getSender(), m.getMessage());
            Pair<String, String[]> parsed = parse(m.getMessage());
            Object handler = handlers.get(parsed.getA());
            if(handler instanceof String)
                msgc.sendAttached(m.getDialog(), (String) handler, m.getMessageId());
            else
                try {
                    BotModule module = (BotModule) handler;
                    if(module.handle0(m, parsed.getB()) && module.isLoggable())
                        vkbot.getConnector().addToQueue("INSERT INTO vkbot_logger VALUES (%d, %d, %d, '%s')",
                                m.getSender(),
                                vkbot.isAdministrator(m.getSender()) ? 2 : vkbot.isModerator(m.getSender()) ? 1 : 0,
                                System.currentTimeMillis(),
                                m.getMessage());
                }catch(Exception ex) {
                    Logger.warn("Could not handle message using " + handler.toString(), ex);
                    msgc.sendAttached(m.getDialog(), "К сожалению, я не смогла обработать это сообщение. "
                            + "Если вы уверены, что ввели его верно, пожалуйста, обратитесь к администрации бота.",
                            m.getMessageId());
                }
        }
        
        private Pair<String, String[]> parse(String phrase) {
            String lphrase = phrase.toLowerCase();
            for(String key : handlers.keySet())
                if(lphrase.startsWith(key))
                    return split(key, phrase, key.split(" ").length);
            String key = vkbot.getClassificationController().classify(phrase);
            return key.equals("null") ? new Pair(key, null) : split(key, phrase, key.split(" ").length);
        }
        
        private Pair<String, String[]> split(String key, String phrase, int notArgs) {
            String[] spl = phrase.split(" ");
            String[] args = new String[spl.length - notArgs];
            for(int i = notArgs; i < spl.length; ++i)
                args[i - notArgs] = spl[i];
            return new Pair(key, args);
        }
        
        private void loadModules() {
            handlers.put("null", "Прости, но я не поняла, что ты хотел сказать :(");
            add("помощь", new Help(vkbot));
            add("привет", new Hello(vkbot));
            add("md5", new MD5(vkbot));
            add("инфа", new Infa(vkbot));
            add("кто", new Who(vkbot));
            add("когда", new When(vkbot));
            add("расписание", new Scheduler(vkbot));
            add("найди музыку", new MusicSearch(vkbot));
            add("курс", new MoneyRates(vkbot));
            add("новости", new News(vkbot));
            add("давай поговорим", new StartTalking(vkbot));
            add("хватит разговоров", new EndTalking(vkbot));
            add("администраторы", new Administration(vkbot));
            add("модераторы", new Staff(vkbot));
            add("создай голосование", new VoteCreation(vkbot));
            add("я голосую за", new VoteVote(vkbot));
            add("покажи голосование", new VoteShow(vkbot));
            add("создай мем", new MemeCreate(vkbot));
            add("шаблоны для мемов", new MemeTemplateList(vkbot));
            
            //FOR MODERATORS
            add("обнови модуль", new UpdateCustomHandler(vkbot));
            add("запомни", new Study(vkbot));
            
            //FOR ADMINISTRATORS
            add("забань", new Ban(vkbot));
            add("разбань", new Unban(vkbot));
            add("добавь меня в чат", new AddToChat(vkbot));
            add("отправь", new SendDirectly(vkbot));
            add("засыпай", new Sleep(vkbot));
            add("добавь шаблон для мемов", new MemeTemplateAdd(vkbot));
            add("удали шаблон для мемов", new MemeTemplateRemove(vkbot));
            add("добавь модератора", new AddModerator(vkbot));
            add("удали модератора", new RemoveModerator(vkbot));
            
            //SECRETS
            add("скаков", new McSkakov(vkbot));
            add("swag", new SWAG(vkbot));
            add("скалениум", new Skalenium(vkbot));
            add("88005553535", new Telephone(vkbot));
            
            //CUSTOM
            try(ResultSet set = vkbot.getConnector().query("SELECT * FROM vkbot_text_modules")) {
                while(set.next()) {
                    String name = set.getString(1);
                    handlers.put(name, new CustomHandler(vkbot, name, set.getString(2)));
                }
            }catch(SQLException ex) {
                Logger.warn("Could not load text handlers!", ex);
            }
        }
        
        private void add(String key, BotModule module) {
            handlers.put(key, module);
        }
        
        public Set<String> getHandlers() {
            return handlers.keySet();
        }
        
        public void createHandler(String name, String text) {
            name = "#" + name;
            text = text.replace("|", "");
            if(handlers.containsKey(name))
                ((CustomHandler) handlers.get(name)).update(text);
            else {
                handlers.put(name, new CustomHandler(vkbot, name, text));
                vkbot.getConnector().addToQueue("INSERT INTO vkbot_text_modules VALUES ('%s', '%s')", name, text);
            }
        }
        
        public BotModule getHandler(String name) {
            return (BotModule) handlers.get(name);
        }
        
    }
    
}
