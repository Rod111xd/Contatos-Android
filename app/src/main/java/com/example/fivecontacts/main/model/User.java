package com.example.fivecontacts.main.model;

import java.io.Serializable;

public class User implements Serializable {
    String nome;


    public User(String nome, String login, String password) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


}
