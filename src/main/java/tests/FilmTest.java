package tests;

import models.Film;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FilmTest {

    @Test
    void testRatingAgeCheck() {
        assertFalse(Film.Rating.U12.isAgeInRating(11));
        assertTrue(Film.Rating.U12.isAgeInRating(12));
        assertTrue(Film.Rating.U12.isAgeInRating(15));
        assertFalse(Film.Rating.U18.isAgeInRating(17));
        assertTrue(Film.Rating.U18.isAgeInRating(18));
    }

    @Test
    void testFilmName() {
        Film film = new Film("Matrix", 2.30f, "Warner", null, null, Film.Genre.ACTION, Film.Rating.U16, null);
        assertEquals("Matrix", film.getName());
    }
}
