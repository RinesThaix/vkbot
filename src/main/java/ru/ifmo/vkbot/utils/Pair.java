package ru.ifmo.vkbot.utils;

/**
 *
 * @author RinesThaix
 */
public class Pair<A, B> {

    private A a = null;
    private B b = null;
    
    public Pair() {}
    
    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }
    
    public A getA() {
        return a;
    }
    
    public B getB() {
        return b;
    }
    
}
