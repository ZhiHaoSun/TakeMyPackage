package org.team7.TakeMyPackage.model;

/**
 * Proudly created by zhangxinye on 5/10/17.
 *
 */

public class Account {

    private String name;
    private String image;
    private String address;
    private String phone;
    private String nric;

    public Account(String name, String image, String address, String phone, String nric) {
        this.name = name;
        this.image = image;
        this.address = address;
        this.phone = phone;
        this.nric = nric;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
