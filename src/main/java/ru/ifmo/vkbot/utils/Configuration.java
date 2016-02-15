package ru.ifmo.vkbot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
    
    private String listToString(List list) {
        StringBuilder sb = new StringBuilder();
        for(Object o : list)
            sb.append(o.toString()).append(" ");
        String val = sb.toString();
        if(!val.isEmpty())
            val = val.substring(0, val.length() - 1);
        return val;
    }
    
    public List getList(String key, List defaultValue) {
        String s = getString(key, null);
        if(s == null) {
            setList(key, defaultValue);
            return defaultValue;
        }
        String[] spl = s.split(" ");
        List l = new ArrayList(spl.length);
        for(String s2 : spl)
            l.add(s2);
        return l;
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
    
}
