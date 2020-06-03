package com.planificador.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planificador.entity.Grupo;
import com.planificador.entity.Usuario;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo,Integer> {
	boolean existsByUsuarioAndNombre(Usuario usuario, String nombre);
	List<Grupo> findAllByUsuarioOrderByIdAsc(Usuario usuario);
}
