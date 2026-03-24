package models.sale.sellable;

import models.AppData;
import models.Room;
import models.Session;

import java.io.Serializable;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;

    private Session session;
    private Room.Seat seat;
    private float price;
    private Type type;
    private State state;
    private Combo combo;

    public Ticket(Session session, Room.Seat seat, Type type, Combo combo) {
        this.session = session;
        this.seat = seat;
        this.price = AppData.getInstance().getTicketPrice(type);
        this.type = type;
        this.state = State.ACTIVE;
        this.combo = combo;
    }

    public float getPrice() {
        return price;
    }

    public Combo getCombo() {
        return combo;
    }

    public Room.Seat getSeat() {
        return seat;
    }

    public Type getType() {
        return type;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Session getSession() {
        return session;
    }

    public enum Type {
        JUNIOR(0, 18, "Júnior"),
        NORMAL(18, 65, "Normal"),
        SENIOR(65, Integer.MAX_VALUE, "Sénior");

        private int ageMin;
        private int ageMax;
        private String name;

        Type(int ageMin, int ageMax, String name) {
            this.ageMin = ageMin;
            this.ageMax = ageMax;
            this.name = name;
        }

        public static Type fromName(String ticketName) {
            for (Type type : Type.values()) {
                if (type.getName().equalsIgnoreCase(ticketName)) {
                    return type;
                }
            }
            return null;
        }

        public int getAgeMin() {
            return this.ageMin;
        }

        public int getAgeMax() {
            return this.ageMax;
        }

        public String getName() {
            return this.name;
        }

        public boolean isInAgeGap(int age) {
            return age >= this.ageMin && age < this.ageMax;
        }

        @Override
        public String toString() {
            return this.name;
        }

    }

    public enum State {
        ACTIVE("Ativo"),
        CANCELED("Cancelado"),
        REFUNDED("Refunded");

        private String name;

        State(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public String toString() {
        double totalPrice = price;
        StringBuilder sb = new StringBuilder();

        sb.append("Bilhete ")
                .append(type.getName().toLowerCase())
                .append(" - Sessão: ").append(session.getFilm().getName())
                .append(" - Lugar: ").append(numToLetter(seat.getRow())).append(seat.getColumn());

        if (combo != null) {
            sb.append(" - Combo: ").append(combo.toString());
            totalPrice += combo.getPrice();
        }

        sb.append(" => ").append("+").append(String.format("%.2f", totalPrice)).append("€");

        return sb.toString();
    }

    private String numToLetter(int num) {
        String letter = "";
        while (num > 0) {
            num--;
            letter = (char) ('A' + (num % 26)) + letter;
            num /= 26;
        }
        return letter;
    }


}
