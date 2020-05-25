package com.planificador.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planificador.entity.Etiqueta;
import com.planificador.entity.Usuario;

@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta,Integer> {
	boolean existsByUsuarioAndNombre(Usuario usuario, String nombre);
	List<Etiqueta> findAllByUsuarioOrderByNombreAsc(Usuario usuario);
}
