package ru.ifmo.vkbot.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;
import javax.imageio.ImageIO;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import ru.ifmo.vkbot.VkBot;

/**
 *
 * @author RinesThaix
 */
public class ImagesWorker {
    
    private final static File folder = new File("temp"), templates = new File("templates");
    private final static Random r = new Random();

    public static long upload(String name) {
        File f = new File(folder, name);
        if(!f.exists())
            return -1;
        String line = PostExecutor.buildAndGet("photos.getMessagesUploadServer");
        try {
            JSONObject answer = (JSONObject) ((JSONObject) VkBot.getParser().parse(line)).get("response");
            String url = (String) answer.get("upload_url");
            url += "&photo=" + PostExecutor.encode(f.getAbsolutePath());
            URL URL = new URL(url);
            MultipartUtility mu = new MultipartUtility(URL);
            mu.addFilePart("photo", f);
            final byte[] bytes = mu.finish();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
            line = reader.readLine();
            reader.close();
            answer = (JSONObject) VkBot.getParser().parse(line);
            long server = (long) answer.get("server");
            String photo = (String) answer.get("photo");
            String hash = (String) answer.get("hash");
            JSONArray array = VkBot.parse(PostExecutor.buildAndGet("photos.saveMessagesPhoto", "server", server, "photo", photo, "hash", hash));
            long pid = (long) ((JSONObject) array.get(0)).get("pid");
            f.delete();
            return pid;
        }catch(IOException | ParseException ex) {
            Logger.warn("Could not upload image!", ex);
            return 0;
        }
    }
    
    public static void download(String src, String name) {
        if(name == null) {
            String[] spl = src.split("[\\.\\/]");
            name = spl[spl.length - 2] + "." + spl[spl.length - 1];
        }
        try {
            BufferedImage image = ImageIO.read(new URL(src));
            ImageIO.write(image, "jpg", new File(templates, name));
        }catch(Exception ex) {
            Logger.warn("Could not download image from " + src + "!", ex);
        }
    }
    
    public static void delete(String template) {
        new File(templates, template).delete();
    }
    
    public static long addTextRemotely(String url, String textU, String textD) {
        try {
            String[] spl = url.split("[\\.\\/]");
            String name = spl[spl.length - 2] + "." + spl[spl.length - 1];
            BufferedImage image = ImageIO.read(new URL(url));
            Graphics g = image.getGraphics();
            Font f = new Font("Impact", 1, 60);
            g.setFont(f);
            float size = 60f;
            g.setFont(g.getFont().deriveFont(size));
            while(g.getFontMetrics().stringWidth(textU) >= image.getWidth())
                g.setFont(g.getFont().deriveFont(size /= 2));
            int width = g.getFontMetrics().stringWidth(textU);
            printWithOutline(g, textU, (image.getWidth() - width) >> 1, 50, 3, Color.WHITE, Color.BLACK);
            g.drawString(textU, (image.getWidth() - width) >> 1, 50);
            size = 60f;
            g.setFont(g.getFont().deriveFont(size));
            while(g.getFontMetrics().stringWidth(textD) >= image.getWidth())
                g.setFont(g.getFont().deriveFont(size /= 2));
            width = g.getFontMetrics().stringWidth(textD);
            g.setColor(Color.BLACK);
            printWithOutline(g, textD, (image.getWidth() - width) >> 1, image.getHeight() - 50, 3, Color.WHITE, Color.BLACK);
            g.dispose();
            ImageIO.write(image, spl[spl.length - 1], new File(folder, name));
            return upload(name);
        }catch(Exception ex) {
            Logger.warn("Could not add text to remote image!");
        }
        return 0;
    }
    
    public static long addText(String template, String textU, String textD) {
        try {
            BufferedImage image = ImageIO.read(new File(templates, template + ".jpg"));
            Graphics g = image.getGraphics();
            Font f = new Font("Impact", 1, 60);
            g.setFont(f);
            float size = 60f;
            g.setFont(g.getFont().deriveFont(size));
            while(g.getFontMetrics().stringWidth(textU) >= image.getWidth())
                g.setFont(g.getFont().deriveFont(size /= 2));
            int width = g.getFontMetrics().stringWidth(textU);
            printWithOutline(g, textU, (image.getWidth() - width) >> 1, 50, 3, Color.WHITE, Color.BLACK);
            g.drawString(textU, (image.getWidth() - width) >> 1, 50);
            size = 60f;
            g.setFont(g.getFont().deriveFont(size));
            while(g.getFontMetrics().stringWidth(textD) >= image.getWidth())
                g.setFont(g.getFont().deriveFont(size /= 2));
            width = g.getFontMetrics().stringWidth(textD);
            g.setColor(Color.BLACK);
            printWithOutline(g, textD, (image.getWidth() - width) >> 1, image.getHeight() - 50, 3, Color.WHITE, Color.BLACK);
            g.dispose();
            String name = r.nextInt(1000000000) + ".jpg";
            ImageIO.write(image, "jpg", new File(folder, name));
            return upload(name);
        }catch(Exception ex) {
            Logger.warn("Could not add text to remote image!");
        }
        return 0;
    }
    
    private static void printWithOutline(Graphics g, String text, int x, int y, int width, Color in, Color out) {
        g.setColor(out);
        g.drawString(text, NW(x, width), NW(y, width));
        g.drawString(text, NW(x, width), SE(y, width));
        g.drawString(text, SE(x, width), NW(y, width));
        g.drawString(text, SE(x, width), SE(y, width));
        g.setColor(in);
        g.drawString(text, x, y);
    }
    
    private static int NW(int p, int distance) {
        return p - distance;
    }
    
    private static int SE(int p, int distance) {
        return p + distance;
    }
    
}
