package ru.ifmo.vkbot.modules;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.utils.Logger;

/**
 *
 * @author RinesThaix
 */
public class CustomHandler extends BotModule {
    
    private final List<String> answers = new ArrayList();
    private final String name;

    public CustomHandler(VkBot vkbot, String name) {
        super(vkbot);
        this.name = name;
        try(ResultSet set = vkbot.getConnector().query("SELECT answers FROM vkbot_text_modules WHERE name='" + name + "'")) {
            if(set.next())
                answers.addAll(Arrays.asList(set.getString(1).split("\\|")));
            else
                answers.add("Я.. я не знаю, что ответить  :(");
        }catch(SQLException ex) {
            Logger.warn("Could not load CustomHandler names \"" + name + "\"!", ex);
        }
    }
    
    public CustomHandler(VkBot vkbot, String name, String ans) {
        super(vkbot);
        this.name = name;
        answers.addAll(Arrays.asList(ans.split("\\|")));
    }

    @Override
    public void handle(Message m, String[] args) {
        getMC().send(m.getDialog(), answers.get(getRandom().nextInt(answers.size())));
    }
    
    public void update(String newAnswer) {
        answers.add(newAnswer);
        StringBuilder sb = new StringBuilder();
        for(String s : answers)
            sb.append(s).append("|");
        getVkBot().getConnector().addToQueue("UPDATE vkbot_text_modules SET answers='%s' WHERE name='%s'",
                sb.toString().trim(), name);
    }

}
