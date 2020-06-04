package com.planificador.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="tarjetas_credito")
public class TarjetaCredito {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id", nullable=false, updatable=false)
	private Integer id;
	
	@Column(name="limite_credito", nullable=false, updatable=true)
	private Double limiteCredito;
	
	@OneToOne(mappedBy="tarjetaCredito")
	private Cuenta cuenta;
}
