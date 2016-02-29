package ru.ifmo.vkbot.utils.sql;

/**
 *
 * @author RinesThaix
 */
public class TablesLoader {

    public static void load(Connector connector) {
        connector.query(
                "CREATE TABLE IF NOT EXISTS " + 
                "vkbot_logger" +
                " (" +
                "uid bigint(18) NOT NULL," +
                "rank int(1) NOT NULL DEFAULT 0," +
                "date bigint(18) NOT NULL," +
                "command text(0)," +
                "PRIMARY KEY (date), UNIQUE(date)" +
                ") ENGINE=InnoDB CHARACTER SET utf8 COLLATE utf8_general_ci"
        );
        connector.query(
                "CREATE TABLE IF NOT EXISTS " + 
                "vkbot_text_modules" +
                " (" +
                "name varchar(32) NOT NULL," +
                "answers text(0)," +
                "PRIMARY KEY (name), UNIQUE(name)" +
                ") ENGINE=InnoDB CHARACTER SET utf8 COLLATE utf8_general_ci"
        );
    }
    
}
