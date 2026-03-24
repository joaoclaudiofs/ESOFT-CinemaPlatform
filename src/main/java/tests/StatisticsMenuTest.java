package tests;

import app.statistics.StatisticsMenu;
import models.AppData;
import models.Film;
import models.Room;
import models.Session;
import models.sale.Receipt;
import models.sale.SaleLine;
import models.sale.sellable.Combo;
import models.sale.sellable.Product;
import models.sale.sellable.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsMenuTest {
    private AppData appData;

    @BeforeEach
    void setUp() {
        appData = AppData.getInstance();
        appData.clear();
        appData.addFilm(new Film("Test Film 1", 120, "Publisher A", null, null, Film.Genre.ACTION, Film.Rating.U12, null));
        appData.addFilm(new Film("Test Film 2", 90, "Publisher B", null, null, Film.Genre.COMEDY, Film.Rating.U12, null));

        appData.addProduct(new Product("Test Product 1", 5.0f, 30));
        appData.addProduct(new Product("Test Product 2", 3.0f, 20));

        appData.addRoom(new Room("Test Room 1", 10, 10, Room.ImageSystem.SYSTEM_2D, Room.SoundSystem.SYSTEM_DOLBY));

        appData.addSession(
            new Session(
                appData.getRooms().get(0),
                new models.Room.Seat[]{new models.Room.Seat(1, 1, true)},
                new Date(System.currentTimeMillis() + 3600000),
                appData.getFilms().get(0),
                models.Language.EN,
                models.Language.NONE
            )
        );

        appData.addTicket(
            new Ticket(
                appData.getSessions().get(0),
                appData.getRooms().get(0).getSeats()[0],
                Ticket.Type.NORMAL,
                null
            )
        );

        appData.addReceipt(
            new models.sale.Receipt(
                new models.sale.SaleLine[]{
                    new SaleLine(appData.getProducts().get(0), 1, 5),
                    new SaleLine(appData.getProducts().get(1), 2, 3)
                },
                new Date(System.currentTimeMillis() + 100000)
            )
        );
    }


    @Test
    void testGetMostWatchedFilm() {
        AppData appData = AppData.getInstance();
        List<Session> sessions = appData.getSessions();
        Film mostWatchedFilm = StatisticsMenu.getMostWatchedFilm(sessions);

        assertNotNull(mostWatchedFilm);
    }

    @Test
    void testGetProfits() {
        AppData appData = AppData.getInstance();
        List<Receipt> receipts = appData.getReceipts();
        YearMonth[] lastYear = new java.time.YearMonth[12];
        LocalDate now = java.time.LocalDate.now();
        for (int i = 0; i < 12; i++) {
            lastYear[i] = YearMonth.from(now.minusMonths(11 - i));
        }

        float[] profits = StatisticsMenu.getProfits(receipts, lastYear);
        assertNotNull(profits);
        assertEquals(12, profits.length);
    }
}