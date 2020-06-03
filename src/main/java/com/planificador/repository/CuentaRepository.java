package com.planificador.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planificador.entity.Cuenta;
import com.planificador.entity.Usuario;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta,Integer> {
	boolean existsByUsuarioAndNombre(Usuario usuario, String nombre);
}
