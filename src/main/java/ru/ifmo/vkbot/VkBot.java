package ru.ifmo.vkbot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import ru.ifmo.vkbot.controllers.*;
import ru.ifmo.vkbot.modules.Administration;
import ru.ifmo.vkbot.modules.BotModule.Group;
import ru.ifmo.vkbot.modules.Staff;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.utils.Configuration;
import ru.ifmo.vkbot.utils.Logger;
import ru.ifmo.vkbot.utils.PostExecutor;
import ru.ifmo.vkbot.utils.sql.Connector;
import ru.ifmo.vkbot.utils.sql.ConnectorBuilder;
import ru.ifmo.vkbot.utils.sql.TablesLoader;

/**
 *
 * @author RinesThaix
 */
public class VkBot {

    private final static String version = "1.1.3";
    private final static JSONParser parser = new JSONParser();

    private static VkBot instance;

    private Configuration config;
    private String access_token;
    private int assistant_id;
    private final List<Long> administration;
    private final List<Long> staff;

    private MessagesController msgc;
    private BanController banc;
    private FriendsController friendsc;
    private MemesTemplatesController mtc;
    private ClassificationController cc;

    private final Connector connector;

    private VkBot() {
        instance = this;
        Logger.log("Enabling VkBot v%s", version);
        try {
            Logger.log("Loading configuration file..");
            config = new Configuration("config");
            access_token = config.getString("access_token", "0000");
            assistant_id = config.getInt("assistant_id", 0);
            
            Logger.log("Preloading administration & staff names..");
            administration = loadGroupMembers("administration", "59649933");
            staff = loadGroupMembers("staff", "1");
            Administration.loadAdmins(this.administration);
            Staff.loadStaff(this.staff);

            Logger.log("Loading database properties..");
            connector = new ConnectorBuilder()
                    .setName("vkbot_connector")
                    .setHost(config.getString("db.host", "localhost") + ":3306")
                    .setUser(config.getString("db.user", "root"))
                    .setPassword(config.getString("db.pass", "root"))
                    .setDatabase(config.getString("db.database", "vkbot"))
                    .setCharacterEncoding(ConnectorBuilder.CharacterEncoding.CP1251)
                    .setAutoReconnect(true)
                    .setAutoReconnectRetries(10)
                    .build(true); 
            TablesLoader.load(connector);
            
            config.save();
        } catch (IOException | NumberFormatException ex) {
            Logger.warn("Could not read configuration file! Shutting down!", ex);
            System.exit(0);
            throw new AssertionError();
        }
        Logger.log("Loading controllers..");
        this.friendsc = new FriendsController(this);
        this.banc = new BanController();
        this.msgc = new MessagesController(this);
        this.mtc = new MemesTemplatesController();
        this.cc = ClassificationController.load();
        
        PostExecutor.buildAndGet("status.set", "text", PostExecutor.encode("Милаша v%s", version));

        Logger.log("Initialization completed! Now starting reading from console.");
        Scanner scan = new Scanner(System.in, "UTF-8");
        while (true) {
            getMessagesController().getLinker().handle(new Message(scan.nextLine(), 0, 0, 0));
        }
    }
    
    private boolean disabled = false;
    
    public void disable() {
        if(disabled)
            return;
        disabled = true;
        
        Logger.log("Disabling VkBot v%s..", version);
        this.msgc.disable();
        this.banc.save();
        this.cc.save();
        Connector.shutdownAll();
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException ex) {
        }
        Logger.log("Good bye.");
        System.exit(0);
    }

    public void reboot() {
        Logger.log("Rebooting VkBot v%s..", version);
        this.cc.save();
        this.cc = ClassificationController.load();
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

    public ClassificationController getClassificationController() {
        return cc;
    }

    public Connector getConnector() {
        return connector;
    }

    public Group getGroup(long uid) {
        return isAdministrator(uid) ? Group.ADMINISTRATOR : isModerator(uid) ? Group.MODERATOR : Group.USER;
    }

    public boolean isAdministrator(long uid) {
        return administration.contains(uid) || uid == 0;
    }

    public boolean isModerator(long uid) {
        return staff.contains(uid) || isAdministrator(uid);
    }

    public List<Long> getAdministrators() {
        return administration;
    }

    public List<Long> getModerators() {
        return staff;
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
        } catch (Exception ex) {
            Logger.warn("Could not parse answer-line: " + answer);
            return null;
        }
    }

    private List<Long> loadGroupMembers(String group, String defaultMember) {
        final List<Long> storage = new ArrayList<>();
        for (String s : config.getList(group, Collections.singletonList(defaultMember))) {
            storage.add(Long.parseLong(s));
        }
        return storage;
    }

    public static void main(String[] args) {
        new VkBot();
    }

}
