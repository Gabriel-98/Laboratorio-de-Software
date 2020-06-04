package com.planificador.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonTypeName("TARJETA-CREDITO")
public class TarjetaCreditoDTO extends CuentaDTO {
	
	private Double limiteCredito;
}
