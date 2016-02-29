package ru.ifmo.vkbot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author RinesThaix
 */
public class Configuration {

    private final File file;
    private final String name;
    private final Properties prop;
    
    public Configuration(String name) throws IOException {
        this.name = name;
        this.file = new File(name + ".properties");
        if(!this.file.exists())
            this.file.createNewFile();
        prop = new Properties();
        prop.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
    }
    
    public void setList(String key, List list) {
        setObject(key, listToString(list));
    }
    
    private String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for(String s : list)
            sb.append(s).append(" ");
        String val = sb.toString();
        if(!val.isEmpty())
            val = val.trim();
        return val;
    }
    
    public List<String> getList(String key, List<String> defaultValue) {
        String s = getString(key, null);
        if (s == null) {
            setList(key, defaultValue);
            return defaultValue;
        }
        return Arrays.asList(s.split(" "));
    }
    
    public int getInt(String key, int defaultValue) {
        Object value = getObject(key, null);
        if(value == null) {
            setObject(key, defaultValue);
            return defaultValue;
        }
        return Integer.parseInt((String) value);
    }
    
    public void setInt(String key, int value) {
        setObject(key, value);
    }
    
    public String getString(String key, String defaultValue) {
        Object value = getObject(key, null);
        if(value == null) {
            if(defaultValue != null)
                setObject(key, defaultValue);
            return defaultValue;
        }
        return (String) value;
    }
    
    public void setString(String key, String value) {
        setObject(key, value);
    }
    
    public Object getObject(String key, Object defaultValue) {
        Object value = prop.get(key);
        if(value == null) {
            if(defaultValue != null)
                setObject(key, defaultValue);
            return defaultValue;
        }
        return value;
    }
    
    public void setObject(String key, Object value) {
        prop.setProperty(key, value.toString());
    }
    
    public void remove(String key) {
        prop.remove(key);
    }
    
    public void save() throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
        prop.store(writer, "");
        writer.close();
    }
    
    public String getName() {
        return name;
    }
    
}
