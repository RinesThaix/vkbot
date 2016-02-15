package ru.ifmo.vkbot.structures;

/**
 *
 * @author RinesThaix
 */
public class Photo {

    private final long owner_id;
    private final long pid;
    private final String src;
    
    public Photo(long owner_id, long pid, String src) {
        this.owner_id = owner_id;
        this.pid = pid;
        this.src = src;
    }
    
    public long getOwnerId() {
        return owner_id;
    }
    
    public long getPhotoId() {
        return pid;
    }
    
    public String getSource() {
        return src;
    }
    
}
