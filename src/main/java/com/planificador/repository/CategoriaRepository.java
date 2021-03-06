package com.planificador.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planificador.entity.Categoria;
import com.planificador.entity.Usuario;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,Integer> {
	boolean existsByUsuarioAndNombre(Usuario usuario, String nombre);
	List<Categoria> findAllByUsuarioOrderByNombreAsc(Usuario usuario);
	List<Categoria> findAllByUsuarioAndTipoOrderByNombreAsc(Usuario usuario, String tipo);
}
