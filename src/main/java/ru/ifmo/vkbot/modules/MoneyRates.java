package ru.ifmo.vkbot.modules;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;

/**
 *
 * @author RinesThaix
 */
public class MoneyRates extends BotModule {

    public MoneyRates(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        String symbol;
        switch(args[0].toLowerCase()) {
            case "доллара":
            case "usd":
                symbol = "USD";
                break;
            case "евро":
            case "euro":
            case "eur":
                symbol = "EUR";
                break;
            default:
                symbol = args[0].toUpperCase();
                break;
        }
        try {
            String lsymbol = symbol.toLowerCase();
            String url = "http://www.banki.ru/products/currency/" + lsymbol + "/";
            Document doc = Jsoup.connect(url).get();
            String rate = doc.select("div.currency-table").select("div.currency-table__rate__num").first().text();
            getMC().sendAttached(m.getDialog(), "Курс " + symbol + " на данный момент: " + rate + " руб.", m.getMessageId());
        }catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

}
