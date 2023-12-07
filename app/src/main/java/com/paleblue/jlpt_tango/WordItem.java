package com.paleblue.jlpt_tango;

public class WordItem {
    private int id;
    private String word;
    private String pronunciation;
    private String meaning;
    private String tableName;
    private boolean known;
    public WordItem(int id, String word, String pronunciation, String meaning, String tableName) {
        this.id = id;
        this.word = word;
        this.pronunciation = pronunciation;
        this.meaning = meaning;
        this.tableName = tableName;
        this.known = false;
    }

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getMeaning() {
        return meaning;
    }

    public boolean isKnown() {
        return known;
    }

    public void setKnown(boolean known) {
        this.known = known;
    }
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
