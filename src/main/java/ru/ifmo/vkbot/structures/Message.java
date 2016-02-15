package ru.ifmo.vkbot.structures;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author RinesThaix
 */
public class Message {

    private String message;
    private final long messageId;
    private final long senderId;
    private final long dialogId;
    private long[] members = null;
    private final Set<Photo> photos = new HashSet();
    private final Set<Audio> audios = new HashSet();
    private final Set<Video> videos = new HashSet();
    
    public Message(String message, long messageId, long sender, long dialog) {
        this.message = message;
        this.messageId = messageId;
        this.senderId = sender;
        this.dialogId = dialog;
    }
    
    public String getMessage() {
        return message;
    }
    
    public long getSender() {
        return senderId;
    }
    
    public long getDialog() {
        return dialogId;
    }
    
    public long getMessageId() {
        return messageId;
    }
    
    public void setMembers(long... members) {
        this.members = members;
    }
    
    public long[] getMembers() {
        return members;
    }
    
    public void updateMessage(String msg) {
        this.message = msg;
    }
    
    public void addPhoto(Photo p) {
        photos.add(p);
    }
    
    public void addAudio(Audio a) {
        audios.add(a);
    }
    
    public void addVideo(Video v) {
        videos.add(v);
    }
    
    public Collection<Photo> getPhotos() {
        return photos;
    }
    
    public Collection<Video> getVideos() {
        return videos;
    }
    
    public Collection<Audio> getAudios() {
        return audios;
    }
    
}
