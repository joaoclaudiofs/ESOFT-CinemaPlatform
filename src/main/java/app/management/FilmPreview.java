package app.management;

import app.generics.ObjectPreview;
import models.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FilmPreview extends ObjectPreview {
    private JPanel panelMain;
    private JTextField fieldName;
    private JComboBox comboGenre;
    private JComboBox comboRating;
    private JTextField fieldDuration;
    private JList listLanguage;
    private JList listSubtitles;
    private JList listImageSystem;
    private JTextField fieldPublisher;
    private JComboBox comboImageSystem;
    private Film film;
    private ManagementMenu managementMenu;

    public FilmPreview(ManagementMenu managementMenu, PreviewType type, Film film) {
        super(
                type == PreviewType.VIEW
                        ? "Ver Filme"
                        : type == PreviewType.EDIT
                        ? "Editar Filme"
                        : "Criar Filme",
                type == PreviewType.VIEW ? Button.BACK : Button.CANCEL,
                type != PreviewType.VIEW ? Button.SAVE : null);

        this.managementMenu = managementMenu;
        this.previewType = type;
        setMainPanel(panelMain);

        fieldDuration.setText("0");

        for (Film.Genre genre : Film.Genre.values()) {
            comboGenre.addItem(genre);
        }

        for (Film.Rating rating : Film.Rating.values()) {
            comboRating.addItem(rating);
        }

        DefaultListModel<Language> languagesModel = new DefaultListModel<>();
        DefaultListModel<Language> subtitlesModel = new DefaultListModel<>();
        for (Language language: Language.values()) {
            languagesModel.addElement(language);
            subtitlesModel.addElement(language);
        }
        listLanguage.setModel(languagesModel);
        listSubtitles.setModel(subtitlesModel);

        DefaultListModel<Room.ImageSystem> imageSystemsModel = new DefaultListModel<>();
        for (Room.ImageSystem imageSystem : Room.ImageSystem.values()) {
            imageSystemsModel.addElement(imageSystem);
        }
        listImageSystem.setModel(imageSystemsModel);

        if (film != null) {
            this.film = film;
            super.object = film;

            fieldName.setText(film.getName());
            fieldPublisher.setText(film.getPublisher());
            fieldDuration.setText(String.valueOf(film.getDuration()));
            comboGenre.setSelectedItem(film.getGenre());
            comboRating.setSelectedItem(film.getRating());

            if (type == PreviewType.VIEW) {
                languagesModel.clear();
                for (Language language : film.getLanguages()) {
                    languagesModel.addElement(language);
                }

                subtitlesModel.clear();
                for (Language subtitle : film.getSubtitles()) {
                    subtitlesModel.addElement(subtitle);
                }

                imageSystemsModel.clear();
                for (Room.ImageSystem imageSystem : film.getImageSystems()) {
                    imageSystemsModel.addElement(imageSystem);
                }
            } else {
                for (Language language : film.getLanguages()) {
                    int index = languagesModel.indexOf(language);
                    listLanguage.addSelectionInterval(index, index);
                }

                for (Language subtitle : film.getSubtitles()) {
                    int index = languagesModel.indexOf(subtitle);
                    listSubtitles.addSelectionInterval(index, index);
                }

                for (Room.ImageSystem imageSystem : film.getImageSystems()) {
                    int index = imageSystemsModel.indexOf(imageSystem);
                    listImageSystem.addSelectionInterval(index, index);
                }
            }

            List<Session> associatedSessions = new ArrayList<>();
            for (Session session : AppData.getInstance().getSessions()) {
                if (session.getFilm() == film) {
                    associatedSessions.add(session);
                }
            }

            this.setFooterInfo(associatedSessions.size() + " sessão(ões) associada(s).");
        }

        if (type == PreviewType.VIEW) {
            fieldName.setEnabled(false);
            fieldPublisher.setEnabled(false);
            fieldDuration.setEnabled(false);
            comboGenre.setEnabled(false);
            comboRating.setEnabled(false);
            listLanguage.setEnabled(false);
            listSubtitles.setEnabled(false);
            listImageSystem.setEnabled(false);
        }

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
        String name = fieldName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Insira um nome válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String publisher = fieldPublisher.getText().trim();
        if (publisher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Insira um editor válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float duration = 0;
        try {
            duration = Float.parseFloat(fieldDuration.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Insira uma duração válida.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (duration <= 0) {
            JOptionPane.showMessageDialog(this, "A duração não pode ser negativa.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Film.Genre genre = (Film.Genre) comboGenre.getSelectedItem();
        if (genre == null) {
            JOptionPane.showMessageDialog(this, "Selecione um género válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Film.Rating rating = (Film.Rating) comboRating.getSelectedItem();
        if (rating == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma classificação válida.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Language> languages = listLanguage.getSelectedValuesList();
        if (languages.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione pelo menos um idioma.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Language> subtitles = listSubtitles.getSelectedValuesList();
        if (subtitles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione pelo menos uma legenda.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Room.ImageSystem> imageSystems = listImageSystem.getSelectedValuesList();
        if (imageSystems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione pelo menos um sistema de imagem.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (previewType == PreviewType.CREATE) {
            AppData.getInstance().addFilm(new Film(
                name,
                    duration,
                    publisher,
                    new HashSet<>(languages),
                    new HashSet<>(subtitles),
                    genre,
                    rating,
                    new HashSet<>(imageSystems)
            ));
            JOptionPane.showMessageDialog(this, "Filme salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else if (film != null && previewType == PreviewType.EDIT) {
            film.setName(name);
            film.setDuration(duration);
            film.setPublisher(publisher);
            film.setGenre(genre);
            film.setRating(rating);
            film.setLanguages(new HashSet<>(languages));
            film.setSubtitles(new HashSet<>(subtitles));
            film.setImageSystems(new HashSet<>(imageSystems));


            JOptionPane.showMessageDialog(this, "Filme atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }

        this.managementMenu.loadFilms();
        this.dispose();
    }
}