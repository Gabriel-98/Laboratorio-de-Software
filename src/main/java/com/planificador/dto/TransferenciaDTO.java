package com.planificador.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonTypeName("TRANSFERENCIA")
public class TransferenciaDTO extends TransaccionDTO {
	private Integer idCuentaOrigen;
	private Integer idCuentaDestino;
	private Double cantidadEnviada;
	private Double cantidadRecibida;
}
