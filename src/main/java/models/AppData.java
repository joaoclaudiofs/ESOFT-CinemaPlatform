package models;

import models.sale.SaleLine;
import models.sale.sellable.Combo;
import models.sale.sellable.Product;
import models.sale.Receipt;
import models.sale.sellable.Ticket;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class AppData implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String FILENAME = "app.data";
    private static AppData instance = null;

    private List<Product> products = new ArrayList<>();
    private List<Session> sessions = new ArrayList<>();
    private List<Film> films = new ArrayList<>();
    private List<Combo> combos = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();
    private List<Receipt> receipts = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();

    private float refundLimitHours = 0f;

    private HashMap<Ticket.Type, Float> ticketPrices = new HashMap<>() {
        {
            put(Ticket.Type.JUNIOR, 0f);
            put(Ticket.Type.NORMAL, 0f);
            put(Ticket.Type.SENIOR, 0f);
        }
    };

    private AppData() {

        addFilm(new Film("Avatar: The Way of Water", 192, "20th Century Studios",
                new HashSet<>(Set.of(Language.EN, Language.PT)), new HashSet<>(Set.of(Language.NONE, Language.EN, Language.PT)),
                Film.Genre.ACTION, Film.Rating.U12,
                new HashSet<>(Set.of(Room.ImageSystem.SYSTEM_2D, Room.ImageSystem.SYSTEM_3D))
        ));
        addFilm(new Film("Oppenheimer", 180, "Universal Pictures",
                new HashSet<>(Set.of(Language.EN, Language.PT)), new HashSet<>(Set.of(Language.NONE, Language.EN, Language.PT)),
                Film.Genre.DRAMA, Film.Rating.U12, new HashSet<>(Set.of(Room.ImageSystem.SYSTEM_2D))
        ));
        addFilm(new Film("Barbie", 114, "Warner Bros. Pictures",
                new HashSet<>(Set.of(Language.EN, Language.PT)), new HashSet<>(Set.of(Language.NONE, Language.EN, Language.PT)),
                Film.Genre.ACTION, Film.Rating.U12,
                new HashSet<>(Set.of(Room.ImageSystem.SYSTEM_2D))
        ));

        addRoom(new Room("Sala 1", 10, 10, Room.ImageSystem.SYSTEM_2D, Room.SoundSystem.SYSTEM_DTS));
        addRoom(new Room("Sala 2", 14, 16, Room.ImageSystem.SYSTEM_3D, Room.SoundSystem.SYSTEM_DOLBY));
        addRoom(new Room("Sala 3", 14, 16, Room.ImageSystem.SYSTEM_2D, Room.SoundSystem.SYSTEM_DOLBY));

        Room.Seat seat1 = new Room.Seat(1, 1, true);
        Room.Seat seat2 = new Room.Seat(1, 2, false);
        Room.Seat seat3 = new Room.Seat(1, 3, false);
        Room.Seat[] seats1 = {
                seat1, seat2
        };
        Room.Seat[] seats2 = {
                seat3
        };

        addSession(new Session(rooms.get(0), seats1, Date.from(Instant.now().plusSeconds((18000))), films.get(0), Language.EN, Language.PT));
        addSession(new Session(rooms.get(0), seats2, Date.from(Instant.now().plusSeconds((3600))), films.get(1), Language.EN, Language.PT));

        addTicket(new Ticket(sessions.get(0), seat1, Ticket.Type.NORMAL, new Combo(Ticket.Type.NORMAL, getProducts().get(0), 2.5f)));
        addTicket(new Ticket(sessions.get(0), seat2, Ticket.Type.JUNIOR, null));
        addTicket(new Ticket(sessions.get(1), seat3, Ticket.Type.JUNIOR, null));

        addProduct(new Product("Popcorn", 5.0f, 30));
        addProduct(new Product("Soda", 3.0f, 50));
        addProduct(new Product("Candy", 2.0f));

        addCombo(new Combo(Ticket.Type.JUNIOR, getProducts().get(0), 3.0f));
        addCombo(new Combo(Ticket.Type.NORMAL, getProducts().get(1), 2.5f));
        addCombo(new Combo(Ticket.Type.SENIOR, getProducts().get(2), 1.5f));

        SaleLine saleLine1 = new SaleLine(products.get(0), 2, products.get(0).getPrice() * 2);
        SaleLine saleLine2 = new SaleLine(tickets.get(0), 1, 10);
        SaleLine saleLine3 = new SaleLine(tickets.get(1), 1, 7.5);
        SaleLine saleLine4 = new SaleLine(tickets.get(2), 1, 7.5);
        SaleLine[] sales1 = new SaleLine[] { saleLine1, saleLine2, saleLine3 };
        SaleLine[] sales2 = new SaleLine[] { saleLine4 };
        SaleLine[] sales3 = new SaleLine[] { saleLine1 };
        addReceipt(new Receipt(sales1, Date.from(Instant.now().minusSeconds(3600))));
        addReceipt(new Receipt(sales2, Date.from(Instant.now().minusSeconds(18000))));
        addReceipt(new Receipt(sales3, Date.from(Instant.now().minusSeconds(18000))));

        setRefundLimitHours(2.5f);
        setTicketPrice(Ticket.Type.JUNIOR, 7.5f);
        setTicketPrice(Ticket.Type.NORMAL, 10f);
        setTicketPrice(Ticket.Type.SENIOR, 8.5f);

        SimpleDateFormat dateParser = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            addSession(new Session(getRooms().get(0), new Room.Seat[0],
                    dateParser.parse("01/01/2025 18:00"),
                    getFilms().get(0), Language.EN, Language.NONE));
            addSession(new Session(getRooms().get(1), new Room.Seat[0],
                    dateParser.parse("01/01/2025 20:00"),
                    getFilms().get(1), Language.PT, Language.EN));
            addSession(new Session(getRooms().get(0), new Room.Seat[0],
                    dateParser.parse("01/01/2025 22:00"),
                    getFilms().get(2), Language.EN, Language.PT));
        } catch (Exception e) {}

    }

    public static AppData getInstance() {
        if (instance == null) {
            instance = loadData();
        }

        if (instance == null) {
            System.out.println("Failed to load AppData instance");
        }
        return instance;
    }

    private static AppData loadData() {
        AppData loadedData = null;

        if (!Files.exists(Paths.get(FILENAME))) {
            System.out.println("app.data not found");
            return new AppData();
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(Paths.get(FILENAME)))) {
            loadedData = (AppData) inputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Failed to load data");
            ex.printStackTrace();
        }
        return loadedData != null ? loadedData : new AppData();
    }

    public static void saveData() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(FILENAME)))) {
            outputStream.writeObject(instance);
        } catch (IOException ex) {
            System.out.println("Failed to save data");
            ex.printStackTrace();
        }
    }

    public void clear() {
        products.clear();
        sessions.clear();
        films.clear();
        combos.clear();
        rooms.clear();
        receipts.clear();
        tickets.clear();

        refundLimitHours = 0f;
        ticketPrices = new HashMap<>() {
            {
                put(Ticket.Type.JUNIOR, 0f);
                put(Ticket.Type.NORMAL, 0f);
                put(Ticket.Type.SENIOR, 0f);
            }
        };
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public List<Session> getSessions() {
        return new ArrayList<>(sessions);
    }

    public List<Film> getFilms() {
        return new ArrayList<>(films);
    }

    public List<Combo> getCombos() {
        return new ArrayList<>(combos);
    }

    public List<Receipt> getReceipts() {
        return new ArrayList<>(receipts);
    }

    public List<Room> getRooms() {
        return new ArrayList<>(rooms);
    }

    public List<Ticket> getTickets() {
        return new ArrayList<>(tickets);
    }

    public float getTicketPrice(Ticket.Type type) {
        return ticketPrices.get(type);
    }

    public float getRefundLimitHours() {
        return refundLimitHours;
    }

    public List<Ticket> getTicketsBySession(Session session) {
        List<Ticket> ticketsBySession = new ArrayList<>();
        for (Ticket ticket : tickets) {
            // TODO ticket active check
            if (ticket.getSession().equals(session)) {
                ticketsBySession.add(ticket);
            }
        }
        return ticketsBySession;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void addSession(Session session) {
        sessions.add(session);
    }

    public void addFilm(Film film) {
        films.add(film);
    }

    public void addCombo(Combo combo) {
        combos.add(combo);
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public void addReceipt(Receipt receipt) {
        receipts.add(receipt);
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public void setRefundLimitHours(float limit) {
        this.refundLimitHours = limit;
    }

    public void setTicketPrice(Ticket.Type type, float price) {
        ticketPrices.put(type, price);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public void removeFilm(Film film) {
        films.remove(film);
    }

    public void removeCombo(Combo combo) {
        combos.remove(combo);
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
    }

    public void removeReceipt(Receipt receipt) {
        receipts.remove(receipt);
    }

    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
    }

//    public void removerLugarOcupado(String filme, String sessao, String lugar) {
//        for (Session session : sessions) {
//            if (session.getFilm().getName().equals(filme) && session.getDate().toString().equals(sessao)) {
//                Room room = session.getRoom();
//                for (Room.Seat seat : room.getSeats()) {
//                    if (seat.getName().equals(lugar)) {
//                        seat.setOccupied(false);
//                        break;
//                    }
//                }
//                break;
//            }
//        }
//    }
}
