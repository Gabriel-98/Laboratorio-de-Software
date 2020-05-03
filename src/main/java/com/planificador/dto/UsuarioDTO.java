package com.planificador.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
	private String email;
	private String nombre;
	private String password;
	private Double saldo;
	private String divisa;
	private Boolean habilitarSeparadorAutomatico;
	private Boolean habilitarSaldoEjecucion;
}
