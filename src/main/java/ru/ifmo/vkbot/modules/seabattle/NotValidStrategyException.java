package ru.ifmo.vkbot.modules.seabattle;

import static ru.ifmo.vkbot.modules.seabattle.AbstractStrategy.*;

/**
 *
 * @author RinesThaix
 */
public class NotValidStrategyException extends SeaBattleException {
    
    public NotValidStrategyException(int[][] matrix) {
        super(parse(matrix));
    }
    
    private static String parse(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        sb.append("You placed ships wrong:\n");
        for(int i = 0; i < 10; ++i) {
            for(int j = 0; j < 10; ++j)
                sb.append(matrix[i][j] == PLACED_SHIP ? "X" : ".").append(" ");
            sb.append("\n");
        }
        return sb.toString();
    }

}
