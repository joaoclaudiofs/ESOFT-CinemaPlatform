package app.management;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import app.generics.ObjectPreview;
import models.AppData;
import models.Film;
import models.Room;
import models.Session;
import models.sale.sellable.Combo;
import models.sale.sellable.Product;
import models.sale.sellable.Ticket;

import app.Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagementMenu extends JFrame {
    private JPanel panelHeader;
    private JLabel labelWindowTitle;
    private JButton buttonBack;
    private JPanel panelFooter;
    private JButton buttonView;
    private JButton buttonUpdate;
    private JButton buttonDelete;
    private JButton buttonCreate;
    private JTabbedPane tabbedPanel;
    private JPanel panelRoot;
    private JScrollPane scrollSessions;
    private JList listSessions;
    private JPanel panelSessions;
    private JLabel labelSessions;
    private JScrollPane scrollProducts;
    private JPanel panelProducts;
    private JLabel labelProducts;
    private JList listProducts;
    private JScrollPane scrollCombos;
    private JPanel panelCombos;
    private JList listCombos;
    private JLabel labelCombos;
    private JScrollPane scrollFilms;
    private JPanel panelFilms;
    private JLabel labelFilms;
    private JList listFilms;
    private JScrollPane scrollRooms;
    private JPanel panelRooms;
    private JLabel labelRooms;
    private JList listRooms;
    private JScrollPane scrollSettings;
    private JPanel panelSettings;
    private JTextField tfJunior;
    private JTextField tfNormal;
    private JTextField tfSenior;
    private JTextField tfRefundTime;

    private Tab tab = Tab.SESSIONS;
    private ObjectPreview lastOpenPreview;

    public ManagementMenu() {
        super("Menu de Gestão");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(800, 600);
        this.setMinimumSize(new Dimension(400, 300));
        this.setLocationRelativeTo(null);

        this.setVisible(true);
        this.setContentPane(panelRoot);

        buttonBack.setIcon(new FlatSVGIcon("icons/arrows/chevron-left.svg", 24, 24));
        buttonBack.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        buttonBack.setVisible(true);
        buttonBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menu menu = new Menu();
                menu.setVisible(true);
                ManagementMenu.this.dispose();
            }
        });

        tabbedPanel.putClientProperty("JTabbedPane.scrollButtonsPolicy", "asNeeded");
        tabbedPanel.putClientProperty("JScrollBar.showButtons", true);
        tabbedPanel.addChangeListener(e -> {
            switch (tabbedPanel.getSelectedIndex()) {
                case 0: tab = Tab.SESSIONS;
                    loadSessions();
                    break;
                case 1: tab = Tab.PRODUCTS;
                    loadProducts();
                    break;
                case 2: tab = Tab.COMBOS;
                    loadCombos();
                    break;
                case 3: tab = Tab.FILMS;
                    loadFilms();
                    break;
                case 4: tab = Tab.ROOMS;
                    loadRooms();
                    break;
                case 5: tab = Tab.SETTINGS;
                    loadTickets();
                    break;
                default: tab = Tab.SESSIONS; // Default to sessions if no other tabs are added
            }
        });

        buttonView.addActionListener(this::onView);
        buttonUpdate.addActionListener(this::onUpdate);
        buttonDelete.addActionListener(this::onDelete);
        buttonCreate.addActionListener(this::onCreate);

        loadSessions();

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (lastOpenPreview != null && lastOpenPreview.isDisplayable()) {
                    lastOpenPreview.dispose();
                }
            }
        });
    }

    private void onView(ActionEvent ev) {
        Object previewObject = switch (tab) {
            case SESSIONS -> listSessions.getSelectedValue();
            case PRODUCTS -> listProducts.getSelectedValue();
            case COMBOS -> listCombos.getSelectedValue();
            case FILMS -> listFilms.getSelectedValue();
            case ROOMS -> listRooms.getSelectedValue();
            default -> null;
        };

        if (previewObject == null) {
            JOptionPane.showMessageDialog(this, tab.getItem() + " não selecionado(a).", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.openPreview(ObjectPreview.PreviewType.VIEW, previewObject);
    }

    private void onUpdate(ActionEvent ev) {
        Object previewObject = switch (tab) {
            case SESSIONS -> listSessions.getSelectedValue();
            case PRODUCTS -> listProducts.getSelectedValue();
            case COMBOS -> null;
            case FILMS -> listFilms.getSelectedValue();
            case ROOMS -> null;
            case SETTINGS -> null;
            default -> null;
        };

        if (tab == Tab.SETTINGS) {
            AppData appData = AppData.getInstance();

            HashMap<Ticket.Type, JTextField> ticketsMap = new HashMap<>() {
                {
                    put(Ticket.Type.JUNIOR, tfJunior);
                    put(Ticket.Type.NORMAL, tfNormal);
                    put(Ticket.Type.SENIOR, tfSenior);
                }
            };

            for (JTextField textField : ticketsMap.values()) {
                String input = textField.getText();

                try {
                    float value = Float.parseFloat(input);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "O preço tem de ser um número valido");
                    return;
                }
            }

            String input = tfRefundTime.getText();
            try {
                float value = Float.parseFloat(input);
                appData.setRefundLimitHours(value);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "O tempo para reembolso têm que ser um número valido");
                return;
            }

            for (Map.Entry<Ticket.Type, JTextField> entry : ticketsMap.entrySet()) {
                appData.setTicketPrice(entry.getKey(), Float.parseFloat(entry.getValue().getText()));
            }

            JOptionPane.showMessageDialog(null, "Definições atualizados com sucesso");

            return;
        }

        if (previewObject == null) {
            JOptionPane.showMessageDialog(this, tab.getItem() + " não selecionado(a).", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.openPreview(ObjectPreview.PreviewType.EDIT, previewObject);
    }

    private void onDelete(ActionEvent ev) {
        Object previewObject = switch (tab) {
            case SESSIONS -> listSessions.getSelectedValue();
            case PRODUCTS -> listProducts.getSelectedValue();
            case COMBOS -> listCombos.getSelectedValue();
            case FILMS -> listFilms.getSelectedValue();
            case ROOMS -> listRooms.getSelectedValue();
            default -> null;
        };

        if (previewObject == null) {
            JOptionPane.showMessageDialog(this, tab.getItem() + " não selecionado(a).", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }


        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o(a) " + tab.getItem().toLowerCase() + "?", "Eliminar " + tab.getItem().toLowerCase(), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        switch (tab) {
            case SESSIONS -> {
                List<Ticket> associatedTickets = AppData.getInstance().getTicketsBySession((Session) previewObject);
                if (!associatedTickets.isEmpty()) {
                    confirm = JOptionPane.showConfirmDialog(this, "Esta sessão tem bilhetes associados. Deseja cancelar os bilhetes?", "Cancelar Bilhetes", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
                    if (confirm == JOptionPane.OK_OPTION) {
                        for (Ticket ticket : associatedTickets) {
                            if (ticket.getState() == Ticket.State.ACTIVE) {
                                ticket.setState(Ticket.State.CANCELED);
                            }
                        }
                        AppData.getInstance().removeSession((Session) previewObject);
                        JOptionPane.showMessageDialog(this, "Sessão e bilhetes associados excluídos com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        return;
                    }
                } else {
                    AppData.getInstance().removeSession((Session) previewObject);
                    JOptionPane.showMessageDialog(this, "Sessão excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                }

                loadSessions();
            }
            case PRODUCTS -> {
                List<Combo> associatedCombos = new ArrayList<>();
                for (Combo combo : AppData.getInstance().getCombos()) {
                    if (combo.getProduct() == previewObject) {
                        associatedCombos.add(combo);
                    }
                }
                if (!associatedCombos.isEmpty()) {
                    if (JOptionPane.showConfirmDialog(this, "Este produto tem combos associados. Deseja eliminar os combos?", "Eliminar Combos", JOptionPane.ERROR_MESSAGE, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                        for (Combo combo : associatedCombos) {
                            AppData.getInstance().removeCombo(combo);
                        }
                        AppData.getInstance().removeProduct((Product) previewObject);
                        JOptionPane.showMessageDialog(this, "Produto e combos associados excluídos com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        return;
                    }
                } else {
                    AppData.getInstance().removeProduct((Product) previewObject);
                    JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                }

                loadProducts();
            }
            case COMBOS -> {
                AppData.getInstance().removeCombo((Combo) previewObject);
                JOptionPane.showMessageDialog(this, "Combo excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadCombos();
            }
            case FILMS -> {
                List<Session> associatedSessions = new ArrayList<>();
                for (Session session : AppData.getInstance().getSessions()) {
                    if (session.getFilm() == previewObject) {
                        associatedSessions.add(session);
                    }
                }
                if (!associatedSessions.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Este filme tem sessões associadas. Não é possível remover filme", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                AppData.getInstance().removeFilm((Film) previewObject);
                JOptionPane.showMessageDialog(this, "Filme excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadFilms();
            }
            case ROOMS -> {
                List<Session> associatedSessions = new ArrayList<>();
                for (Session session : AppData.getInstance().getSessions()) {
                    if (session.getRoom() == previewObject) {
                        associatedSessions.add(session);
                    }
                }
                if (!associatedSessions.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Esta sala tem sessões associadas. Não é possível remover sala", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                AppData.getInstance().removeRoom((Room) previewObject);
                JOptionPane.showMessageDialog(this, "Sala excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadRooms();
            }
        }
    }

    private void onCreate(ActionEvent ev) {
        if (lastOpenPreview != null && lastOpenPreview.isDisplayable()) {
            lastOpenPreview.dispose();
        }

        lastOpenPreview = switch (tab) {
            case SESSIONS -> new SessionPreview(this, ObjectPreview.PreviewType.CREATE, null);
            case PRODUCTS -> new ProductPreview(this, ObjectPreview.PreviewType.CREATE, null);
            case COMBOS -> new ComboPreview(this, ObjectPreview.PreviewType.CREATE, null);
            case FILMS -> new FilmPreview(this, ObjectPreview.PreviewType.CREATE, null);
            case ROOMS -> new RoomPreview(this, ObjectPreview.PreviewType.CREATE, null);
            default -> null;
        };
    }

    private void openPreview(ObjectPreview.PreviewType previewType, Object selectedObject) {
        if (selectedObject == null) {
            JOptionPane.showMessageDialog(this, "Item inválido para visualização.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (lastOpenPreview != null) {
            if (lastOpenPreview.getPreviewType() == previewType &&
                    lastOpenPreview.isDisplayable() &&
                    selectedObject == lastOpenPreview.getObject()
            ) {
                lastOpenPreview.setVisible(true);
                lastOpenPreview.toFront();
                lastOpenPreview.requestFocus();
                lastOpenPreview.repaint();
                return;
            }
            lastOpenPreview.dispose();
        }

        lastOpenPreview = switch (selectedObject.getClass().getName()) {
            case "models.Session" -> new SessionPreview(this, previewType, (Session) selectedObject);
            case "models.sale.sellable.Product" -> new ProductPreview(this, previewType, (Product) selectedObject);
            case "models.sale.sellable.Combo" -> new ComboPreview(this, previewType, (Combo) selectedObject);
            case "models.Film" -> new FilmPreview(this, previewType, (Film) selectedObject);
            case "models.Room" -> new RoomPreview(this, previewType, (Room) selectedObject);
            default -> null;
        };

        if (lastOpenPreview == null) {
            JOptionPane.showMessageDialog(this, "Visualização não suportada para este objeto.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    public void loadSessions() {
        List<Session> sessions = AppData.getInstance().getSessions();

        scrollSessions.setBorder(null);
        listSessions.setBackground(null);

        if (sessions == null || sessions.size() == 0) {
            labelSessions.setText("Nenhuma sessão encontrada.");
            labelSessions.setVisible(true);
            listSessions.setVisible(false);
            buttonView.setEnabled(false);
            buttonUpdate.setEnabled(false);
            buttonDelete.setEnabled(false);
            buttonCreate.setEnabled(false);
            return;
        }
        labelSessions.setVisible(false);
        listSessions.setVisible(true);

        DefaultListModel<Session> model = new DefaultListModel<>();
        for (Session session : sessions) {
            model.addElement(session);
        }
        listSessions.setModel(model);

        buttonView.setEnabled(true);
        buttonUpdate.setEnabled(true);
        buttonDelete.setEnabled(true);
        buttonCreate.setEnabled(true);
    }

    public void loadProducts() {
        List<Product> products = AppData.getInstance().getProducts();

        scrollProducts.setBorder(null);
        listProducts.setBackground(null);

        if (products == null || products.size() == 0) {
            labelProducts.setText("Nenhum produto encontrado.");
            labelProducts.setVisible(true);
            listProducts.setVisible(false);
            buttonView.setEnabled(false);
            buttonUpdate.setEnabled(false);
            buttonDelete.setEnabled(false);
            buttonCreate.setEnabled(false);
            return;
        }
        labelProducts.setVisible(false);
        listProducts.setVisible(true);

        DefaultListModel<Product> model = new DefaultListModel<>();
        for (Product product : products) {
            model.addElement(product);
        }
        listProducts.setModel(model);

        buttonView.setEnabled(true);
        buttonUpdate.setEnabled(true);
        buttonDelete.setEnabled(true);
        buttonCreate.setEnabled(true);
    }

    public void loadCombos() {
        List<Combo> combos = AppData.getInstance().getCombos();

        scrollCombos.setBorder(null);
        listCombos.setBackground(null);

        if (combos == null || combos.size() == 0) {
            labelCombos.setText("Nenhum combo encontrado.");
            labelCombos.setVisible(true);
            listCombos.setVisible(false);
            buttonView.setEnabled(false);
            buttonUpdate.setEnabled(false);
            buttonDelete.setEnabled(false);
            buttonCreate.setEnabled(false);
            return;
        }
        labelCombos.setVisible(false);
        listCombos.setVisible(true);

        DefaultListModel<Combo> model = new DefaultListModel<>();
        for (Combo combo : combos) {
            model.addElement(combo);
        }
        listCombos.setModel(model);

        buttonView.setEnabled(true);
        buttonUpdate.setEnabled(false);
        buttonDelete.setEnabled(true);
        buttonCreate.setEnabled(true);
    }

    public void loadFilms() {
        List<Film> films = AppData.getInstance().getFilms();

        scrollFilms.setBorder(null);
        listFilms.setBackground(null);

        if (films == null || films.size() == 0) {
            labelFilms.setText("Nenhum filme encontrado.");
            labelFilms.setVisible(true);
            listFilms.setVisible(false);
            buttonView.setEnabled(false);
            buttonUpdate.setEnabled(false);
            buttonDelete.setEnabled(false);
            buttonCreate.setEnabled(false);
            return;
        }
        labelFilms.setVisible(false);
        listFilms.setVisible(true);

        DefaultListModel<Film> model = new DefaultListModel<>();
        for (Film film : films) {
            model.addElement(film);
        }
        listFilms.setModel(model);

        buttonView.setEnabled(true);
        buttonUpdate.setEnabled(true);
        buttonDelete.setEnabled(true);
        buttonCreate.setEnabled(true);
    }

    public void loadRooms() {
        List<Room> rooms = AppData.getInstance().getRooms();

        scrollRooms.setBorder(null);
        listRooms.setBackground(null);

        if (rooms == null || rooms.size() == 0) {
            labelRooms.setText("Nenhuma sala encontrada.");
            labelRooms.setVisible(true);
            listRooms.setVisible(false);
            buttonView.setEnabled(false);
            buttonUpdate.setEnabled(false);
            buttonDelete.setEnabled(false);
            buttonCreate.setEnabled(false);
            return;
        }
        labelRooms.setVisible(false);
        listRooms.setVisible(true);

        DefaultListModel<Room> model = new DefaultListModel<>();
        for (Room room : rooms) {
            model.addElement(room);
        }
        listRooms.setModel(model);

        buttonView.setEnabled(true);
        buttonUpdate.setEnabled(false);
        buttonDelete.setEnabled(true);
        buttonCreate.setEnabled(true);
    }

    public void loadTickets() {
        AppData appData = AppData.getInstance();
        tfJunior.setText(String.valueOf(appData.getTicketPrice(Ticket.Type.JUNIOR)));
        tfNormal.setText(String.valueOf(appData.getTicketPrice(Ticket.Type.NORMAL)));
        tfSenior.setText(String.valueOf(appData.getTicketPrice(Ticket.Type.SENIOR)));
        tfRefundTime.setText(String.valueOf(appData.getRefundLimitHours()));

        buttonView.setEnabled(false);
        buttonUpdate.setEnabled(true);
        buttonDelete.setEnabled(false);
        buttonCreate.setEnabled(false);
    }

    public static void main(String[] args) {
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(new java.io.OutputStream() {
            @Override
            public void write(int b) {
                // Discard output
            }
        }));

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            // Optionally log or handle the exception
        } finally {
            // Restore original System.err
            System.setErr(originalErr);
        }

        SwingUtilities.invokeLater(() -> {
            ManagementMenu menu = new ManagementMenu();
            menu.setVisible(true);
        });
    }

    private enum Tab {
        SESSIONS("Sessões", "Sessão"),
        PRODUCTS("Produtos", "Produto"),
        COMBOS("Combos", "Combo"),
        FILMS("Filmes", "Filme"),
        ROOMS("Salas", "Sala"),
        SETTINGS("Definições", "Definição");

        private final String group;
        private final String item;

        Tab(String group, String item) {
            this.group = group;
            this.item = item;
        }

        @Override
        public String toString() {
            return group;
        }

        public String getItem() {
            return item;
        }

        public String getGroup() {
            return group;
        }
    }
}
