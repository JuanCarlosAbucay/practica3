package com.example.bancobanco.dao;

import com.example.bancobanco.beans.Clients;

import java.util.List;

public interface Dao {
    Clients getById(int id);
    List<Clients> getAll();
    void save(Clients clients);
    void update(Clients clients);
    void delete(Clients clients);
}
