package tests;

import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {

    private AppData appData;
    private Film film;

    @BeforeEach
    void setup() {
        appData = AppData.getInstance();
        appData.getFilms().clear();
        appData.getSessions().clear();

        film = new Film(
                "Filme X",
                100f,
                "Produtora Y",
                Set.of(Language.PT),
                Set.of(Language.EN),
                Film.Genre.DRAMA,
                Film.Rating.U12,
                Set.of(Room.ImageSystem.SYSTEM_2D)
        );
        appData.getFilms().add(film);

        Room room = new Room("Sala A", 5, 5, Room.ImageSystem.SYSTEM_2D, Room.SoundSystem.SYSTEM_DOLBY);


        Date data1 = new Date(1672502400000L);
        Date data2 = new Date(1672588800000L);

        Session sessao1 = new Session(room, new Room.Seat[0], data1, film, Language.PT, Language.EN);
        Session sessao2 = new Session(room, new Room.Seat[0], data2, film, Language.PT, Language.EN);

        appData.getSessions().add(sessao1);
        appData.getSessions().add(sessao2);
    }


    @Test
    void testObterSessoesDoFilme() {
        List<Session> sessoesDoFilme = appData.getSessions().stream()
                .filter(s -> s.getFilm().getName().equals(film.getName()))
                .collect(Collectors.toList());

        assertEquals(2, sessoesDoFilme.size(), "Deve retornar as 2 sessões para o filme");

        assertTrue(sessoesDoFilme.stream().anyMatch(s -> s.getDate().equals(new Date(1672502400000L))));
        assertTrue(sessoesDoFilme.stream().anyMatch(s -> s.getDate().equals(new Date(1672588800000L))));
    }


    @Test
    void testGetHorario() {
        Date date = new Date(1234567890000L);
        Film film = new Film("Test", 1.5f, "Pub", null, null, Film.Genre.DRAMA, Film.Rating.U7, null);
        Room room = new Room("R1", 1, 1, Room.ImageSystem.SYSTEM_2D, Room.SoundSystem.SYSTEM_DOLBY);
        Session session = new Session(room, new Room.Seat[0], date, film, null, null);

        String horario = session.getHorario();
        assertNotNull(horario);
        assertTrue(horario.matches("\\d{2}:\\d{2}"));
    }
}
