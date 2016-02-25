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

    public SeaBattle(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        long uid = m.getSender();
        if(args.length == 0 || args[0].equalsIgnoreCase("помощь")) {
            getMC().sendAttached(m.getDialog(), "Здесь должна быть помощь", m.getMessageId());
            return;
        }
        if(games.containsKey(uid)) {
            AbstractStrategy game = games.get(uid);
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
            AbstractStrategy strategy;
            strategy = new RepeatStrategy();
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
