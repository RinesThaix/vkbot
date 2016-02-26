package ru.ifmo.vkbot.modules.seabattle;

/**
 *
 * @author RinesThaix
 */
public class NotValidMoveException extends SeaBattleException {

    private boolean already = false;
    
    public NotValidMoveException(int x, int y, boolean already) {
        super((already ? "You already moved here: " : "It's not a possible move: ") + "(" + x + ";" + y + ")");
        this.already = already;
    }
    
    public boolean isAlreadyMovedHere() {
        return already;
    }
    
}
