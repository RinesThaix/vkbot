package ru.ifmo.vkbot.modules.seabattle;

import java.util.Random;
import ru.ifmo.vkbot.utils.Pair;

/**
 *
 * @author RinesThaix
 */
public class RepeatStrategy extends AbstractStrategy {
    
    public RepeatStrategy(long dialog, long mid) {
        super(dialog, mid);
    }

    @Override
    protected void placeShips() {
        for(int i = 0; i < 4; ++i)
            bot[0][i] = PLACED_SHIP;
        for(int i = 0; i < 3; ++i)
            bot[2][i] = PLACED_SHIP;
        for(int i = 4; i < 7; ++i)
            bot[2][i] = PLACED_SHIP;
        for(int i = 0; i < 2; ++i)
            bot[4][i] = PLACED_SHIP;
        for(int i = 3; i < 5; ++i)
            bot[4][i] = PLACED_SHIP;
        for(int i = 6; i < 8; ++i)
            bot[4][i] = PLACED_SHIP;
        for(int i = 0; i < 8; i += 2)
            bot[6][i] = PLACED_SHIP;
    }
    
    private final Random r = new Random();
    private int lastPlayerX, lastPlayerY;

    @Override
    protected Pair<Integer, Integer> doMove(int px, int py, Outcome previous) throws FoulPlayException {
        if(previous == Outcome.KILLED) {
            int x = r.nextInt(10), y = r.nextInt(10);
            while(players[x][y] != NOT_CHECKED) {
                x = r.nextInt(10);
                y = r.nextInt(10);
            }
            return new Pair(x, y);
        }else if(previous == Outcome.WOUNDED) {
            if(!isEmpty(checkAndGet(players, px, py + 1)))
                return new Pair(px, py + 1);
            if(!isEmpty(checkAndGet(players, px, py - 1)))
                return new Pair(px, py - 1);
            if(!isEmpty(checkAndGet(players, px + 1, py)))
                return new Pair(px + 1, py);
            if(!isEmpty(checkAndGet(players, px - 1, py)))
                return new Pair(px - 1, py);
            throw new FoulPlayException();
        }
        return new Pair(lastPlayerX, lastPlayerY);
    }

    @Override
    protected void afterPlayerMove(int x, int y, Outcome o) {
        lastPlayerX = x;
        lastPlayerY = y;
    }

}
