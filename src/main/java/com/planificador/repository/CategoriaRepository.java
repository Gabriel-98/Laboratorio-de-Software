package com.planificador.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.planificador.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria,Integer> {
	
}
