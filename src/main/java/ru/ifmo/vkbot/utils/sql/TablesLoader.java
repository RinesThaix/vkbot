package ru.ifmo.vkbot.utils.sql;

/**
 *
 * @author RinesThaix
 */
public class TablesLoader {

    public static void load(Connector c) {
        StringBuilder tableConstructor = new StringBuilder();
        tableConstructor.append("CREATE TABLE IF NOT EXISTS ");
        tableConstructor.append("vkbot_logger");
        tableConstructor.append(" (");
        tableConstructor.append("uid bigint(18) NOT NULL,");
        tableConstructor.append("rank int(1) NOT NULL DEFAULT 0,");
        tableConstructor.append("date bigint(18) NOT NULL,");
        tableConstructor.append("command text(0),");
        tableConstructor.append("PRIMARY KEY (date), UNIQUE(date)");
        tableConstructor.append(") ENGINE=InnoDB CHARACTER SET utf8 COLLATE utf8_general_ci");
        c.query(tableConstructor.toString());
        
        tableConstructor = new StringBuilder();
        tableConstructor.append("CREATE TABLE IF NOT EXISTS ");
        tableConstructor.append("vkbot_text_modules");
        tableConstructor.append(" (");
        tableConstructor.append("name varchar(32) NOT NULL,");
        tableConstructor.append("answers text(0),");
        tableConstructor.append("PRIMARY KEY (name), UNIQUE(name)");
        tableConstructor.append(") ENGINE=InnoDB CHARACTER SET utf8 COLLATE utf8_general_ci");
        c.query(tableConstructor.toString());
    }
    
}
