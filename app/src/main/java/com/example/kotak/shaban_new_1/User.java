package com.example.kotak.shaban_new_1;

import java.io.Serializable;

/**
 * Created by Daren Liu on 7/24/2016.
 */
public class User implements Serializable {

    private String phone;
    private String firstName;
    private String lastName;

    User(String firstName, String lastName, String phone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getName(){
        return firstName + " " + lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
