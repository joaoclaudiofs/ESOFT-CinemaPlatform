package models;

public enum Language {
    NONE("NONE", "Nenhum"),
    PT("PT", "Português"),
    EN("EN", "Inglês"),
    ES("ES", "Espanhol"),
    FR("FR", "Francês"),
    DE("DE", "Alemão"),
    IT("IT", "Italiano");

    private String abbreviation;
    private String name;

    Language(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.name = name;
    }

    public String getAbbreviation() {
        return this.abbreviation;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.abbreviation == "NONE" ? this.name : this.abbreviation + " - " + this.name;
    }
}
