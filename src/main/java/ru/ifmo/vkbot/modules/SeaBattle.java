package ru.ifmo.vkbot.modules;

import java.util.HashMap;
import java.util.Map;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.modules.seabattle.*;
import ru.ifmo.vkbot.modules.seabattle.AbstractStrategy.Outcome;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.utils.Logger;

/**
 *
 * @author RinesThaix
 */
public class SeaBattle extends BotModule {
    
    private final static Map<Long, AbstractStrategy> games = new HashMap();
    private final static Map<String, Class<? extends AbstractStrategy>> strategies = new HashMap();
    
    static {
        strategies.put("обычная", DefaultStrategy.class);
        strategies.put("новичок", NoviceStrategy.class);
    }

    public SeaBattle(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        long uid = m.getSender();
        if(args.length == 0 || args[0].equalsIgnoreCase("помощь")) {
            StringBuilder sb = new StringBuilder();
            sb.append("Помощь по морскому бою:\n");
            sb.append("- мб стратегии - выводит все доступные стратегии.\n");
            sb.append("- мб [название стратегии] - начать игру против выбранной стратегии.\n");
            sb.append("- мб сдаюсь - досрочно завершить игру.\n");
            sb.append("- мб Г8 - походить в клетку Г8.\n");
            sb.append("- мб ранила - сказать Милаше, что она своим последним ходом ранила ваш корабль.\n");
            sb.append("- мб поле - попросить Милашу вывести пустое поле.\n");
            if(getVkBot().isAdministrator(uid)) {
                sb.append("- [A] мб принт - попросить Милашу вывести свое поле и информацию о вашем поле.\n");
            }
            sb.append("Легенда карты: Ⓧ - убит, ⓧ - ранен, ⓞ - пусто, ⑔ - неизвестно.\n");
            getMC().sendAttached(m.getDialog(), sb.toString(), m.getMessageId());
            return;
        }
        switch(args[0].toLowerCase()) {
            case "стратегии": {
                StringBuilder sb = new StringBuilder();
                sb.append("Стратегии морского боя:\n");
                for(String s : strategies.keySet())
                    sb.append("- ").append(s).append("\n");
                getMC().sendAttached(m.getDialog(), sb.toString(), m.getMessageId());
                return;
            }case "поле": {
                getMC().sendAttached(m.getDialog(), "Пожалуйста:\n" + AbstractStrategy.getMatrix(new int[10][10]), m.getMessageId());
                return;
            }
        }
        if(games.containsKey(uid)) {
            AbstractStrategy game = games.get(uid);
            switch(args[0].toLowerCase()) {
                case "сдаюсь": {
                    games.remove(uid);
                    getMC().sendAttached(m.getDialog(), "Это было просто!", m.getMessageId());
                    return;
                }case "принт": {
                    if(!getVkBot().isAdministrator(uid)) {
                        getMC().sendAttached(m.getDialog(), "Может, еще чего?", m.getMessageId());
                        return;
                    }
                    String s1 = game.getBotViewMatrix(true), s2 = game.getBotViewMatrix(false);
                    getMC().sendAttached(m.getDialog(), "Пожалуйста:\n" + s1 + "\n" + s2, m.getMessageId());
                    return;
                }
            }
            if(game.isPending())
                try {
                    Outcome o = game.updateLastMove(args[0]);
                    if(o == Outcome.WIN) {
                        getMC().sendAttached(m.getDialog(), "Ха! Я победила! Победила, победила, победила!\nНу что, еще разок?", m.getMessageId());
                        games.remove(uid);
                    }else if(o == Outcome.WOUNDED || o == Outcome.KILLED) {
                        getMC().sendAttached(m.getDialog(), "Мой ход: " + game.botMove(), m.getMessageId());
                    }
                }catch(SeaBattleException ex) {
                    if(ex instanceof NotValidMoveException) {
                        getMC().sendAttached(m.getDialog(), "Стратегия сделала невозможный ход!", m.getMessageId());
                        String result = ((NotValidMoveException) ex).isAlreadyMovedHere() ?
                                "duplicating one of previous moves" : "impossible move";
                        Logger.warn("Not valid move by SeaBattle strategy " + game.getClass().getName() + ": " + result + "!", ex);
                    }else if(ex instanceof UnknownOutcomeException) {
                        getMC().sendAttached(m.getDialog(), "Прости, но я не поняла: мимо, ранила или убила?", m.getMessageId());
                    }else {
                        getMC().sendAttached(m.getDialog(), "Ты нечестно играешь! У тебя корабли расставлены неправильно! Я завершаю эту игру!", m.getMessageId());
                        games.remove(uid);
                    }
                }
            else
                try {
                    Outcome o = game.playerMove(args[0]);
                    if(o == Outcome.WIN) {
                        getMC().sendAttached(m.getDialog(), "Эх, ладно, в этот раз твоя взяла. Ты победил. Поздравляю.", m.getMessageId());
                        games.remove(uid);
                    }else if(o == Outcome.WOUNDED || o == Outcome.KILLED) {
                        getMC().sendAttached(m.getDialog(), (o == Outcome.WOUNDED ? "Ранил" : "Убил") + "!\nХоди.", m.getMessageId());
                    }else {
                        try {
                            getMC().sendAttached(m.getDialog(), "Мимо!\nМой ход: " + game.botMove(), m.getMessageId());
                        }catch(SeaBattleException ex) {
                            if(ex instanceof NotValidMoveException) {
                                getMC().sendAttached(m.getDialog(), "Стратегия сделала невозможный ход!", m.getMessageId());
                                String result = ((NotValidMoveException) ex).isAlreadyMovedHere() ?
                                        "duplicating one of previous moves" : "impossible move";
                                Logger.warn("Not valid move by SeaBattle strategy " + game.getClass().getName() + ": " + result + "!", ex);
                            }else {
                                getMC().sendAttached(m.getDialog(), "Ты нечестно играешь! У тебя корабли расставлены неправильно! Я завершаю эту игру!", m.getMessageId());
                                games.remove(uid);
                            }
                        }
                    }
                }catch(NotValidMoveException ex) {
                    if(ex.isAlreadyMovedHere())
                        getMC().sendAttached(m.getDialog(), "Ты уже ходил в эту клетку.", m.getMessageId());
                    else
                        getMC().sendAttached(m.getDialog(), "Либо ты вышел за пределы поля, либо написал какую-то дичь.", m.getMessageId());
                }
        }else {
            Class<? extends AbstractStrategy> clazz = strategies.get(args[0].toLowerCase());
            if(clazz == null) {
                getMC().sendAttached(m.getDialog(), "Неизвестная стратегия!", m.getMessageId());
                return;
            }
            AbstractStrategy strategy;
            try {
                strategy = clazz.newInstance();
            }catch(InstantiationException | IllegalAccessException ex) {
                getMC().sendAttached(m.getDialog(), "Я не смогла загрузить данную стратегию :(", m.getMessageId());
                Logger.warn("Could not create SeaBattle strategy \"" + clazz.getName() + "\" instance!", ex);
                return;
            }
            try {
                strategy.validate();
                games.put(uid, strategy);
                getMC().sendAttached(m.getDialog(), "Флот Милаши прибыл, капитан. Начинаем игру, и первый ход за тобой!", m.getMessageId());
            }catch(NotValidStrategyException ex) {
                getMC().sendAttached(m.getDialog(), "Выбранная стратегия невалидна. Игра против нее невозможна.", m.getMessageId());
                Logger.warn("Invalid SeaBattle strategy " + strategy.getClass().getName() + "!", ex);
            }
        }
    }

}
