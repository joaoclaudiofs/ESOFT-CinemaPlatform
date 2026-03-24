package app.generics;

import app.management.ManagementMenu;
import app.management.RoomPreview;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import models.Room;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;

public class RoomSeatsPreview extends JFrame {
    private JPanel panelMain;
    private JPanel panelSeats;
    private JPanel panelScreen;
    private JScrollPane scrollPane;

    public RoomSeatsPreview(JFrame frame, Room.Seat[] seats, int rows, int cols, boolean editable) {
        super("Visualizar Lugares da Sala");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(300, 400);
        setMinimumSize(getSize());
        setLocationRelativeTo(frame);
        setContentPane(panelMain);

        scrollPane.setBorder(null);
        panelSeats.setLayout(new BoxLayout(panelSeats, BoxLayout.Y_AXIS));
        panelScreen.setLayout(new BoxLayout(panelScreen, BoxLayout.X_AXIS));


        Dimension btnSize = new Dimension(32, 32);
        for (int i = 0; i < rows; i++) {
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));

            JButton numButton = new JButton(numToLetter(i + 1));
            numButton.setAlignmentX(LEFT_ALIGNMENT);
            numButton.setEnabled(false);
            numButton.setPreferredSize(btnSize);
            numButton.setMaximumSize(btnSize);
            numButton.setMinimumSize(btnSize);
            numButton.setBorder(null);
            rowPanel.add(numButton);

            for (int j = 0; j < cols; j++) {
                JButton seatButton = new JButton();
                seatButton.setAlignmentX(CENTER_ALIGNMENT);
                seatButton.setPreferredSize(btnSize);
                seatButton.setMaximumSize(btnSize);
                seatButton.setMinimumSize(btnSize);
                seatButton.setBorder(null);
                seatButton.setEnabled(true);

                FlatSVGIcon seatIcon = new FlatSVGIcon("icons/seat.svg", 24, 24);
                final int row = i;
                final int col = j;
                seatIcon.setColorFilter(new FlatSVGIcon.ColorFilter() {
                    @Override
                    public Color filter(Color color) {
                        if (seats != null && seats[row * cols + col] != null) {
                            if (seats[row * cols + col].isAccessible()) {
                                return editable ? new Color(0x0592aa) : new Color(0x6ad3e5);
                            }
                        }
                        return editable ? new Color(0x17171e) : new Color(0x918fac);
                    }
                });

                if (editable) {
                    seatButton.addActionListener(e -> {
                        if (seats != null && seats[row * cols + col] != null) {
                            Room.Seat seat = seats[row * cols + col];
                            seat.setAccessible(!seat.isAccessible());

                            seatIcon.setColorFilter(new FlatSVGIcon.ColorFilter() {
                                @Override
                                public Color filter(Color color) {
                                    if (seat.isAccessible()) {
                                        if (seats[row * cols + col].isAccessible()) {
                                            return new Color(0x0592aa);
                                        }
                                    }
                                    return new Color(0x17171e);
                                }
                            });
                            seatButton.setIcon(seatIcon);
                        }
                    });
                }

                seatButton.setIcon(seatIcon);
                rowPanel.add(seatButton);
            }

            JButton emptyButton = new JButton();
            emptyButton.setEnabled(false);
            emptyButton.setPreferredSize(btnSize);
            emptyButton.setMaximumSize(btnSize);
            emptyButton.setMinimumSize(btnSize);
            emptyButton.setBorder(null);
            rowPanel.add(emptyButton);

            panelSeats.add(rowPanel);
        }

        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));

        JButton emptyButton = new JButton();
        emptyButton.setEnabled(false);
        emptyButton.setPreferredSize(btnSize);
        emptyButton.setMaximumSize(btnSize);
        emptyButton.setMinimumSize(btnSize);
        emptyButton.setBorder(null);
        rowPanel.add(emptyButton);

        for (int i = 0; i < cols; i++) {
            JButton numButton = new JButton(String.valueOf(i + 1));
            numButton.setAlignmentX(LEFT_ALIGNMENT);
            numButton.setEnabled(false);
            numButton.setPreferredSize(btnSize);
            numButton.setMaximumSize(btnSize);
            numButton.setMinimumSize(btnSize);
            numButton.setBorder(null);
            rowPanel.add(numButton);
        }
        emptyButton = new JButton();
        emptyButton.setEnabled(false);
        emptyButton.setPreferredSize(btnSize);
        emptyButton.setMaximumSize(btnSize);
        emptyButton.setMinimumSize(btnSize);
        emptyButton.setBorder(null);
        rowPanel.add(emptyButton);
        panelSeats.add(rowPanel);

        setVisible(true);
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
