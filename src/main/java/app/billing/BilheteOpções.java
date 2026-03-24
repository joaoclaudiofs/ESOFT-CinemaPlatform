package app.billing;

import models.*;
import models.sale.sellable.Combo;
import models.sale.sellable.Ticket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class BilheteOpções extends JFrame {
    public JComboBox<String> comboBox1; // Sessoes
    public JComboBox<String> comboBox2; // Lugares
    public JComboBox<String> comboBox3; // Filmes
    private JRadioButton criançaRadioButton;
    public JRadioButton normalRadioButton;
    private JRadioButton séniorRadioButton;
    private JButton adicionarButton;
    private JPanel painel1;
    private JPanel comboPanel;

    public ButtonGroup comboGroup;
    private final Consumer<Ticket> callback;

    private static String lastSelectedFilm = null;
    private static String lastSelectedSession = null;

    public BilheteOpções(Consumer<Ticket> callback) {
        this.callback = callback;

        setTitle("Opções de Bilhete");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setContentPane(painel1);

        AppData appData = AppData.getInstance();

        for (Film filme : appData.getFilms()) {
            comboBox3.addItem(filme.getName());
        }

        comboBox3.addActionListener(e -> {
            comboBox1.removeAllItems();
            comboBox2.removeAllItems();

            String filmeSelecionado = (String) comboBox3.getSelectedItem();
            for (Session sessao : appData.getSessions()) {
                if (sessao.getFilm().getName().equals(filmeSelecionado)) {
                    comboBox1.addItem(sessao.getHorario());
                }
            }
        });

        comboBox1.addActionListener(e -> {
            comboBox2.removeAllItems();
            String filmeSelecionado = (String) comboBox3.getSelectedItem();
            String sessaoSelecionada = (String) comboBox1.getSelectedItem();

            if (filmeSelecionado == null || sessaoSelecionada == null) return;

            for (Session sessao : appData.getSessions()) {
                if (sessao.getFilm().getName().equals(filmeSelecionado) &&
                        sessao.getHorario().equals(sessaoSelecionada)) {
                    for (String lugar : sessao.getAvailableSeats()) {
                        comboBox2.addItem(lugar);
                    }
                    break;
                }
            }
        });

        ButtonGroup tipoBilheteGroup = new ButtonGroup();
        tipoBilheteGroup.add(criançaRadioButton);
        tipoBilheteGroup.add(normalRadioButton);
        tipoBilheteGroup.add(séniorRadioButton);

        criançaRadioButton.addActionListener(e -> updateCombos());
        normalRadioButton.addActionListener(e -> updateCombos());
        séniorRadioButton.addActionListener(e -> updateCombos());

        if (lastSelectedFilm != null) {
            comboBox3.setSelectedItem(lastSelectedFilm);
            comboBox1.removeAllItems();
            for (Session sessao : appData.getSessions()) {
                if (sessao.getFilm().getName().equals(lastSelectedFilm)) {
                    comboBox1.addItem(sessao.getHorario());
                }
            }
        }

        if (lastSelectedFilm != null && lastSelectedSession != null) {
            comboBox1.setSelectedItem(lastSelectedSession);
            for (Session sessao : appData.getSessions()) {
                if (sessao.getFilm().getName().equals(lastSelectedFilm) &&
                        sessao.getHorario().equals(lastSelectedSession)) {
                    comboBox2.removeAllItems();
                    for (String lugar : sessao.getAvailableSeats()) {
                        comboBox2.addItem(lugar);
                    }
                    break;
                }
            }
        }

        adicionarButton.addActionListener(this::adicionarBilhete);
    }

    public void adicionarBilhete(ActionEvent e) {
        if (comboBox3.getSelectedItem() == null || comboBox1.getSelectedItem() == null || comboBox2.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Deve preencher os campos Filme, Sessão, Lugar", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!criançaRadioButton.isSelected() && !normalRadioButton.isSelected() && !séniorRadioButton.isSelected()) {
            JOptionPane.showMessageDialog(this, "Deve selecionar o tipo de bilhete", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nomeFilme = (String) comboBox3.getSelectedItem();
        String horarioSessao = (String) comboBox1.getSelectedItem();
        String nomeLugar = (String) comboBox2.getSelectedItem();

        AppData appData = AppData.getInstance();
        Session sessaoAtual = null;
        Room.Seat lugar = null;

        for (Session sessao : appData.getSessions()) {
            if (sessao.getFilm().getName().equals(nomeFilme) && sessao.getHorario().equals(horarioSessao)) {
                sessaoAtual = sessao;
                if (sessao.getRoom() == null || sessao.getRoom().getSeats() == null) {
                    break;
                }

                for (Room.Seat s : sessao.getRoom().getSeats()) {
                    if (s != null && s.toString().equals(nomeLugar)) {
                        lugar = s;
                        break;
                    }
                }
                break;
            }
        }

        if (sessaoAtual == null || lugar == null) {
            JOptionPane.showMessageDialog(this, "Sessão ou lugar inválido", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Ticket.Type tipo = null;
        if (criançaRadioButton.isSelected()) tipo = Ticket.Type.JUNIOR;
        else if (normalRadioButton.isSelected()) tipo = Ticket.Type.NORMAL;
        else if (séniorRadioButton.isSelected()) tipo = Ticket.Type.SENIOR;


        Combo selectedCombo = null;
        ButtonModel selectedComboModel = comboGroup != null ? comboGroup.getSelection() : null;
        if (selectedComboModel != null) {
            String comboStr = selectedComboModel.getActionCommand();
            for (Combo c : appData.getCombos()) {
                if (comboStr.contains(c.getProduct().getName()) && c.getTicket() == tipo) {
                    selectedCombo = c;
                    break;
                }
            }
        }

        Ticket ticket = new Ticket(sessaoAtual, lugar, tipo, selectedCombo);
        sessaoAtual.ocuparLugar(lugar);

        lastSelectedFilm = nomeFilme;
        lastSelectedSession = horarioSessao;

        callback.accept(ticket);
        dispose();
    }

    public void updateCombos() {
        comboPanel.removeAll();
        comboGroup = new ButtonGroup();

        Ticket.Type selectedType = null;
        if (criançaRadioButton.isSelected()) {
            selectedType = Ticket.Type.JUNIOR;

        } else if (normalRadioButton.isSelected()) {
            selectedType = Ticket.Type.NORMAL;
        } else if (séniorRadioButton.isSelected()) {
            selectedType = Ticket.Type.SENIOR;
        }

        if (selectedType != null) {
            for (Combo combo : AppData.getInstance().getCombos()) {
                if (combo.getTicket() == selectedType) {
                    JRadioButton btn = new JRadioButton(combo.toString());
                    btn.setActionCommand(combo.toString() + " (" + combo.getPrice() + "€)");
                    comboGroup.add(btn);
                    comboPanel.add(btn);
                }
            }
        }

        comboPanel.revalidate();
        comboPanel.repaint();
    }

    public double getPrecoComboSelecionado() {
        ButtonModel selected = comboGroup != null ? comboGroup.getSelection() : null;
        if (selected != null) {
            String action = selected.getActionCommand();
            int idx = action.lastIndexOf("(");
            if (idx != -1) {
                try {
                    String valor = action.substring(idx + 1, action.length() - 2).replace("€", "").trim();
                    return Double.parseDouble(valor);
                } catch (NumberFormatException ignored) {}
            }
        }
        return 0.0;
    }
}
