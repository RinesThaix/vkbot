package ru.ifmo.vkbot.utils.sql;

public class ConnectorBuilder {

    private Connector connector;
    private String name;
    private String host;
    private String user;
    private String password;
    private String database;
    private boolean autoReconnect = true;
    private int retries = 10;
    private CharacterEncoding encoding = CharacterEncoding.CP1251;

    public ConnectorBuilder() {}

    public ConnectorBuilder(String name, String host, String user, String password, String database) {
        this.name = name;
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
    }

    public String getName() {
        return this.name;
    }

    public ConnectorBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public String getHost() {
        return this.host;
    }

    public ConnectorBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public String getUser() {
        return this.user;
    }

    public ConnectorBuilder setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public ConnectorBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDatabase() {
        return this.database;
    }

    public ConnectorBuilder setDatabase(String database) {
        this.database = database;
        return this;
    }

    public CharacterEncoding getCharacterEncoding() {
        return this.encoding;
    }

    public ConnectorBuilder setCharacterEncoding(CharacterEncoding encoding) {
        this.encoding = encoding;
        return this;
    }

    public ConnectorBuilder setAutoReconnect(boolean reconnect) {
        this.autoReconnect = reconnect;
        return this;
    }

    public ConnectorBuilder setAutoReconnectRetries(int retries) {
        if ((retries < 0) || (retries > 40)) {
            throw new IllegalArgumentException("Retries number must be in 0-40 range");
        }
        if (!this.autoReconnect) {
            throw new IllegalStateException("Autoreconnect set to false.");
        }
        this.retries = retries;
        return this;
    }

    public Connector build(boolean initialize) {
        if ((this.host == null) || (this.name == null) || (this.user == null) || (this.password == null) || (this.database == null)) {
            throw new IllegalStateException("Required fields not set!");
        }
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("jdbc:mysql://");
        urlBuilder.append(this.host);
        urlBuilder.append('/');
        urlBuilder.append(this.database);
        urlBuilder.append("?characterEncoding=");
        urlBuilder.append(this.encoding);
        if (this.autoReconnect) {
            urlBuilder.append("&autoReconnect=true&maxReconnects=");
            urlBuilder.append(this.retries);
        }
        this.connector = new Connector(this.name, this.user, this.password, urlBuilder.toString());
        if (initialize) {
            this.connector.initialize();
        }
        return this.connector;
    }

    public static enum CharacterEncoding {

        UTF8("utf-8"), CP1251("cp1251");

        private String name;

        private CharacterEncoding(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}
