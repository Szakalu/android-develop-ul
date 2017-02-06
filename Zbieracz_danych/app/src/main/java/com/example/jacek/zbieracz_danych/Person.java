package com.example.jacek.zbieracz_danych;

/**
 * Created by Jacek on 02.01.2017.
 */

public class Person {
    int id;
    String name;
    String surname;
    String birthDayDate;
    String photoPath;

    public Person(int id, String name, String surname, String birthDayDate, String photoPath) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDayDate = birthDayDate;
        this.photoPath = photoPath;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthDayDate() {
        return birthDayDate;
    }

    public void setBirthDayDate(String birthDayDate) {
        this.birthDayDate = birthDayDate;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
