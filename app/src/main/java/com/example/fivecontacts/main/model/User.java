package com.example.fivecontacts.main.model;

import java.io.Serializable;
import java.lang.reflect.Array;

public class User implements Serializable {
    String nome;
    String login;
    String senha;
    String email;


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
