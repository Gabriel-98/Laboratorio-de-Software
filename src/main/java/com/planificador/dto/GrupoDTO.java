package com.planificador.dto;

import lombok.Data;

@Data
public class GrupoDTO {
	private Integer id;
	private String nombre;
	private Double saldo;
	private String divisa;
	private String emailUsuario;
}
