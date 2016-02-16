package ru.ifmo.vkbot.utils.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import ru.ifmo.vkbot.utils.Logger;

public class Connector extends Thread {

    private static List<Connector> list = new LinkedList();
    private String user;
    private String pass;
    private String url;
    private Connection connection;

    public static void shutdownAll() {
        for (Connector c : list) {
            c.shutdown();
            c.close();
            c.interrupt();
        }
    }

    private AtomicBoolean queryInProgress = new AtomicBoolean(false);
    private AtomicBoolean queryAddEnabled = new AtomicBoolean(true);
    private BlockingQueue<String> queryQueue = new ArrayBlockingQueue(128);

    @Deprecated
    public Connector(String threadName, String user, String pass, String url) {
        super(threadName);
        this.user = user;
        this.pass = pass;
        this.url = url;
    }

    public boolean addToQueue(String query) {
        if (this.queryAddEnabled.get()) {
            return this.queryQueue.offer(query);
        }
        throw new IllegalStateException("This connector isn't accepting queries");
    }

    public boolean addToQueue(String query, Object... args) {
        return addToQueue(String.format(query, args));
    }

    public void run() {
        Logger.log("Started database thread.");
        try {
            for (;;) {
                this.queryInProgress.set(true);
                String query = (String) this.queryQueue.take();
                query(query);
                this.queryInProgress.set(false);
            }
        } catch (InterruptedException e) {
        }
    }

    public boolean shutdown() {
        this.queryAddEnabled.set(false);
        synchronized (Connector.class) {
            while (!this.queryQueue.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return true;
    }

    public boolean initialize() {
        if (!checkConnection()) {
            Logger.log("[%s] Unable to connect to database!", getName());
            return false;
        }
        Logger.log("[%s] Successfully connected to database!", getName());
        start();
        return true;
    }

    private boolean checkDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException e) {
            Logger.warn("MySQL driver class missing: " + e.getMessage() + "!");
        }
        return false;
    }

    public boolean checkConnection() {
        return open() != null;
    }

    public Connection open() {
        if (!checkDriver()) {
            return null;
        }
        try {
            if (this.connection == null) {
                return DriverManager.getConnection(this.url, this.user, this.pass);
            }
            if (this.connection.isValid(0)) {
                return this.connection;
            }
            return DriverManager.getConnection(this.url, this.user, this.pass);
        } catch (SQLException e) {
            Logger.warn("Could not be resolved because of an SQL Exception!", e);
        }
        return null;
    }

    public boolean close() {
        this.connection = open();
        try {
            if (this.connection != null) {
                this.connection.close();
                this.connection = null;
                return true;
            }
        } catch (Exception e) {
            Logger.warn("Failed to close database connection!", e);
        }
        return false;
    }

    public ResultSet query(String query, Object... args) {
        return query(String.format(query, args));
    }

    public ResultSet query(String query) {
        Statement statement = null;
        ResultSet result = null;
        this.queryInProgress.set(true);
        try {
            this.connection = open();
            statement = this.connection.createStatement();
            if (getStatement(query).equals(Statements.SELECT)) {
                result = statement.executeQuery(query);
                this.queryInProgress.set(false);
                return result;
            }
            if (getStatement(query).equals(Statements.INSERT)) {
                statement.executeUpdate(query, 1);
                result = statement.getGeneratedKeys();
                this.queryInProgress.set(false);
                return result;
            }
            statement.executeUpdate(query);
            this.queryInProgress.set(false);
            return result;
        } catch (SQLException e) {
            Logger.warn("Error in SQL query: " + query + "!", e);
            this.queryInProgress.set(false);
        }
        return result;
    }

    public int updateQuery(String query) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = open();
            statement = connection.createStatement();
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            Logger.warn("Error in SQL query: " + query + "!", e);
        }
        return 0;
    }

    public PreparedStatement prepare(String query) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = open();
            return connection.prepareStatement(query);
        } catch (SQLException e) {
            if (!e.toString().contains("not return ResultSet")) {
                Logger.warn("Error in SQL prepare-query: " + query + "!", e);
            }
        }
        return ps;
    }

    public boolean createTable(String query) {
        Statement statement = null;
        try {
            this.connection = open();
            if (query.equals("")) {
                Logger.warn("SQL query empty: createTable(: " + query + ")!");
                return false;
            }
            statement = this.connection.createStatement();
            statement.execute(query);
            return true;
        } catch (SQLException e) {
            Logger.warn("SQL Error occured!", e);
            return false;
        } catch (Exception e) {
            Logger.warn("SQL Error occured!", e);
        }
        return false;
    }

    public boolean checkTable(String table) {
        try {
            this.connection = open();
            if (this.connection == null) {
                Logger.warn("Unable to check whether tables exist!");
                return false;
            }
            Statement statement = this.connection.createStatement();

            ResultSet result = statement.executeQuery("SELECT * FROM " + table);
            if (result == null) {
                return false;
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("exist")) {
                return false;
            }
            Logger.warn("Error in SQL query!", e);
            if (query("SELECT * FROM " + table) == null) {
                return true;
            }
        }
        return false;
    }

    public boolean colExists(String table, String column) {
        try {
            this.connection = open();
            if (this.connection == null) {
                Logger.warn("Unable to check whether tables exist!");
                return false;
            }
            DatabaseMetaData metadata = this.connection.getMetaData();

            ResultSet result = metadata.getColumns(null, null, table, column);
            if (result == null) {
                return false;
            }
            result.next();
            result.close();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("exist")) {
                return false;
            }
            Logger.warn("Error in SQL query!", e);
        }
        return false;
    }

    protected Statements getStatement(String query) {
        String trimmedQuery = query.trim();
        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("SELECT")) {
            return Statements.SELECT;
        }
        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("INSERT")) {
            return Statements.INSERT;
        }
        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("UPDATE")) {
            return Statements.UPDATE;
        }
        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("DELETE")) {
            return Statements.DELETE;
        }
        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("CREATE")) {
            return Statements.CREATE;
        }
        if (trimmedQuery.substring(0, 5).equalsIgnoreCase("ALTER")) {
            return Statements.ALTER;
        }
        if (trimmedQuery.substring(0, 4).equalsIgnoreCase("DROP")) {
            return Statements.DROP;
        }
        if (trimmedQuery.substring(0, 8).equalsIgnoreCase("TRUNCATE")) {
            return Statements.TRUNCATE;
        }
        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("RENAME")) {
            return Statements.RENAME;
        }
        if (trimmedQuery.substring(0, 2).equalsIgnoreCase("DO")) {
            return Statements.DO;
        }
        if (trimmedQuery.substring(0, 7).equalsIgnoreCase("REPLACE")) {
            return Statements.REPLACE;
        }
        if (trimmedQuery.substring(0, 4).equalsIgnoreCase("LOAD")) {
            return Statements.LOAD;
        }
        if (trimmedQuery.substring(0, 7).equalsIgnoreCase("HANDLER")) {
            return Statements.HANDLER;
        }
        if (trimmedQuery.substring(0, 4).equalsIgnoreCase("CALL")) {
            return Statements.CALL;
        }
        return Statements.SELECT;
    }

    protected static enum Statements {

        SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, CREATE, ALTER, DROP, TRUNCATE, RENAME;

        private Statements() {
        }
    }
}
