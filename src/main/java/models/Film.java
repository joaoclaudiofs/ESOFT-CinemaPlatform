package models;

import java.io.Serializable;
import java.util.Set;

public class Film implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private float duration;
    private String publisher;
    private Set<Language> languages;
    private Set<Language> subtitles;
    private Genre genre;
    private Rating rating;
    private Set<Room.ImageSystem> imageSystems;

    public Film(String name, float duration, String publisher, Set<Language> languages, Set<Language> subtitles, Genre genre, Rating rating, Set<Room.ImageSystem> imageSystems) {
        this.name = name;
        this.duration = duration;
        this.publisher = publisher;
        this.languages = languages;
        this.subtitles = subtitles;
        this.genre = genre;
        this.rating = rating;
        this.imageSystems = imageSystems;
    }

    public String getName() {
        return name;
    }

    public float getDuration() {
        return duration;
    }

    public String getPublisher() {
        return publisher;
    }

    public Genre getGenre() {
        return genre;
    }

    public Rating getRating() {
        return rating;
    }

    public Set<Language> getLanguages() {
        return languages;
    }

    public Set<Language> getSubtitles() {
        return subtitles;
    }

    public Set<Room.ImageSystem> getImageSystems() {
        return imageSystems;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public void setSubtitles(Set<Language> subtitles) {
        this.subtitles = subtitles;
    }

    public void setImageSystems(Set<Room.ImageSystem> imageSystems) {
        this.imageSystems = imageSystems;
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }

    public enum Genre {
        ACTION("Ação"),
        DRAMA("Drama"),
        COMEDY("Comédia"),
        HORROR("Terror"),
        THRILLER("Thriller"),
        ANIMATION("Animação"),
        DOCUMENTARY("Documentário"),
        SCIENCE_FICTION("Ficção Científica"),
        FANTASY("Fantasia"),
        ROMANCE("Romance"),
        ADVENTURE("Aventura"),
        MYSTERY("Mistério");

        private String name;

        Genre(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum Rating {
        S(0),
        U7(7),
        U12(12),
        U16(16),
        U18(18);

        private int age;

        Rating(int age) {
            this.age = age;
        }

        public int getAge() {
            return this.age;
        }

        public boolean isAgeInRating(int age) {
            return age >= this.age;
        }
    }

    @Override
    public String toString() {
        return this.name + " (" + this.publisher + ")";
    }
}
