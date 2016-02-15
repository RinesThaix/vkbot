package ru.ifmo.vkbot.modules;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.ifmo.vkbot.VkBot;
import ru.ifmo.vkbot.structures.Message;
import ru.ifmo.vkbot.utils.RDate;

/**
 *
 * @author RinesThaix
 */
public class Scheduler extends BotModule {

    public Scheduler(VkBot vkbot) {
        super(vkbot);
    }

    @Override
    public void handle(Message m, String[] args) {
        if(args.length < 2) {
            getMC().sendAttached(m.getDialog(), "Недостаточно аргументов!", m.getMessageId());
            return;
        }
        int iday = 0;
        if(args[iday].equals("для"))
            ++iday;
        if(args[iday].equals("группы"))
            ++iday;
        String group = args[iday].toUpperCase();
        if(args[++iday].equals("на"))
            ++iday;
        String day = args[iday];
        if(day.equals("сегодня"))
            day = new RDate().getDayOfTheWeek();
        else if(day.equals("вчера")) {
            RDate date = new RDate();
            date.addDays(-1);
            day = date.getDayOfTheWeek();
        }else if(day.equals("позавчера")) {
            RDate date = new RDate();
            date.addDays(-2);
            day = date.getDayOfTheWeek();
        }else if(day.equals("завтра")) {
            RDate date = new RDate();
            date.addDay();
            day = date.getDayOfTheWeek();
        }else if(day.equals("послезавтра")) {
            RDate date = new RDate();
            date.addDays(2);
            day = date.getDayOfTheWeek();
        }
        switch(day) {
            case "пн": case "пнд":
            case "понедельник": iday = 1; break;
            case "вт":
            case "вторник": iday = 2; break;
            case "ср":
            case "среду": case "среда": iday = 3; break;
            case "чв": case "чет":
            case "четверг": iday = 4; break;
            case "пт": case "пят":
            case "пятницу": case "пятница": iday = 5; break;
            case "сб": case "суб":
            case "субботу": case "суббота": iday = 6; break;
            case "вск": case "воск": case "вс": case "вскр":
            case "воскресенье": {
                getMC().sendAttached(m.getDialog(), "В воскресенье нет пар, дурачок!", m.getMessageId());
                return;
            }default: {
                getMC().sendAttached(m.getDialog(), "Умный самый? :/", m.getMessageId());
                return;
            }
        }
        String url = "http://www.ifmo.ru/ru/schedule/0/" + group + "/raspisanie_zanyatiy_" + group + ".htm";
        List<String> first = new ArrayList(), second = new ArrayList(), third = new ArrayList();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements all = doc.select("div.container").select("div.page-content").select("div.rasp_tabl_day")
                    .select("table.rasp_tabl");
            int id = 0;
            for(Element table : all) {
                if(++id != iday)
                    continue;
                for(Element pair : table.select("td.time")) {
                    String time = pair.select("span").first().text();
                    String ned = pair.select("dt").first().text();
                    if(ned.startsWith("неч"))
                        ned = "неч.";
                    else if(ned.startsWith("чет"))
                        ned = "чет.";
                    time += " (" + (ned.isEmpty() ? "всегда" : ned) + ") - ";
                    first.add(time);
                }
                for(Element pair : table.select("td.room"))
                    second.add(pair.select("span").first().text().replace("ул.", "").replace("д.", "") + ", " +
                            pair.select("dd").first().text() + " - ");
                for(Element pair : table.select("td.lesson"))
                    third.add(pair.select("dd").first().text() + ".");
            }
            StringBuilder sb = new StringBuilder();
            if(first.isEmpty()) {
                getMC().sendAttached(m.getDialog(), "Расписание для группы " + group + " не найдено :(", m.getMessageId());
                return;
            }
            switch(day) {
                case "пятница":
                    day = "пятницу";
                    break;
                case "суббота":
                    day = "субботу";
                    break;
                default:
                    break;
            }
            sb.append("Расписание для группы ").append(group).append(" на ").append(day).append(":\n");
            for(int i = 0; i < first.size(); ++i)
                sb.append(first.get(i)).append(second.get(i)).append(third.get(i)).append("\n");
            getMC().sendAttached(m.getDialog(), sb.toString(), m.getMessageId());
        }catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

}
