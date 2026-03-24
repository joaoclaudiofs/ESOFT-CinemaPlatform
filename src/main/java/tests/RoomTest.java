package tests;

import models.Room;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {

    @Test
    void testRoomSeatsCreation() {
        Room room = new Room("R1", 2, 3, Room.ImageSystem.SYSTEM_2D, Room.SoundSystem.SYSTEM_DOLBY);

        Room.Seat[][] seats = new Room.Seat[2][3];
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 3; c++) {
                seats[r][c] = new Room.Seat(r, c, false);
            }
        }
        assertEquals(6, room.getSeats().length);
    }
}
