package ru.ifmo.vkbot.structures;

/**
 *
 * @author RinesThaix
 */
public class Audio {

    private final long owner_id;
    private final long aid;
    private final String src;
    
    public Audio(long owner_id, long aid, String src) {
        this.owner_id = owner_id;
        this.aid = aid;
        this.src = src;
    }
    
    public long getOwnerId() {
        return owner_id;
    }
    
    public long getAudioId() {
        return aid;
    }
    
    public String getSource() {
        return src;
    }
    
}
