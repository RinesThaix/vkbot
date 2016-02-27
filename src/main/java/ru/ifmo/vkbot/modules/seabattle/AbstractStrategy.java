package ru.ifmo.vkbot.modules.seabattle;

import ru.ifmo.vkbot.utils.Pair;

/**
 *
 * @author RinesThaix
 */
public abstract class AbstractStrategy {

    protected final int[][]
            bot = new int[10][10],
            players = new int[10][10];
    
    protected final static int
            NOT_CHECKED = 0, //неизвестно, что здесь
            SURELY_EMTPY = 1, //мы сюда походили, и здесь пусто
            EMPTY = 2, //мы сюда не ходили, но здесь должно быть пусто, т.к. где-то рядом корабль
            WOUNDED_SHIP = 3, //раненый корабль
            KILLED_SHIP = 4, //убитый корабль
            PLACED_SHIP = 5; //здесь стоит наш корабль
    
    private int
            shipCellsLeft_bot = 20,
            shipCellsLeft_player = 20,
            cellsLeft_player = 100,
            four_player = 1,
            three_player = 2,
            two_player = 3,
            one_player = 4;
    
    private int lastX = -1, lastY = -1; //наш последний ход, ждем ответа игрока
    protected Outcome lastOutcome = Outcome.NOTHING;
    
    public void validate() throws NotValidStrategyException {
        placeShips();
        if(!checkShips())
            throw new NotValidStrategyException(bot);
    }
    
    public final Outcome playerMove(String move) throws NotValidMoveException {
        Pair<Integer, Integer> pa = parseMove(move);
        if(pa == null)
            throw new NotValidMoveException(-1, -1, false);
        int x = pa.getA(), y = pa.getB();
        if(!exists(x, y))
            throw new NotValidMoveException(x, y, false);
        if(bot[x][y] != NOT_CHECKED && bot[x][y] != PLACED_SHIP)
            throw new NotValidMoveException(x, y, true);
        Outcome o = playerMove0(x, y);
        afterPlayerMove(x, y, o);
        return o;
    }
    
    private Outcome playerMove0(int x, int y) throws NotValidMoveException {
        if(bot[x][y] == NOT_CHECKED) {
            bot[x][y] = SURELY_EMTPY;
            return Outcome.NOTHING;
        }
        if(--shipCellsLeft_bot == 0)
            return Outcome.WIN;
        Integer u = checkAndGet(bot, x, y + 1), d = checkAndGet(bot, x, y - 1), l = checkAndGet(bot, x - 1, y),
                r = checkAndGet(bot, x + 1, y);
        if(isNothing(u) && isNothing(d) && isNothing(l) && isNothing(r)) {
            bot[x][y] = KILLED_SHIP;
            return Outcome.KILLED;
        }
        boolean killed = true;
        for(int dx = x - 1; dx >= 0; --dx) {
            Integer v = checkAndGet(bot, dx, y);
            if(isNothing(v))
                break;
            if(v == PLACED_SHIP) {
                killed = false;
                break;
            }
        }
        for(int dx = x + 1; dx < 10; ++dx) {
            Integer v = checkAndGet(bot, dx, y);
            if(isNothing(v))
                break;
            if(v == PLACED_SHIP) {
                killed = false;
                break;
            }
        }
        for(int dy = y - 1; dy >= 0; --dy) {
            Integer v = checkAndGet(bot, x, dy);
            if(isNothing(v))
                break;
            if(v == PLACED_SHIP) {
                killed = false;
                break;
            }
        }
        for(int dy = y + 1; dy < 10; ++dy) {
            Integer v = checkAndGet(bot, x, dy);
            if(isNothing(v))
                break;
            if(v == PLACED_SHIP) {
                killed = false;
                break;
            }
        }
        if(killed) {
            bot[x][y] = KILLED_SHIP;
            //Здесь, по идее, было бы неплохо обновить все WOUNDED_SHIP на убитые, но нам это не особо нужно, так что :)
            return Outcome.KILLED;
        }else {
            bot[x][y] = WOUNDED_SHIP;
            return Outcome.WOUNDED;
        }
    }
    
    public final boolean isPending() {
        return lastOutcome == Outcome.PENDING;
    }

    public final char SYMBOL_SHIP = '╋';
    public final char SYMBOL_WOUNDED_SHIP = '┳';
    public final char SYMBOL_NOT_CHECKED = '━';
    public final char SYMBOL_SURELY_EMPTY = '┃';
    public final char SYMBOL_OTHER = '┻';

    public String getMatrix(boolean bot) {
        int[][] matrix = bot ? this.bot : this.players;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 10; ++i) {
            for(int j = 0; j < 10; ++j)
                sb.append(matrix[j][i] == PLACED_SHIP ? SYMBOL_SHIP :
                        matrix[j][i] == WOUNDED_SHIP ? SYMBOL_WOUNDED_SHIP :
                        matrix[j][i] == NOT_CHECKED ? SYMBOL_NOT_CHECKED :
                        matrix[j][i] == SURELY_EMTPY ? SYMBOL_SURELY_EMPTY : SYMBOL_OTHER)
                        .append(" ");
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public final String botMove() throws NotValidMoveException, FoulPlayException {
        Outcome previous = lastOutcome;
        lastOutcome = Outcome.PENDING;
        Pair<Integer, Integer> move = doMove(lastX, lastY, previous);
        int x = move.getA(), y = move.getB();
        if(!exists(x, y))
            throw new NotValidMoveException(x, y, false);
        if(players[x][y] != NOT_CHECKED && players[x][y] != EMPTY)
            throw new NotValidMoveException(x, y, true);
        lastX = x;
        lastY = y;
        char c = x == 9 ? 'К' : (char) (x + 'А');
        return c + "" + (y + 1);
    }
    
    public final Outcome updateLastMove(String outcome) throws UnknownOutcomeException, FoulPlayException {
        if(--cellsLeft_player < shipCellsLeft_player - 1)
            throw new FoulPlayException();
        switch(outcome.toLowerCase()) {
            case "мимо":
                players[lastX][lastY] = SURELY_EMTPY;
                return lastOutcome = Outcome.NOTHING;
            case "ранила":
                --shipCellsLeft_player;
                players[lastX][lastY] = WOUNDED_SHIP;
                int x1 = lastX, x2 = lastX, y1 = lastY, y2 = lastY;
                for(int dx = x1 + 1;; ++dx) {
                    Integer v = checkAndGet(players, dx, lastY);
                    if(isNothing(v)) {
                        x2 = dx - 1;
                        break;
                    }
                    if(v != WOUNDED_SHIP)
                        throw new FoulPlayException();
                }
                for(int dx = x1 - 1;; --dx) {
                    Integer v = checkAndGet(players, dx, lastY);
                    if(isNothing(v)) {
                        x1 = dx + 1;
                        break;
                    }
                    if(v != WOUNDED_SHIP)
                        throw new FoulPlayException();
                }
                for(int dy = y1 + 1;; ++dy) {
                    Integer v = checkAndGet(players, lastX, dy);
                    if(isNothing(v)) {
                        y2 = dy - 1;
                        break;
                    }
                    if(v != WOUNDED_SHIP)
                        throw new FoulPlayException();
                }
                for(int dy = y1 - 1;; --dy) {
                    Integer v = checkAndGet(players, lastX, dy);
                    if(isNothing(v)) {
                        y1 = dy + 1;
                        break;
                    }
                    if(v != WOUNDED_SHIP)
                        throw new FoulPlayException();
                }
                if(x1 != x2 && y1 != y1 || x2 - x1 + 1 > 4 || y2 - y1 + 1 > 4)
                    throw new FoulPlayException();
                return lastOutcome = Outcome.WOUNDED;
            case "убила":
                if(--shipCellsLeft_player == 0)
                    return lastOutcome = Outcome.WIN;
                int x = lastX, y = lastY;
                x1 = x; x2 = x;
                y1 = y; y2 = y;
                for(int dx = x1 + 1;; ++dx) {
                    Integer v = checkAndGet(players, dx, y);
                    if(isNothing(v)) {
                        x2 = dx - 1;
                        break;
                    }
                    if(v == WOUNDED_SHIP)
                        players[dx][y] = KILLED_SHIP;
                    else
                        throw new FoulPlayException();
                }
                for(int dx = x1 - 1;; --dx) {
                    Integer v = checkAndGet(players, dx, y);
                    if(isNothing(v)) {
                        x1 = dx + 1;
                        break;
                    }
                    if(v == WOUNDED_SHIP)
                        players[dx][y] = KILLED_SHIP;
                    else
                        throw new FoulPlayException();
                }
                for(int dy = y1 + 1;; ++dy) {
                    Integer v = checkAndGet(players, x, dy);
                    if(isNothing(v)) {
                        y2 = dy - 1;
                        break;
                    }
                    if(v == WOUNDED_SHIP)
                        players[x][dy] = KILLED_SHIP;
                    else
                        throw new FoulPlayException();
                }
                for(int dy = y1 - 1;; --dy) {
                    Integer v = checkAndGet(players, x, dy);
                    if(isNothing(v)) {
                        y1 = dy + 1;
                        break;
                    }
                    if(v == WOUNDED_SHIP)
                        players[x][dy] = KILLED_SHIP;
                    else
                        throw new FoulPlayException();
                }
                if(x1 != x2 && y1 != y2)
                    throw new FoulPlayException();
                int size = Math.max(x2 - x1, y2 - y1) + 1;
                switch(size) {
                    case 4: --four_player; break;
                    case 3: --three_player; break;
                    case 2: --two_player; break;
                    case 1: --one_player; break;
                    default: throw new FoulPlayException();
                }
                if(four_player < 0 || three_player < 0 || two_player < 0 || one_player < 0)
                    throw new FoulPlayException();
                for(x = x1 - 1; x <= x2 + 1; ++x)
                    for(y = y1 - 1; y <= y2 + 1; ++y) {
                        if(x >= 0 && x < 10 && y >= 0 && y < 10 && players[x][y] == NOT_CHECKED)
                            players[x][y] = EMPTY;
                    }
                players[lastX][lastY] = KILLED_SHIP;
                return lastOutcome = Outcome.KILLED;
            default:
                throw new UnknownOutcomeException();
        }
    }
    
    //Нужно расставить корабли в матрице bot[][]
    protected abstract void placeShips();
    
    //Нужно вернуть пару (x; y) - куда мы желаем походить. Аргументы: координаты предыдущего хода и результат.
    protected abstract Pair<Integer, Integer> doMove(int px, int py, Outcome previous) throws FoulPlayException;
    
    //Функция, вызываемая после того, как игрок походил в точку (x; y), и результатом этого хода стало o.
    protected abstract void afterPlayerMove(int x, int y, Outcome o);
    
    private Pair<Integer, Integer> parseMove(String move) {
        try {
            move = move.toLowerCase();
            char f = move.charAt(0);
            if(f >= 'a' && f <= 'j')
                f -= 'a';
            else if(f >= 'а' && f <= 'к') {
                if(f == 'к')
                    f = 'й';
                f -= 'а';
            }else
                return null;
            if(move.length() == 3) {
                String sub = move.substring(1, 3);
                if(sub.equals("10"))
                    return new Pair((int) f, 9);
            }else {
                char s = move.charAt(1);
                if(s >= '1' && s <= '9')
                    return new Pair((int) f, (int) (s - '1'));
            }
        }catch(Exception ex) {}
        return null;
    }
    
    private int[][] checked = new int[10][10];
    private int four = 1, three = 2, two = 3, one = 4;
    
    private boolean checkShips() {
        for(int i = 0; i < 10; ++i)
            for(int j = 0; j < 10; ++j)
                checked[i][j] = bot[i][j];
        int empty = 0, placed = 0;
        for(int i = 0; i < 10; ++i)
            for(int j = 0; j < 10; ++j) {
                int v = checked[i][j];
                if(v == NOT_CHECKED)
                    ++empty;
                else if(v == PLACED_SHIP)
                    ++placed;
            }
        int neededPlaced = 20;
        if(placed != neededPlaced || empty != 100 - neededPlaced)
            return false;
        for(int i = 0; i < 10; ++i)
            for(int j = 0; j < 10; ++j)
                checkCell(i, j);
        return four == 0 && three == 0 && two == 0 && one == 0;
    }
    
    protected boolean exists(int x, int y) {
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }
    
    protected Integer checkAndGet(int[][] matrix, int x, int y) {
        if(!exists(x, y))
            return null;
        return matrix[x][y];
    }
    
    protected boolean isNothing(Integer value) {
        return value == null || value <= EMPTY;
    }
    
    protected boolean isEmpty(Integer value) {
        return value == null || value == SURELY_EMTPY || value == EMPTY;
    }
    
    protected boolean isWounded(Integer value) {
        return value != null && value == WOUNDED_SHIP;
    }
    
    protected boolean isNotChecked(Integer value) {
        return value != null && value == NOT_CHECKED;
    }
    
    private Integer checkAndGet(int x, int y) {
        return checkAndGet(checked, x, y);
    }
    
    public boolean checkFreeSpot(int[][] matrix, int x1, int y1, int x2, int y2) {
        for(int x = x1 - 1; x <= x2 + 1; ++x)
            for(int y = y1 - 1; y <= y2 + 1; ++y) {
                if(x >= x1 && x <= x2 && y >= y1 && y <= y2)
                    continue;
                Integer current = checkAndGet(matrix, x, y);
                if(!isNothing(current))
                    return false;
            }
        return true;
    }
    
    private void decreaseShipsAmount(int size) {
        switch(size) {
            case 4: --four; break;
            case 3: --three; break;
            case 2: --two; break;
            case 1: --one; break;
            default: one = -1; break;
        }
    }
    
    private void checkCell(int x, int y) {
        if(!exists(x, y))
            return;
        int v = checked[x][y];
        if(v == PLACED_SHIP) {
            checked[x][y] = KILLED_SHIP;
            Integer u = checkAndGet(x, y + 1), d = checkAndGet(x, y - 1), l = checkAndGet(x - 1, y), r = checkAndGet(x + 1, y);
            if(isNothing(u) && isNothing(d) && isNothing(l) && isNothing(r)) {
                --one;
                return;
            }
            if(!isNothing(u)) {
                int endY = y + 1;
                for(;; ++endY) {
                    Integer value = checkAndGet(x, endY);
                    if(isNothing(value)) {
                        --endY;
                        break;
                    }
                    checked[x][endY] = KILLED_SHIP;
                }
                if(!checkFreeSpot(checked, x, y, x, endY))
                    one = -1;
                decreaseShipsAmount(endY - y + 1);
            }
            if(!isNothing(r)) {
                int endX = x + 1;
                for(;; ++endX) {
                    Integer value = checkAndGet(endX, y);
                    if(isNothing(value)) {
                        --endX;
                        break;
                    }
                    checked[endX][y] = KILLED_SHIP;
                }
                if(!checkFreeSpot(checked, x, y, endX, y))
                    one = -1;
                decreaseShipsAmount(endX - x + 1);
            }
        }
    }
    
    public static enum Outcome {
        NOTHING, //мимо
        PENDING, //ожидаем ответа игрока
        WOUNDED, //ранен
        KILLED, //убит
        WIN; //победа
    }
    
}
