package ru.ifmo.vkbot.modules.seabattle;

/**
 *
 * @author RinesThaix
 */
public class NotValidMoveException extends SeaBattleException {

    private boolean already = false;
    
    public NotValidMoveException(boolean already) {
        super(already ? "You already moved here." : "It's not a possible move.");
        this.already = already;
    }
    
    public boolean isAlreadyMovedHere() {
        return already;
    }
    
}
