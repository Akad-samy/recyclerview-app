package com.aksa.stories;

class ChapterModel {
    int id;
    String chapter_nbr;
    String chapter_detail;

    public ChapterModel(int id, String chapter_nbr, String chapter_detail) {
        this.id = id;
        this.chapter_nbr = chapter_nbr;
        this.chapter_detail = chapter_detail;
    }

    public ChapterModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChapter_nbr() {
        return chapter_nbr;
    }

    public void setChapter_nbr(String chapter_nbr) {
        this.chapter_nbr = chapter_nbr;
    }

    public String getChapter_detail() {
        return chapter_detail;
    }

    public void setChapter_detail(String chapter_detail) {
        this.chapter_detail = chapter_detail;
    }

    @Override
    public String toString() {
        return "ChapterModel{" +
                "id=" + id +
                ", chapter_nbr='" + chapter_nbr + '\'' +
                ", chapter_detail='" + chapter_detail + '\'' +
                '}';
    }
}
