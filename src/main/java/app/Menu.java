package app;

import app.management.ManagementMenu;
import app.statistics.StatisticsMenu;
import app.billing.Faturacao;

import javax.swing.*;

public class Menu extends JFrame{

    private JButton faturaçãoButton;
    private JButton gestãoButton;
    private JButton consultasButton;
    private JPanel panel1;


    public Menu() {
        setTitle("Menu");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(400, 300);
        setLocationRelativeTo(null);


        faturaçãoButton.addActionListener(e -> openFaturacao());
        gestãoButton.addActionListener(e -> openGestao());
        consultasButton.addActionListener(e -> openConsultas());

        setContentPane(panel1);
    }

    private void openConsultas() {
        StatisticsMenu consultas = new StatisticsMenu();
        consultas.setVisible(true);
        this.dispose(); // Close the menu window
    }

    private void openGestao() {
        ManagementMenu meny = new ManagementMenu();
        meny.setVisible(true);
        this.dispose();
    }

    private void openFaturacao() {
        Faturacao faturacao = new Faturacao();
        faturacao.setVisible(true);
        this.dispose();
    }
}
