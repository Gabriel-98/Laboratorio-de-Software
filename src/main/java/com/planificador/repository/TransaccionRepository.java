package com.planificador.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planificador.entity.Transaccion;
import com.planificador.entity.Usuario;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion,Integer> {
	List<Transaccion> findAllByUsuarioOrderById(Usuario usuario);
	List<Transaccion> findAllByUsuarioAndTipoOrderById(Usuario usuario, String tipo);
}
