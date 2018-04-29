package com.example.kotak.shaban_new_1;

/**
 * Created by kotak on 10/04/2018.
 */

public class Author {

private String firstName;
private String lastName;
private String phone;
private String createdAt;
private String updateAt;
private String roleId;

    public Author(String firstName, String lastName, String phone, String createdAt, String updateAt, String roleId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
        this.roleId = roleId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
