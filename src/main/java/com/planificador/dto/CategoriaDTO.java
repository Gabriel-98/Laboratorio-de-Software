package com.planificador.dto;

import lombok.Data;

@Data
public class CategoriaDTO {
	private Integer id;
	private String nombre;
	private String tipo;
	private String codigoIcono;
	private String emailUsuario;
}
