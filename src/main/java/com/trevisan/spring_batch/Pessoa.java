package com.trevisan.spring_batch;

public class Pessoa {
    public String name;
    public String document;
    public String email;
    public String phone;
    public String age;

    public Pessoa(String name, String document, String email, String phone, String age) {
        this.name = name;
        this.document = document;
        this.email = email;
        this.phone = phone;
        this.age = age;
    }

    public Pessoa(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
