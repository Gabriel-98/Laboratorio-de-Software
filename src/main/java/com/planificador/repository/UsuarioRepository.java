package com.planificador.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.planificador.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario,String> {
	long countByClave(String clave);
}
