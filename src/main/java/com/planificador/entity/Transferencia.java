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
@Table(name="transferencias")
public class Transferencia {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id", nullable=false, updatable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="id_cuenta_origen", referencedColumnName="id", nullable=false, updatable=false)
	private Cuenta cuentaOrigen;
	
	@ManyToOne
	@JoinColumn(name="id_cuenta_destino", referencedColumnName="id", nullable=false, updatable=false)
	private Cuenta cuentaDestino;
	
	@Column(name="cantidad_enviada", nullable=false, updatable=false)
	private Double cantidadEnviada;
	
	@Column(name="cantidad_recibida", nullable=false, updatable=false)
	private Double cantidadRecibida;
	
	@OneToOne(mappedBy="transferencia")
	private Transaccion transaccion;
}
