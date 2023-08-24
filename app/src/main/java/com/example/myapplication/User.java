package com.example.myapplication;

import java.util.List;

public class User {
        private String firstName;
        private String lastName;

        private String email;
        private String phone;
        private boolean isRegistered;
        private List<Float> idVector;
        public User(String firstName, String lastName, String email, String phone, boolean isRegistered, List<Float> idVector) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phone = phone;
            this.isRegistered = isRegistered;
            this.idVector = idVector;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public List<Float> getIdVector() {
        return idVector;
    }

    public void setIdVector(List<Float> idVector) {
        this.idVector = idVector;
    }
}
