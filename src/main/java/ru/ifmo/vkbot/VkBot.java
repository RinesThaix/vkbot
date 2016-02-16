package ru.ifmo.vkbot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import ru.ifmo.vkbot.controllers.*;
import ru.ifmo.vkbot.modules.Administration;
import ru.ifmo.vkbot.modules.Staff;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.utils.Configuration;
import ru.ifmo.vkbot.utils.Logger;
import ru.ifmo.vkbot.utils.PostExecutor;
import ru.ifmo.vkbot.utils.sql.Connector;
import ru.ifmo.vkbot.utils.sql.ConnectorBuilder;

/**
 *
 * @author RinesThaix
 */
public class VkBot {

    private final static String version = "1.1";
    private final static JSONParser parser = new JSONParser();
    
    private static VkBot instance;
    
    private Configuration config;
    private String access_token;
    private int assistant_id;
    private final List<Long> administration = new ArrayList(), staff = new ArrayList();
    
    private MessagesController msgc;
    private BanController banc;
    private FriendsController friendsc;
    private MemesTemplatesController mtc;
    
    private Connector connector;
    
    public VkBot() {
        instance = this;
        enable();
    }
    
    private void enable() {
        Logger.log("Enabling VkBot v%s", version);
        try {
            Logger.log("Loading configuration file..");
            config = new Configuration("config");
            access_token = config.getString("access_token", "0000");
            assistant_id = config.getInt("assistant_id", 0);
            List admins = new ArrayList();
            admins.add("59649933");
            admins = config.getList("administration", admins);
            List staff = new ArrayList();
            staff.add("1");
            staff = config.getList("staff", staff);
            Logger.log("Loading database properties..");
            String host = config.getString("db.host", "localhost");
            String user = config.getString("db.user", "root");
            String pass = config.getString("db.pass", "root");
            String db = config.getString("db.database", "vkbot");
            connector = new ConnectorBuilder("db", host, user, pass, db).build(true);
            config.save();
            for(Object s : admins)
                administration.add(Long.parseLong((String) s));
            for(Object s : staff)
                this.staff.add(Long.parseLong((String) s));
            Logger.log("Preloading administration & staff names..");
            Administration.loadAdmins(this.administration);
            Staff.loadStaff(this.staff);
        }catch(IOException | NumberFormatException ex) {
            Logger.warn("Could not read configuration file! Shutting down!", ex);
            System.exit(0);
        }
        Logger.log("Preparing all of controllers..");
        this.friendsc = new FriendsController(this);
        this.banc = new BanController();
        this.msgc = new MessagesController(this);
        this.mtc = new MemesTemplatesController();
        PostExecutor.buildAndGet("status.set", "text", PostExecutor.encode("Милаша v%s", version));
        
        Logger.log("Initialization completed! Now starting reading from console.");
        Scanner scan = new Scanner(System.in, "UTF-8");
        while (true)
            getMessagesController().getLinker().handle(new Message(scan.nextLine(), 0, 0, 0));
    }
    
    public void disable() {
        Logger.log("Disabling VkBot v%s..", version);
        this.msgc.disable();
        try {
            Thread.sleep(1000l);
        }catch(InterruptedException ex) {}
        System.exit(0);
    }
    
    public String getAccessToken() {
        return access_token;
    }
    
    public long getAssistantId() {
        return assistant_id;
    }
    
    public Configuration getConfig() {
        return config;
    }
    
    public MessagesController getMessagesController() {
        return msgc;
    }
    
    public BanController getBanController() {
        return banc;
    }
    
    public FriendsController getFriendsController() {
        return friendsc;
    }
    
    public MemesTemplatesController getMemesTemplatesController() {
        return mtc;
    }
    
    public Connector getConnector() {
        return connector;
    }
    
    public boolean isAdministrator(long uid) {
        return administration.contains(uid) || uid == 0;
    }
    
    public boolean isModerator(long uid) {
        return staff.contains(uid) || isAdministrator(uid);
    }
    
    public static VkBot getInstance() {
        return instance;
    }
    
    public static JSONParser getParser() {
        return parser;
    }
    
    public static JSONArray parse(String answer) {
        try {
            return (JSONArray) ((JSONObject) parser.parse(answer)).get("response");
        }catch(Exception ex) {
            Logger.warn("Could not parse answer-line: " + answer);
            return null;
        }
    }
    
}
