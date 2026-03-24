package app.billing;

import javax.swing.*;

public class Gestao extends JFrame{
    private JTabbedPane tabbedPane1;
    private JList list1;
    private JButton editarButton;
    private JButton adicionarButton;
    private JButton removerButton;
    private JList list2;
    private JList list3;
    private JList list5;
    private JPanel panel1;
    private JFormattedTextField a750€FormattedTextField;
    private JFormattedTextField a500€FormattedTextField;
    private JFormattedTextField a600€FormattedTextField;
    private JList list4;

    public Gestao() {
        setTitle("Gestão");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setContentPane(panel1);
    }
}
