package app.statistics;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import models.AppData;
import models.Film;
import models.Session;
import models.sale.Receipt;
import models.sale.SaleLine;
import models.sale.sellable.Combo;
import models.sale.sellable.Product;
import models.sale.sellable.Ticket;
import app.Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.List;

public class StatisticsMenu extends JFrame{
    private JTabbedPane tpMain;
    private JPanel panel1;
    private JLabel lbFilmMonth;
    private JLabel lbFilmYear;
    private JLabel lbSessionWeekDay;
    private JList listProfits;
    private JList listReceipts;
    private JButton btnView;
    private JButton buttonBack;
    private JButton btnRefund;
    private JButton btnPrint;

    AppData appData = AppData.getInstance();

    public StatisticsMenu() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(800, 600);
        this.setMinimumSize(new Dimension(400, 300));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setContentPane(panel1);

        buttonBack.setIcon(new FlatSVGIcon("icons/arrows/chevron-left.svg", 24, 24));
        buttonBack.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        buttonBack.setVisible(true);
        buttonBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menu menu = new Menu();
                menu.setVisible(true);
                StatisticsMenu.this.dispose();
            }
        });

        displayFilmStatistics();

        tpMain.addChangeListener(e -> {
            switch (tpMain.getSelectedIndex()) {
                case 0:
                    displayFilmStatistics();
                    break;
                case 1:
                    displaySessionStatistics();
                    break;
                case 2:
                    displayProfitStatistics();
                    break;
                case 3:
                    displayRecipts();
                    break;
            }
        });

        btnView.addActionListener(e -> {
            int selectedIndex = listReceipts.getSelectedIndex();

            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(null, "Selecione uma fatura para visualizar");
                return;
            }

            Receipt receipt = appData.getReceipts().get(selectedIndex);
            ReceiptPreview fatura = new ReceiptPreview(receipt);
            fatura.setVisible(true);
        });

        btnRefund.addActionListener(e -> {
            int selectedIndex = listReceipts.getSelectedIndex();

            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(null, "Selecione uma fatura para reembolsar");
                return;
            }

            Receipt receipt = appData.getReceipts().get(selectedIndex);
            refundReceipt(receipt, (long) appData.getRefundLimitHours(), true);
        });

        btnPrint.addActionListener(e -> {
            int selectedIndex = listReceipts.getSelectedIndex();

            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(null, "Selecione uma fatura para imprimir");
                return;
            }

            Receipt receipt = appData.getReceipts().get(selectedIndex);
            printReceipt(receipt);
        });
    }

    void displayFilmStatistics() {
        List<Ticket> tickets = appData.getTickets();
        LocalDate now = LocalDate.now();

        List<Session> sessionsLastMonth = new ArrayList<>();
        List<Session> sessionsLastYear = new ArrayList<>();

        // obter todos os filmes de bilhetes no ultimo mês e ano
        for (Ticket ticket : tickets) {
            Session session = ticket.getSession();
            // converter Date para LocalDate
            LocalDate sessionDate = session.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (sessionDate.isAfter(now.minusMonths(1))) {
                sessionsLastMonth.add(session);
            }
            if (sessionDate.isAfter(now.minusYears(1))) {
                sessionsLastYear.add(session);
            }
        }

        Film monthFilm = getMostWatchedFilm(sessionsLastMonth);
        lbFilmMonth.setText(monthFilm != null ? monthFilm.getName() : "Sem dados");

        Film yearFilm = getMostWatchedFilm(sessionsLastYear);
        lbFilmYear.setText(yearFilm != null ? yearFilm.getName() : "Sem dados");
    }

    void displaySessionStatistics() {
        List<Session> sessions = appData.getSessions();
        DayOfWeek dayOfWeek = getDayOfWeek(sessions);

        lbSessionWeekDay.setText(dayOfWeek != null ? dayOfWeek.toString() : "Sem dados");
    }

    void displayProfitStatistics() {
        DefaultListModel<String> model = new DefaultListModel<>();
        listProfits.setModel(model);

        // ultimos 12 meses
        LocalDate now = LocalDate.now();
        YearMonth[] lastYear = new YearMonth[12];
        for (int i = 0; i < 12; i++) {
            lastYear[i] = YearMonth.from(now.minusMonths(11 - i));
        }

        float[] profits = getProfits(appData.getReceipts(), lastYear);

        for (int i = 0; i < 12; i++) {
            model.addElement(lastYear[i].getMonth() + " " + lastYear[i].getYear() + ": " + profits[i]);
        }
    }

    void displayRecipts() {
        List<Receipt> receipts = appData.getReceipts();

        DefaultListModel<String> model = new DefaultListModel<>();
        listReceipts.setModel(model);

        for (Receipt receipt : receipts) {
            model.addElement(receipt.getDate().toString());
        }
    }

    public static Film getMostWatchedFilm(List<Session> sessions) {
        // mapear filmes vezes que aparece
        Map<Film, Integer> count = new HashMap<>();

        for (Session session : sessions) {
            Film film = session.getFilm();
            count.put(film, count.getOrDefault(film, 0) + 1);
        }

        Film mostWatchedFilm = null;
        int timesWatched = 0;

        for (Map.Entry<Film, Integer> entry : count.entrySet()) {
            if (entry.getValue() > timesWatched) {
                mostWatchedFilm = entry.getKey();
                timesWatched = entry.getValue();
            }
        }

        return mostWatchedFilm;
    }

    public static float[] getProfits(List<Receipt> receipts, YearMonth[] lastYear) {
        float[] profits = new float[12];

        for (Receipt receipt : receipts) {
            // converter Date -> LocalDate e obter o mês + ano
            LocalDate receiptDate = receipt.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            YearMonth month = YearMonth.from(receiptDate);

            for (int i = 0; i < 12; i++) {
                if (month.equals(lastYear[i])) {
                    profits[i] += receipt.getPrice();
                }
            }
        }

        return profits;
    }

    public static DayOfWeek getDayOfWeek(List<Session> sessions) {
        Map<DayOfWeek, Integer> count = new HashMap<>();

        for (Session session : sessions) {
            DayOfWeek weekDay = session.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek();
            count.put(weekDay, count.getOrDefault(weekDay, 0) + 1);
        }

        DayOfWeek dayOfWeek = null;
        int counter = 0;

        for (Map.Entry<DayOfWeek, Integer> entry : count.entrySet()) {
            if (entry.getValue() > counter) {
                dayOfWeek = entry.getKey();
                counter = entry.getValue();
            }
        }

        return dayOfWeek;
    }

    public static void refundReceipt(Receipt receipt, Long limit, boolean reply) {
        boolean alreadyRefunded = false;
        int refundedCount = 0;

        for (SaleLine sale : receipt.getSales()) {
            Object item = sale.getProduct();

            if (item instanceof Ticket) {
                Ticket ticket = (Ticket) item;

                LocalDateTime sessionDate = ticket.getSession().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime now = LocalDateTime.now();

                if (sessionDate.minusHours(limit).isBefore(now)) {
                    break;
                }

                if (ticket.getState() == Ticket.State.REFUNDED) {
                    alreadyRefunded = true;
                    break;
                }

                ticket.setState(Ticket.State.REFUNDED);
                refundedCount++;
            }
        }

        if (reply) {
            if (refundedCount > 0) {
                JOptionPane.showMessageDialog(null, "Foram reembolsados " + refundedCount + " bilhete(s)");
            } else if (alreadyRefunded) {
                JOptionPane.showMessageDialog(null, "Todos os bilhetes desta fatura já foram reembolsados");
            } else {
                JOptionPane.showMessageDialog(null, "Esta fatura não tem bilhetes legiveis para reembolso");
            }
        }
    }

    void printReceipt(Receipt receipt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

        try (PrintWriter out = new PrintWriter(sdf.format(receipt.getDate()) + ".txt")) {
            out.println("Data: " + receipt.getDate());
            out.println("Total: " + receipt.getPrice() + "€");
            out.println("Itens: ");
            for (SaleLine sale : receipt.getSales()) {
                String str = "- ";
                boolean skip = false;

                Object item = sale.getProduct();
                if (item instanceof Product) {
                    str += ((Product) item).getName() + " x" + sale.getQuantity();
                } else if (item instanceof Combo) {
                    str += "Combo";
                } else if (item instanceof Ticket) {
                    str += "Bilhete " + ((Ticket) item).getType().getName();
                    if (((Ticket) item).getState() == Ticket.State.REFUNDED) {
                        str += " (Reembolsado)";
                    }
                } else {
                    str += item;
                    skip = true;
                }

                if (!skip) {
                    str += " " + sale.getPrice() + "€";
                }

                out.println(str);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Não foi possivil imprimir a fatura");
            System.out.println("Failed to print receipt");
            ex.printStackTrace();
        }
    }
}
