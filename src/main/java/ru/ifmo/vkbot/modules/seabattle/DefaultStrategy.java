package ru.ifmo.vkbot.modules.seabattle;

import java.util.Random;
import ru.ifmo.vkbot.utils.Pair;

/**
 *
 * @author RinesThaix
 */
public class DefaultStrategy extends AbstractStrategy {
    
    protected final Random rand = new Random();

    @Override
    protected void placeShips() {
        placeShip(4);
        placeShip(3); placeShip(3);
        placeShip(2); placeShip(2); placeShip(2);
        for(int i = 0; i < 4; ++i)
            placeShip(1);
    }
    
    private void placeShip(int size) {
        boolean vert = rand.nextBoolean();
        if(vert) {
            boolean couldPlace;
            int x, y;
            do {
                couldPlace = true;
                x = rand.nextInt(10);
                y = rand.nextInt(11 - size);
                for(int dy = y; dy < y + size; ++dy)
                    if(bot[x][dy] != NOT_CHECKED) {
                        couldPlace = false;
                        break;
                    }
                couldPlace &= checkFreeSpot(bot, x, y, x, y + size - 1);
            }while(!couldPlace);
            for(int dy = y; dy < y + size; ++dy)
                bot[x][dy] = PLACED_SHIP;
        }else {
            boolean couldPlace;
            int x, y;
            do {
                couldPlace = true;
                x = rand.nextInt(11 - size);
                y = rand.nextInt(10);
                for(int dx = x; dx < x + size; ++dx)
                    if(bot[dx][y] != NOT_CHECKED) {
                        couldPlace = false;
                        break;
                    }
                couldPlace &= checkFreeSpot(bot, x, y, x + size - 1, y);
            }while(!couldPlace);
            for(int dx = x; dx < x + size; ++dx)
                bot[dx][y] = PLACED_SHIP;
        }
    }
    
    protected Pair<Integer, Integer> randomMove() throws FoulPlayException {
        int x = rand.nextInt(10), y = rand.nextInt(10), i = 0;
        while(players[x][y] != NOT_CHECKED && ++i < 100000) {
            x = rand.nextInt(10);
            y = rand.nextInt(10);
        }
        if(i == 100000)
            throw new FoulPlayException();
        return new Pair(x, y);
    }
    
    private Pair<Integer, Integer> wounded = null;

    @Override
    protected Pair<Integer, Integer> doMove(int px, int py, Outcome previous) throws FoulPlayException {
        if(previous == Outcome.WOUNDED)
            wounded = new Pair(px, py);
        else if(previous == Outcome.KILLED)
            wounded = null;
        if(wounded != null) {
            px = wounded.getA();
            py = wounded.getB();
            Integer u = checkAndGet(players, px, py + 1), d = checkAndGet(players, px, py - 1),
                    l = checkAndGet(players, px - 1, py), r = checkAndGet(players, px + 1, py);
            if(isWounded(u)) {
                if(!isEmpty(d))
                    return new Pair(px, py - 1);
                for(int y = py + 2; y < 10; ++y)
                    if(players[px][y] == NOT_CHECKED)
                        return new Pair(px, y);
                throw new FoulPlayException();
            }
            if(isWounded(d)) {
                if(!isEmpty(u))
                    return new Pair(px, py + 1);
                for(int y = py - 2; y >= 0; --y)
                    if(players[px][y] == NOT_CHECKED)
                        return new Pair(px, y);
                throw new FoulPlayException();
            }
            if(isWounded(r)) {
                if(!isEmpty(l))
                    return new Pair(px - 1, py);
                for(int x = px + 2; x < 10; ++x)
                    if(players[x][py] == NOT_CHECKED)
                        return new Pair(x, py);
                throw new FoulPlayException();
            }
            if(isWounded(l)) {
                if(!isEmpty(r))
                    return new Pair(px + 1, py);
                for(int x = px - 2; x >= 0; --x)
                    if(players[x][py] == NOT_CHECKED)
                        return new Pair(x, py);
                throw new FoulPlayException();
            }
            int x, y, i = 0;
            do {
                switch(rand.nextInt(4)) {
                    case 0: x = px - 1; y = py; break;
                    case 1: x = px + 1; y = py; break;
                    case 2: x = px; y = py - 1; break;
                    default: x = px; y = py + 1; break;
                }
                if(++i == 1000)
                    throw new FoulPlayException();
            }while(x < 0 || x >= 10 || y < 0 || y >= 10 || players[x][y] != NOT_CHECKED);
            return new Pair(x, y);
        }else
            return randomMove();
    }

    @Override
    protected void afterPlayerMove(int x, int y, Outcome o) {
        //Nothing
    }

}
