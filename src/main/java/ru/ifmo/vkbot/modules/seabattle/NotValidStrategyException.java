package ru.ifmo.vkbot.modules.seabattle;

/**
 *
 * @author RinesThaix
 */
public class NotValidStrategyException extends SeaBattleException {
    
    public NotValidStrategyException() {
        super("You placed ships wrong.");
    }

}
