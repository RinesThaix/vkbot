package ru.ifmo.vkbot.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author RinesThaix
 */
public class Vote {

    private final String name;
    private final Map<Integer, Integer> results;
    private final Map<Integer, String> options;
    private final Map<Long, Integer> votes = new HashMap<>();
    private final long creationTime;
    
    public Vote(String name, String... options) {
        this.name = name;
        this.results = new HashMap<>(options.length);
        this.options = new HashMap<>(options.length);
        for(int i = 0; i < options.length; ++i) {
            this.options.put(i, options[i]);
            this.results.put(i, 0);
        }
        this.creationTime = System.currentTimeMillis();
    }
    
    public boolean vote(long uid, int option) {
        if(votes.containsKey(uid)) {
            int previous = votes.get(uid);
            votes.put(uid, option);
            results.put(previous, results.get(previous) - 1);
            results.put(option, results.get(option) + 1);
            return false;
        }
        votes.put(uid, option);
        results.put(option, results.get(option) + 1);
        return true;
    }
    
    public int getOptionsSize() {
        return options.size();
    }
    
    public long getCreationTime() {
        return creationTime;
    }
    
    public String getName() {
        return name;
    }
    
    public List<String> getOptions() {
        List<String> list = new ArrayList();
        for(int i = 0; options.containsKey(i); ++i)
            list.add(options.get(i));
        return list;
    }
    
    public List<Integer> getResults() {
        List<Integer> list = new ArrayList();
        for(int i = 0; options.containsKey(i); ++i)
            list.add(results.get(i));
        return list;
    }
    
}
