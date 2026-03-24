package tests;

import app.statistics.ReceiptPreview;
import app.statistics.StatisticsMenu;
import models.*;
import models.sale.Receipt;
import models.sale.SaleLine;
import models.sale.sellable.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RefundTest {
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

        appData.addRoom(new Room("Room 1", 10, 10,
                Room.ImageSystem.SYSTEM_2D, Room.SoundSystem.SYSTEM_DTS));

        Room.Seat seat = new Room.Seat(1, 1, true);
        Room.Seat[] seats = { seat };
        appData.addSession(new Session(appData.getRooms().get(0), seats,
                Date.from(Instant.now().plusSeconds(10800)), appData.getFilms().get(0),
                Language.EN, Language.EN));
        appData.addSession(new Session(appData.getRooms().get(0), seats,
                Date.from(Instant.now().plusSeconds(3600)), appData.getFilms().get(0),
                Language.EN, Language.EN));

        appData.addTicket(new Ticket(appData.getSessions().get(0), seat, Ticket.Type.NORMAL, null));
        appData.addTicket(new Ticket(appData.getSessions().get(1), seat, Ticket.Type.NORMAL, null));

        SaleLine saleLine1 = new SaleLine(appData.getTickets().get(0), 1, 10);
        SaleLine saleLine2 = new SaleLine(appData.getTickets().get(1), 1, 10);
        SaleLine[] sales = new SaleLine[] { saleLine1, saleLine2 };
        appData.addReceipt(new Receipt(sales, Date.from(Instant.now())));

        appData.setRefundLimitHours(2);
    }

    @Test
    void testRefundTicket() {
        Ticket refundableTicket = appData.getTickets().get(0);
        Ticket unrefundableTicket = appData.getTickets().get(1);

        ReceiptPreview.refundTicket(refundableTicket, false);
        ReceiptPreview.refundTicket(unrefundableTicket, false);

        assertEquals(Ticket.State.REFUNDED, refundableTicket.getState(), "O primeiro bilhete deve estar reembolsado visto que o limite de reembolso são duas horas e a sessão deste bilhete acontece daqui a 3");
        assertNotEquals(Ticket.State.REFUNDED, unrefundableTicket.getState(), "O primeiro bilhete não deve estar reembolsado visto que o limite de reembolso são duas horas e a sessão deste bilhete acontece daqui a 1");
    }

    @Test
    void testRefundReceipt() {
        Ticket refundableTicket = appData.getTickets().get(0);
        Ticket unrefundableTicket = appData.getTickets().get(1);

        StatisticsMenu.refundReceipt(appData.getReceipts().get(0), (long) appData.getRefundLimitHours(), false);

        assertEquals(Ticket.State.REFUNDED, refundableTicket.getState(), "O primeiro bilhete da fatura pode ser reembolsado");
        assertNotEquals(Ticket.State.REFUNDED, unrefundableTicket.getState(), "O segundo bilhete da fatura não pode ser reembolsado");
    }
}
