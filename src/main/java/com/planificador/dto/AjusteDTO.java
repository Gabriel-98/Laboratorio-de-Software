package com.planificador.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonTypeName("AJUSTE")
public class AjusteDTO extends TransaccionDTO {
	private Integer idCuenta;
	private Double saldoAdicional;
}
