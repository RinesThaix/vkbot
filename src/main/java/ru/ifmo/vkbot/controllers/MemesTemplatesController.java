package ru.ifmo.vkbot.controllers;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import ru.ifmo.vkbot.utils.ImagesWorker;

/**
 *
 * @author RinesThaix
 */
public class MemesTemplatesController {
    
    private final Set<String> templates = new HashSet<>();

    public MemesTemplatesController() {
        File folder = new File("templates");
        for(File f : folder.listFiles())
            if(f.getName().endsWith(".jpg"))
                templates.add(f.getName().replace(".jpg", ""));
    }
    
    public Collection<String> getTemplates() {
        return templates;
    }
    
    public boolean exists(String template) {
        return templates.contains(template);
    }
    
    public void addTemplate(String template, String src) {
        templates.add(template);
        ImagesWorker.download(src, template + ".jpg");
    }
    
    public void removeTemplate(String template) {
        templates.remove(template);
        ImagesWorker.delete(template + ".jpg");
    }
    
}
