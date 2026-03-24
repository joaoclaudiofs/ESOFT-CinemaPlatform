package app.management;

import app.generics.ObjectPreview;
import app.generics.RoomSeatsPreview;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import models.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class RoomPreview extends ObjectPreview {
    private JPanel panelMain;
    private JTextField fieldId;
    private JTextField fieldRows;
    private JTextField fieldCols;
    private JComboBox comboImageSystem;
    private JButton buttonRoomLayout;
    private JLabel labelRoomLayout;
    private JComboBox comboSoundSystem;
    private Room room;
    private ManagementMenu managementMenu;

    Room.Seat[] seats = null;
    RoomSeatsPreview lastPreview = null;

    public RoomPreview(ManagementMenu managementMenu, PreviewType type, Room room) {
        super(
            type == PreviewType.VIEW
                ? "Ver Sala"
                : type == PreviewType.EDIT
                    ? "Editar Sala"
                    : "Criar Sala",
            type == PreviewType.VIEW ? Button.BACK : Button.CANCEL,
            type != PreviewType.VIEW ? Button.SAVE : null);

        this.managementMenu = managementMenu;
        this.previewType = type;
        setMainPanel(panelMain);

        fieldRows.setText("1");
        fieldCols.setText("1");

        labelRoomLayout.setIcon(new FlatSVGIcon("icons/alerts_feedback/alert-circle.svg", 16, 16));
        labelRoomLayout.setVisible(false);

        for (Room.ImageSystem imageSystem : Room.ImageSystem.values()) {
            comboImageSystem.addItem(imageSystem);
        }

        for (Room.SoundSystem soundSystem : Room.SoundSystem.values()) {
            comboSoundSystem.addItem(soundSystem);
        }

        if (room != null) {
            this.room = room;
            super.object = room;

            fieldId.setText(room.getId());
            fieldRows.setText(String.valueOf(room.getNumRows()));
            fieldCols.setText(String.valueOf(room.getNumColumns()));
            comboImageSystem.setSelectedItem(room.getImageSystem());
            comboSoundSystem.setSelectedItem(room.getSoundSystem());

            List<Session> associatedSessions = new ArrayList<>();
            for (Session session : AppData.getInstance().getSessions()) {
                if (session.getRoom() == room) {
                    associatedSessions.add(session);
                }
            }

            fieldRows.setEnabled(false);
            fieldCols.setEnabled(false);

            this.setFooterInfo(associatedSessions.size() + " sessão(ões) associada(s).");
        }

        if (type == PreviewType.VIEW) {
            fieldId.setEnabled(false);
            fieldRows.setEnabled(false);
            fieldCols.setEnabled(false);
            comboImageSystem.setEnabled(false);
            comboSoundSystem.setEnabled(false);
            buttonRoomLayout.setText("Ver Sala");
        }

        fieldRows.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                try {
                    int rows = Integer.parseInt(fieldRows.getText());
                    if (rows < 1) {
                        fieldRows.setText("1");
                    }
                } catch (NumberFormatException e) {
                    fieldRows.setText("1");
                }
                labelRoomLayout.setVisible(true);
            }
        });

        fieldCols.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                try {
                    int cols = Integer.parseInt(fieldCols.getText());
                    if (cols < 1) {
                        fieldCols.setText("1");
                    }
                } catch (NumberFormatException e) {
                    fieldCols.setText("1");
                }
                labelRoomLayout.setVisible(true);
            }
        });

        this.seats = room != null ? room.getSeats() : new Room.Seat[0];
        buttonRoomLayout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                labelRoomLayout.setVisible(false);

                int rows;
                try {
                    rows = Integer.parseInt(fieldRows.getText().trim());
                    if (rows < 1) {
                        rows = 1;
                    }
                } catch (NumberFormatException ex) {
                    rows = 0;
                }

                int cols;
                try {
                    cols = Integer.parseInt(fieldCols.getText().trim());
                    if (cols < 1) {
                        cols = 1;
                    }
                } catch (NumberFormatException ex) {
                    cols = 0;
                }

                if (seats.length != rows * cols) {
                    seats = new Room.Seat[rows * cols];
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            seats[i * cols + j] = new Room.Seat(i + 1, j + 1, false);
                        }
                    }
                }

                if (lastPreview != null && lastPreview.isDisplayable()) {
                    lastPreview.dispose();
                }

                RoomSeatsPreview menu = new RoomSeatsPreview(RoomPreview.this, seats, rows, cols, type != PreviewType.VIEW);
                lastPreview = menu;
            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (lastPreview != null && lastPreview.isDisplayable()) {
                    lastPreview.dispose();
                }
            }
        });

        this.setVisible(true);
    }

    @Override
    public void onBack(ActionEvent e) {
        this.dispose();
    }

    @Override
    public void onCancel(ActionEvent e) {
        this.dispose();
    }

    @Override
    public void onSave(ActionEvent ev) {
        String id = fieldId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Insira um identificador válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int rows;
        try {
            rows = Integer.parseInt(fieldRows.getText().trim());
            if (rows < 1) {
                JOptionPane.showMessageDialog(this, "Insira um número de linhas válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Insira um número de linhas válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int cols;
        try {
            cols = Integer.parseInt(fieldCols.getText().trim());
            if (cols < 1) {
                JOptionPane.showMessageDialog(this, "Insira um número de colunas válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Insira um número de colunas válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Room.ImageSystem imageSystem = (Room.ImageSystem) comboImageSystem.getSelectedItem();
        if (imageSystem == null) {
            JOptionPane.showMessageDialog(this, "Selecione um sistema de imagem válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Room.SoundSystem soundSystem = (Room.SoundSystem) comboSoundSystem.getSelectedItem();
        if (soundSystem == null) {
            JOptionPane.showMessageDialog(this, "Selecione um sistema de som válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (previewType == PreviewType.CREATE) {
            Room newRoom = new Room(
                id,
                rows,
                cols,
                imageSystem,
                soundSystem
            );

            int index = 0;
            for (Room.Seat seat : newRoom.getSeats()) {
                int row = seat.getRow();
                int col = seat.getColumn();
                Room.Seat savedSeat = this.seats.length > index ? this.seats[index++] : null;

                if (savedSeat != null) {
                    seat.setAccessible(savedSeat.isAccessible());
                } else {
                    seat.setAccessible(false);
                }
            }

            AppData.getInstance().addRoom(newRoom);
            JOptionPane.showMessageDialog(this, "Sala salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }

        this.managementMenu.loadRooms();
        this.dispose();
    }
}