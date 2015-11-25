package com.bpatech.trucktracking.DTO;

/**
 * Created by Anita on 9/10/2015.
 */
public class User {

    int user_id;
    String companyName;
    String userName;
    String phone_no;
   boolean userStaus;

    public User() {
    }


    public User(int user_id, String phone_no, String companyName,
                String userName) {
        super();
        this.user_id = user_id;
        this.phone_no = phone_no;
        this.companyName = companyName;
        this.userName = userName;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUser_id() {

        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

}