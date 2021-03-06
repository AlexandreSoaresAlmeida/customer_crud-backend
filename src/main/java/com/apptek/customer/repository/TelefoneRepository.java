package com.apptek.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apptek.customer.model.Telefone;

@Repository
public interface TelefoneRepository extends JpaRepository<Telefone, Integer> {

}