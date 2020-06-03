package com.planificador.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="grupos")
public class Grupo {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id", nullable=false, updatable=false)
	private Integer id;
	
	@Column(name="nombre", nullable=false, updatable=true)
	private String nombre;
	
	@Column(name="saldo", nullable=false, updatable=true)
	private Double saldo;
	
	@Column(name="divisa", nullable=false, updatable=true)
	private String divisa;
	
	@ManyToOne
	@JoinColumn(name="email_usuario", referencedColumnName="email", nullable=false, updatable=false)
	private Usuario usuario;
}
