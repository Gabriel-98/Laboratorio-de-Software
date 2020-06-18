package com.planificador.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="ajustes")
public class Ajuste {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id", nullable=false, updatable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="id_cuenta", referencedColumnName="id", nullable=false, updatable=false)
	private Cuenta cuenta;
	
	@Column(name="saldo_adicional", nullable=false, updatable=false)
	private Double saldoAdicional;
	
	@OneToOne(mappedBy="ajuste")
	private Transaccion transaccion;
}
