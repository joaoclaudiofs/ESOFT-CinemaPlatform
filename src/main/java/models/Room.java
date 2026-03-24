package models;

import java.io.Serializable;
import java.util.Set;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private int numRows;
    private int numColumns;
    private Seat[][] seats;
    private ImageSystem imageSystem;
    private SoundSystem soundSystem;

    public Room(String id, int numRows, int numColumns, ImageSystem imageSystem, SoundSystem soundSystem) {
        this.id = id;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.seats = new Seat[this.numRows][this.numColumns];
        this.imageSystem = imageSystem;
        this.soundSystem = soundSystem;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                seats[i][j] = new Seat(i + 1, j + 1, false);
            }
        }

    }

    public String getId() {
        return id;
    }

    public Seat[] getSeats() {
        Seat[] flatSeats = new Seat[numRows * numColumns];
        int index = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                flatSeats[index++] = seats[i][j];
            }
        }
        return flatSeats;
    }

    public ImageSystem getImageSystem() {
        return imageSystem;
    }

    public Seat getSeat(String a1) {
        for (Seat[] row : seats) {
            for (Seat seat : row) {
                if (seat.getName().equals(a1)) {
                    return seat;
                }
            }
        }
        return null;
    }

    public SoundSystem getSoundSystem() {
        return soundSystem;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int getNumRows() {
        return numRows;
    }

    public enum ImageSystem {
        SYSTEM_2D(0, "2D"),
        SYSTEM_3D(1, "3D");

        private int index;
        private String name;

        ImageSystem(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum SoundSystem {
        SYSTEM_DOLBY(0, "Dolby Atmos"),
        SYSTEM_DTS(1, "DTS:X");

        private int index;
        private String name;

        SoundSystem(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return this.index;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static class Seat implements Serializable {
        private static final long serialVersionUID = 1L;

        private int row;
        private int column;
        private boolean accessible;

        public Seat(int row, int column, boolean accessible) {
            this.row = row;
            this.column = column;
            this.accessible = accessible;
        }

        public Seat(String lugar) {
            String[] parts = lugar.split(" - ");
            this.row = Integer.parseInt(parts[0].replace("Fila ", ""));
            this.column = Integer.parseInt(parts[1].replace("Lugar ", ""));
            this.accessible = false;
        }

        public boolean isAccessible() {
            return accessible;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public void setAccessible(boolean accessible) {
            this.accessible = accessible;
        }

        @Override
        public String toString() {
            return String.format("Fila %d - Lugar %d", row, column);
        }

        public void setOccupied(boolean b) {
            this.accessible = b;
        }

        public Object getName() {
            return String.format("Fila %d - Lugar %d", row, column);
        }
    }

    @Override
    public String toString() {
        return id + " - " + numRows + "x" + numColumns + " - " + imageSystem + " - " + soundSystem.getName();
    }
}
