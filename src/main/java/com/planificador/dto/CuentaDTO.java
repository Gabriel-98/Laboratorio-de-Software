package com.planificador.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.*;

import lombok.Data;

@Data
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "tipo", visible=true)
@JsonSubTypes({
	@JsonSubTypes.Type(value = CuentaDTO.class, name = "CHEQUES"),
	@JsonSubTypes.Type(value = CuentaDTO.class, name = "AHORROS"),
	@JsonSubTypes.Type(value = CuentaDTO.class, name = "EFECTIVO"),
	@JsonSubTypes.Type(value = TarjetaCreditoDTO.class, name = "TARJETA-CREDITO")
})
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
