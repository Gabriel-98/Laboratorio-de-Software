package com.planificador.entity;

import javax.persistence.CascadeType;
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
@Table(name="transacciones")
public class Transaccion {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id", nullable=false, updatable=false)
	private Integer id;
	
	@Column(name="tipo", nullable=false, updatable=false)
	private String tipo;
	
	@ManyToOne
	@JoinColumn(name="id_usuario", referencedColumnName="email", nullable=false, updatable=false)
	private Usuario usuario;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="id_ajuste", referencedColumnName="id", nullable=true, updatable=false)
	private Ajuste ajuste;

	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="id_transferencia", referencedColumnName="id", nullable=true, updatable=false)
	private Transferencia transferencia;
}
