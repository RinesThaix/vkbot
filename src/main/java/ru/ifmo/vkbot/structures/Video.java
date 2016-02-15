package ru.ifmo.vkbot.structures;

/**
 *
 * @author RinesThaix
 */
public class Video {

    private final long owner_id;
    private final long vid;
    private final String src;
    
    public Video(long owner_id, long vid, String src) {
        this.owner_id = owner_id;
        this.vid = vid;
        this.src = src;
    }
    
    public long getOwnerId() {
        return owner_id;
    }
    
    public long getVideoId() {
        return vid;
    }
    
    public String getSource() {
        return src;
    }
    
}
