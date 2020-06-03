package com.planificador.dto;

import lombok.Data;

@Data
public class CuentaDTO {
	private Integer id;
	private String nombre;	
	private Double saldoInicial;
	private Double saldo;
	private String divisa;
	private String descripcion;	
	private Boolean habilitarCheques;
	private Boolean adicionarPatrimonioNeto;
	private String tipo;
	private Integer idGrupo;
	private String emailUsuario;
}
