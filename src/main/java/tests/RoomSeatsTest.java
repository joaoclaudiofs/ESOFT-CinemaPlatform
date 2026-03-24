package tests;

import app.management.RoomPreview;
import models.AppData;
import models.Film;
import models.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoomSeatsTest {
    @Test
    void testRoomSeats() {
        Room room = new Room("Sala Teste", 10, 20, Room.ImageSystem.SYSTEM_2D, Room.SoundSystem.SYSTEM_DTS);
        RoomPreview preview = new RoomPreview(null, RoomPreview.PreviewType.VIEW, room);

        assertNotNull(preview);
        assertEquals("Sala Teste", room.getId());
        assertEquals(10, room.getNumRows());
        assertEquals(20, room.getNumColumns());
    }

    @Test
    void testRoomSeatAccessibility() {
        Room room = new Room("Sala Teste", 5, 5, Room.ImageSystem.SYSTEM_2D, Room.SoundSystem.SYSTEM_DTS);
        for (Room.Seat seat : room.getSeats()) {
            seat.setAccessible(true);
        }

        for (Room.Seat seat : room.getSeats()) {
            assertTrue(seat.isAccessible());
        }
    }
}