package app.management;

import app.generics.ObjectPreview;
import models.*;
import models.sale.sellable.Ticket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SessionPreview extends ObjectPreview {
    private JPanel panelMain;
    private JTextField fieldDate;
    private JComboBox comboRoom;
    private JComboBox comboFilm;
    private JComboBox comboLanguage;
    private JComboBox comboSubtitles;
    private Session session;
    private ManagementMenu managementMenu;

    public SessionPreview(ManagementMenu managementMenu, PreviewType type, Session session) {
        super(
            type == PreviewType.VIEW
                ? "Ver Sessão"
                : type == PreviewType.EDIT
                    ? "Editar Sessão"
                    : "Criar Sessão",
            type == PreviewType.VIEW ? Button.BACK : Button.CANCEL,
            type != PreviewType.VIEW ? Button.SAVE : null);

        this.managementMenu = managementMenu;
        this.previewType = type;
        setMainPanel(panelMain);

        for (Film film: AppData.getInstance().getFilms()) {
            comboFilm.addItem(film);
        }
        comboFilm.setSelectedItem(null);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        fieldDate.setText(dateFormatter.format(new Date()));

        if (session != null) {
            this.session = session;
            super.object = session;

            for (Room room: AppData.getInstance().getRooms()) {
                if (session.getFilm().getImageSystems().contains(room.getImageSystem())) {
                    comboRoom.addItem(room);
                }
            }
            comboRoom.setSelectedItem(session.getFilm());

            for (models.Language language : session.getFilm().getLanguages()) {
                comboLanguage.addItem(language);
            }
            if (session.getFilm().getLanguages() != null) {
                comboLanguage.setSelectedItem(session.getLanguage());
            } else {
                comboLanguage.setSelectedItem(null);
            }

            for (models.Language subtitles : session.getFilm().getSubtitles()) {
                comboSubtitles.addItem(subtitles);
            }
            if (session.getFilm().getSubtitles() != null) {
                comboLanguage.setSelectedItem(session.getSubtitles());
            } else {
                comboLanguage.setSelectedItem(null);
            }

            comboFilm.setSelectedItem(session.getFilm());
            comboRoom.setSelectedItem(session.getRoom());
            fieldDate.setText(dateFormatter.format(session.getDate()));
            comboLanguage.setSelectedItem(session.getLanguage());
            comboSubtitles.setSelectedItem(session.getSubtitles());

            this.setFooterInfo(session.getOccupiedSeats().length + " bilhete(s) associado(s).");
        }

        if (type == PreviewType.VIEW) {
            fieldDate.setEnabled(false);
            comboRoom.setEnabled(false);
            comboFilm.setEnabled(false);
            comboLanguage.setEnabled(false);
            comboSubtitles.setEnabled(false);
        }

        comboFilm.addActionListener(ActionListener -> {
            Film selectedFilm = (Film) comboFilm.getSelectedItem();
            if (selectedFilm != null) {
                Room selectedRoom = (Room) comboRoom.getSelectedItem();
                Language selectedLanguage = (Language) comboLanguage.getSelectedItem();
                Language selectedSubtitles =  (Language) comboSubtitles.getSelectedItem();

                comboRoom.removeAllItems();
                comboLanguage.removeAllItems();
                comboSubtitles.removeAllItems();

                for (Room room : AppData.getInstance().getRooms()) {
                    if (selectedFilm.getImageSystems().contains(room.getImageSystem())) {
                        comboRoom.addItem(room);
                    }
                }
                comboRoom.setSelectedItem(null);
                if (selectedRoom != null && selectedFilm.getImageSystems().contains(selectedRoom.getImageSystem())) {
                    comboRoom.setSelectedItem(selectedRoom);
                }

                for (models.Language language : selectedFilm.getLanguages()) {
                    comboLanguage.addItem(language);
                }
                comboLanguage.setSelectedItem(null);
                if (selectedFilm.getLanguages().contains(selectedLanguage)) {
                    comboLanguage.setSelectedItem(selectedLanguage);
                }

                for (models.Language subtitles : selectedFilm.getSubtitles()) {
                    comboSubtitles.addItem(subtitles);
                }
                comboSubtitles.setSelectedItem(null);
                if (selectedFilm.getSubtitles().contains(selectedSubtitles)) {
                    comboSubtitles.setSelectedItem(selectedSubtitles);
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
        SimpleDateFormat dateParser = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date;
        try {
            date = dateParser.parse(fieldDate.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Insira uma data válida.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Calendar today = Calendar.getInstance();
        today.add(Calendar.MINUTE, 30);
        if (date.after(today.getTime())) {
            JOptionPane.showMessageDialog(this, "Insira uma data de à pelo menos 30 minutos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        today.add(Calendar.MINUTE, -30);

        Film film = (Film) comboFilm.getSelectedItem();
        if (film == null) {
            JOptionPane.showMessageDialog(this, "Selecione um filme válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Room room = (Room) comboRoom.getSelectedItem();
        if (room == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma sala válida.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Language language = (Language) comboLanguage.getSelectedItem();;
        if (language == null || !film.getSubtitles().contains(comboLanguage.getSelectedItem())) {
            JOptionPane.showMessageDialog(this, "Selecione uma idioma válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Language subtitles = (Language) comboSubtitles.getSelectedItem();;
        if (subtitles == null || !film.getSubtitles().contains(comboSubtitles.getSelectedItem())) {
            JOptionPane.showMessageDialog(this, "Selecione uma legenda válida.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (previewType == PreviewType.CREATE) {
            AppData.getInstance().addSession(new Session(
                room,
                new Room.Seat[0],
                date,
                film,
                language,
                subtitles
            ));
            JOptionPane.showMessageDialog(this, "Sessão salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else if (session != null && previewType == PreviewType.EDIT) {

            if (session.getRoom() != room && session.getOccupiedSeats().length != 0) {
                int contirm = JOptionPane.showConfirmDialog(this, "Esta sessão tem bilhetes associados para outra sala. Deseja cancelar os bilhetes?", "Erro", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
                if (contirm == JOptionPane.YES_OPTION) {
                    List<Ticket> associatedTickets = AppData.getInstance().getTicketsBySession((Session) session);
                    for (Ticket ticket : associatedTickets) {
                        if (ticket.getState() == Ticket.State.ACTIVE) {
                            ticket.setState(Ticket.State.CANCELED);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "A sessão não pode ser editada porque tem bilhetes associados.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            session.setFilm(film);
            session.setRoom(room);
            session.setDate(date);
            session.setLanguage(language);
            session.setSubtitles(subtitles);

            JOptionPane.showMessageDialog(this, "Sessão atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }

        this.managementMenu.loadSessions();
        this.dispose();
    }
}