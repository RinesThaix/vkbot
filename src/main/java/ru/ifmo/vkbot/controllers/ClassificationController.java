package ru.ifmo.vkbot.controllers;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.utils.Logger;
import ru.ifmo.vkbot.utils.Pair;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author RinesThaix
 */
public class ClassificationController implements Serializable {

    private final static String name = "vkbot.wdb";
    
    private final int handlersSize;
    private final Instances data;
    private final StringToWordVector filter = new StringToWordVector();
    private final Classifier cls = new J48();
    private boolean isUpToDate;
    
    private ClassificationController() {
        Set<String> handlers = VkBot.getInstance().getMessagesController().getLinker().getHandlers();
        this.handlersSize = handlers.size();
        FastVector attributes = new FastVector(handlersSize);
        attributes.addElement(new Attribute("Message", (FastVector) null));
        FastVector classes = new FastVector(handlersSize);
        for(String handler : handlers)
            classes.addElement(handler);
        attributes.addElement(new Attribute("Class", classes));
        data = new Instances(name, attributes, 10000);
        data.setClassIndex(data.numAttributes() - 1);
        
        for(String key : handlers)
            study(key, key);
    }
    
    private final List<Pair<String, String>> toRemember = new ArrayList();
    
    public void study(String message, String handler) {
        try {
            message = message.toLowerCase();
            toRemember.add(new Pair(message, handler));
            Instance instance = makeInstance(message, data);
            instance.setClassValue(handler);
            data.add(instance);
            isUpToDate = false;
        }catch(Exception ex) {
            Logger.warn("Could not study for \"" + handler + "\"!", ex);
        }
    }
    
    public String classify(String message) {
        message = message.toLowerCase();
        try {
            if(!isUpToDate) {
                filter.setInputFormat(data);
                Instances filteredData = Filter.useFilter(data, filter);
                cls.buildClassifier(filteredData);
                isUpToDate = true;
            }
            Instances testset = data.stringFreeStructure();
            Instance instance = makeInstance(message, testset);
            filter.input(instance);
            instance = filter.output();
            return data.classAttribute().value((int) cls.classifyInstance(instance));
        }catch(Exception ex) {
            Logger.warn("Could not classify message!", ex);
            return "null";
        }
    }
    
    public static ClassificationController load() {
        ClassificationController cc = new ClassificationController();
        try {
            Scanner scan = new Scanner(new FileReader("vkbot.storage"));
            while(scan.hasNextLine()) {
                String[] args = scan.nextLine().split("\\|");
                cc.study(args[0], args[1]);
            }
        }catch(Exception ex) {
            Logger.warn("Could not load and pre-teach ClassificationController!", ex);
        }
        return cc;
        //For future usage
//        try {
//            ObjectInputStream modelInObjectFile = new ObjectInputStream(new FileInputStream(name));
//            ClassificationController cc = (ClassificationController) modelInObjectFile.readObject();
//            modelInObjectFile.close();
//            return cc;
//        }catch(Exception ex) {
//            return new ClassificationController();
//        }
    }
    
    public void save() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("vkbot.storage"));
            for(Pair<String, String> p : toRemember)
                pw.println(p.getA() + "|" + p.getB());
            pw.close();
        }catch(Exception ex) {
            Logger.warn("Could not save classification data!", ex);
        }
        //For future usage
//        try {
//            ObjectOutputStream modelOutObjectFile = new ObjectOutputStream(new FileOutputStream(name));
//            modelOutObjectFile.writeObject(VkBot.getInstance().getClassificationController());
//            modelOutObjectFile.close();
//        }catch(Exception ex) {
//            Logger.warn("Could not serialize ClassificationController!", ex);
//        }
    }
    
    public Classifier getClassifier() {
        return cls;
    }
    
    private Instance makeInstance(String text, Instances data) {
        Instance instance = new Instance(2);
        Attribute messageAtt = data.attribute("Message");
        instance.setValue(messageAtt, messageAtt.addStringValue(text));
        instance.setDataset(data);
        return instance;
    }
    
}
