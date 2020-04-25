package com.planificador.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
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
	@Column(name="id", nullable=false, updatable=false)
	private Integer id;
	
	@Column(name="nombre", nullable=false)
	private String nombre;
	
	@Column(name="tipo", nullable=false)
	private String tipo;
	
	@Column(name="codigo_icono", nullable=true)
	private String codigoIcono;
	
	@ManyToOne
	@JoinColumn(name="usuario", referencedColumnName="email", nullable=false)
	private Usuario usuario;
}
