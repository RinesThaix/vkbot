package ru.ifmo.vkbot.modules;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;

/**
 *
 * @author RinesThaix
 */
public class MD5 extends BotModule {

    public MD5(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        String s = toString(args);
        if(s.isEmpty())
            return;
        getMC().send(m.getDialog(), "MD5(" + s + "): " + md5(s));
    }
    
    private static String md5(String text) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] array = md5.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < array.length; ++i)
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            return sb.toString().toUpperCase();
        }catch(NoSuchAlgorithmException ex) {}
        return null;
    }

}
