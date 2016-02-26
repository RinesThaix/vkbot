package ru.ifmo.vkbot.modules.seabattle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import ru.ifmo.vkbot.utils.Pair;

/**
 *
 * @author RinesThaix
 */
public class NoviceStrategy extends DefaultStrategy {
    
    private final List<Pair> preCells = Arrays.asList(new Pair[]{
        new Pair(0, 4), new Pair(1, 5), new Pair(2, 6), new Pair(0, 5), new Pair(1, 4), new Pair(2, 3),
        new Pair(3, 2), new Pair(4, 1), new Pair(5, 0), new Pair(6, 2), new Pair(5, 1), new Pair(4, 0),
        new Pair(7, 3), new Pair(8, 4), new Pair(9, 5), new Pair(7, 6), new Pair(8, 5), new Pair(9, 4),
        new Pair(3, 7), new Pair(4, 8), new Pair(5, 9), new Pair(6, 7), new Pair(5, 8), new Pair(4, 9),
        new Pair(0, 2), new Pair(0, 7), new Pair(2, 0), new Pair(7, 0),
        new Pair(9, 2), new Pair(9, 7), new Pair(2, 9), new Pair(7, 9)
    });
    
    private final List<Pair> cells = new ArrayList();

    @Override
    protected Pair<Integer, Integer> randomMove() {
        if(cells.isEmpty()) {
            cells.addAll(preCells);
            for(int i = 0; i < 10; ++i) {
                cells.add(new Pair(i, i));
                cells.add(new Pair(i, 9 - i));
            }
            Collections.shuffle(cells);
        }
        while(!cells.isEmpty()) {
            Pair p = cells.remove(0);
            if(players[(int) p.getA()][(int) p.getB()] == NOT_CHECKED)
                return p;
        }
        return super.randomMove();
    }
    
}
