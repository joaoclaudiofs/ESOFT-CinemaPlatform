package models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Session implements Serializable {
    private static final long serialVersionUID = 1L;

    private Room room;
    private Room.Seat[] occupiedSeats;
    private Date date;
    private Film film;
    private Language language;
    private Language subtitles;

    public Session(Room room, Room.Seat[] occupiedSeats, Date date, Film film, Language language, Language subtitles) {
        this.room = room;
        this.occupiedSeats = occupiedSeats;
        this.date = date;
        this.film = film;
        this.language = language;
        this.subtitles = subtitles;
    }

    public Room getRoom() {
        return room;
    }

    public Room.Seat[] getOccupiedSeats() {
        return occupiedSeats;
    }

    public Date getDate() {
        return date;
    }

    public Film getFilm() {
        return film;
    }

    public Language getLanguage() {
        return language;
    }

    public Language getSubtitles() {
        return subtitles;
    }

    public String getHorario() {
        return String.format("%02d:%02d", date.getHours(), date.getMinutes());
    }

    public String[] getAvailableSeats() {
        Room.Seat[] roomSeats = (room != null && room.getSeats() != null) ? room.getSeats() : new Room.Seat[0];
        Room.Seat[] currentOccupiedSeats = occupiedSeats != null ? occupiedSeats : new Room.Seat[0];
        List<String> availableSeats = new ArrayList<>();

        for (Room.Seat seat : roomSeats) {
            if (seat == null) {
                continue;
            }

            boolean isOccupied = false;
            for (Room.Seat occupiedSeat : currentOccupiedSeats) {
                if (sameSeat(seat, occupiedSeat)) {
                    isOccupied = true;
                    break;
                }
            }
            if (!isOccupied) {
                availableSeats.add(seat.toString());
            }
        }

        return availableSeats.toArray(new String[0]);
    }

    private boolean sameSeat(Room.Seat a, Room.Seat b) {
        return a != null && b != null
                && a.getRow() == b.getRow()
                && a.getColumn() == b.getColumn();
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setSubtitles(Language subtitles) {
        this.subtitles = subtitles;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormatter.format(date) +
                " - " + film.getName() +
                " - " + room.getId();
    }

    public void setAvailableSeats(List<String> lugares) {
        if (lugares == null || lugares.isEmpty()) {
            this.occupiedSeats = new Room.Seat[0];
            return;
        }

        Room.Seat[] newOccupiedSeats = new Room.Seat[lugares.size()];
        for (int i = 0; i < lugares.size(); i++) {
            String lugar = lugares.get(i);
            newOccupiedSeats[i] = new Room.Seat(lugar);
        }
        this.occupiedSeats = newOccupiedSeats;
    }
    public void ocuparLugar(Room.Seat novoLugar) {
        if (novoLugar == null) {
            return;
        }

        if (occupiedSeats == null) {
            occupiedSeats = new Room.Seat[0];
        }

        for (Room.Seat ocupado : occupiedSeats) {
            if (sameSeat(ocupado, novoLugar)) {
                return;
            }
        }

        Room.Seat[] novosOcupados = new Room.Seat[occupiedSeats.length + 1];
        System.arraycopy(occupiedSeats, 0, novosOcupados, 0, occupiedSeats.length);
        novosOcupados[occupiedSeats.length] = novoLugar;
        occupiedSeats = novosOcupados;
    }
}
