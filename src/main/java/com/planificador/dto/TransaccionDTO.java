package com.planificador.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Data;

@Data
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "tipo", visible=true)
@JsonSubTypes({
	@JsonSubTypes.Type(value = AjusteDTO.class, name = "AJUSTE"),
	@JsonSubTypes.Type(value = TransferenciaDTO.class, name = "TRANSFERENCIA")
})
public class TransaccionDTO {
	private Integer id;
	private String tipo;
}
