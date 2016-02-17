package ru.ifmo.vkbot.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import ru.ifmo.vkbot.VkBot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Random;

/**
 *
 * @author RinesThaix
 */
public class ImagesWorker {
    
    private final static File folder = new File("temp"), templates = new File("templates");
    private final static Random r = new Random();

    public static long upload(File file) {
        if (!file.exists())
            return -1;
        String line = PostExecutor.buildAndGet("photos.getMessagesUploadServer");
        try {
            JSONObject answer = (JSONObject) ((JSONObject) VkBot.getParser().parse(line)).get("response");
            String url = (String) answer.get("upload_url");
            url += "&photo=" + PostExecutor.encode(file.getAbsolutePath());
            URL URL = new URL(url);
            MultipartUtility mu = new MultipartUtility(URL);
            mu.addFilePart("photo", file);
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
            file.delete();
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
            return upload(addTextAndSave(image, name, spl[spl.length - 1], textU, textD));
        }catch(Exception ex) {
            Logger.warn("Could not add text to remote image!");
        }
        return 0;
    }

    public static long addText(String template, String textU, String textD) {
        try {
            BufferedImage image = ImageIO.read(new File(templates, template + ".jpg"));
            String name = r.nextInt(1000000000) + ".jpg";
            return upload(addTextAndSave(image, name, "jpg", textU, textD));
        }catch(Exception ex) {
            Logger.warn("Could not add text to remote image!");
        }
        return 0;
    }

    private static File addTextAndSave(BufferedImage image, String name, String ext, String textU, String textD) throws IOException {
        textU = textU.toUpperCase();
        textD = textD.toUpperCase();

        Graphics g = image.getGraphics();
        Font f = new Font("Impact", Font.BOLD, 60);
        g.setFont(f);
        float size = 60f;
        g.setFont(g.getFont().deriveFont(size));
        while (g.getFontMetrics().stringWidth(textU) >= image.getWidth()) {
            g.setFont(g.getFont().deriveFont(size /= 2));
        }
        int width = g.getFontMetrics().stringWidth(textU);
        int height = g.getFontMetrics().getHeight();
        float offsetUpY = image.getHeight() * 0.05f + height;
        float offsetDownY = image.getHeight() * 0.95f;
        printWithOutline(g, textU, (image.getWidth() - width) >> 1, (int) offsetUpY, 3, Color.WHITE, Color.BLACK);
        g.drawString(textU, (image.getWidth() - width) >> 1, 50);
        size = 60f;
        g.setFont(g.getFont().deriveFont(size));
        while (g.getFontMetrics().stringWidth(textD) >= image.getWidth()) {
            g.setFont(g.getFont().deriveFont(size /= 2));
        }
        width = g.getFontMetrics().stringWidth(textD);
        g.setColor(Color.BLACK);
        printWithOutline(g, textD, (image.getWidth() - width) >> 1, (int) offsetDownY, 3, Color.WHITE, Color.BLACK);
        g.dispose();
        File fi = new File(folder, name);
        ImageIO.write(image, ext, fi);
        return fi;
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
