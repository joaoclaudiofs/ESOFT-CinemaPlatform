package tests;

import app.statistics.StatisticsMenu;
import models.*;
import models.sale.Receipt;
import models.sale.SaleLine;
import models.sale.sellable.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticsTest {
    AppData appData;

    @BeforeEach
    void setup() {
        // limpar dados
        appData = AppData.getInstance();
        appData.clear();

        // criar dados
        appData.addFilm(new Film("Film 1", 192, "Publisher",
                new HashSet<>(Set.of(Language.EN)), new HashSet<>(Set.of(Language.EN)),
                Film.Genre.ACTION, Film.Rating.U12,
                new HashSet<>(Set.of(Room.ImageSystem.SYSTEM_2D))
        ));
        appData.addFilm(new Film("Film 2", 180, "Publisher",
                new HashSet<>(Set.of(Language.EN)), new HashSet<>(Set.of(Language.EN)),
                Film.Genre.ACTION, Film.Rating.U12,
                new HashSet<>(Set.of(Room.ImageSystem.SYSTEM_2D))
        ));
        appData.addRoom(new Room("Room 1", 10, 10,
                Room.ImageSystem.SYSTEM_2D, Room.SoundSystem.SYSTEM_DTS));

        Room.Seat seat = new Room.Seat(1, 1, true);
        Room.Seat[] seats = { seat };
        appData.addSession(new Session(appData.getRooms().get(0), seats,
                Date.from(Instant.now()), appData.getFilms().get(0),
                Language.EN, Language.EN));
        appData.addSession(new Session(appData.getRooms().get(0), seats,
                Date.from(Instant.now()), appData.getFilms().get(1),
                Language.EN, Language.EN));

        appData.addTicket(new Ticket(appData.getSessions().get(0), seat, Ticket.Type.NORMAL, null));
        appData.addTicket(new Ticket(appData.getSessions().get(0), seat, Ticket.Type.NORMAL, null));
        appData.addTicket(new Ticket(appData.getSessions().get(1), seat, Ticket.Type.NORMAL, null));

        SaleLine saleLine = new SaleLine(appData.getTickets().get(0), 1, 10);
        SaleLine[] sales = new SaleLine[] { saleLine };
        appData.addReceipt(new Receipt(sales, Date.from(Instant.now())));
    }

    @Test
    void testFilmStatistics() {
        List<Ticket> tickets = appData.getTickets();
        List<Session> sessions = new ArrayList<>();

        for (Ticket ticket : tickets) {
            Session session = ticket.getSession();
            sessions.add(session);
        }

        Film result = StatisticsMenu.getMostWatchedFilm(sessions);
        assertEquals(appData.getFilms().get(0), result, "Deve ser o Film 1, index 0 na AppData, visto que este tem 3 bilhetes associados");
    }

    @Test
    void testProfitStatistics() {
        // ultimos 12 meses
        LocalDate now = LocalDate.now();
        YearMonth[] lastYear = new YearMonth[12];
        for (int i = 0; i < 12; i++) {
            lastYear[i] = YearMonth.from(now.minusMonths(11 - i));
        }

        float[] profits = StatisticsMenu.getProfits(appData.getReceipts(), lastYear);

        assertEquals(appData.getReceipts().get(0).getPrice(), profits[11], "O lucro do mês atual deve ser o preço do único recibo no AppData, que é do dia atual");
    }

    @Test
    void testSessionStatistics() {
        List<Session> sessions = appData.getSessions();
        DayOfWeek dayOfWeek = StatisticsMenu.getDayOfWeek(sessions);

        assertEquals(LocalDate.now().getDayOfWeek(), dayOfWeek, "O dia da semana com mais sessões tem de corresponder ao dia da semana de hoje, visto que as duas sessões criadas são do dia atual");
    }
}
