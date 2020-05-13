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
@Table(name="categorias")
public class Categoria {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id", nullable=false, updatable=false)
	private Integer id;
	
	@Column(name="nombre", nullable=false, updatable=true)
	private String nombre;
	
	@Column(name="tipo", nullable=false, updatable=false)
	private String tipo;
	
	@Column(name="codigo_icono", nullable=true, updatable=true)
	private String codigoIcono;
	
	@ManyToOne
	@JoinColumn(name="usuario", referencedColumnName="email", nullable=false, updatable=false)
	private Usuario usuario;
}
