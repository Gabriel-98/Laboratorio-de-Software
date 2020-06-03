package com.planificador.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class TarjetaCreditoDTO extends CuentaDTO {
	
	private Double limiteCredito;
}
