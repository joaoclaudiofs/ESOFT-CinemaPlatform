package tests;

import models.AppData;
import models.Film;
import models.Room;
import models.Session;
import models.sale.Receipt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppDataTest {
    private AppData appData;

    @BeforeEach
    void setUp() {
        appData = AppData.getInstance();
        appData.clear();

        appData.addFilm(new Film("Test Film 1", 120f, "Publisher A", null, null, Film.Genre.ACTION, Film.Rating.U12, null));
        appData.addFilm(new Film("Test Film 2", 90f, "Publisher B", null, null, Film.Genre.COMEDY, Film.Rating.U12, null));

        appData.addRoom(new Room("Test Room 1", 10, 10, Room.ImageSystem.SYSTEM_2D, Room.SoundSystem.SYSTEM_DOLBY));

        appData.addSession(
            new Session(
                appData.getRooms().get(0),
                new Room.Seat[0],
                new Date(),
                appData.getFilms().get(0),
                models.Language.EN,
                models.Language.NONE
            )
        );
    }

    @Test
    void testGetFilms() {
        List<Film> films = appData.getFilms();
        assertNotNull(films);
        assertFalse(films.isEmpty());
    }

    @Test
    void testGetRooms() {
        List<Room> rooms = appData.getRooms();
        assertNotNull(rooms);
        assertFalse(rooms.isEmpty());
    }

    @Test
    void testAddSession() {
        int initialSize = appData.getSessions().size();
        Room room = appData.getRooms().get(0);
        Film film = appData.getFilms().get(0);
        Session session = new Session(room, new Room.Seat[0], new Date(), film, models.Language.EN, models.Language.NONE);
        appData.addSession(session);

        assertEquals(initialSize + 1, appData.getSessions().size());
    }

    @Test
    void testSaveAndLoadData() {
        AppData.saveData();
        AppData loadedData = AppData.getInstance();
        assertNotNull(loadedData);
        assertEquals(appData.getFilms().size(), loadedData.getFilms().size());
    }
}