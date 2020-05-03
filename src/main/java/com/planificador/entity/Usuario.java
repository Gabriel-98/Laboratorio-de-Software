package com.planificador.entity;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;

@Data
@Entity
@Table(name="usuarios")
public class Usuario {
	
	@Id
	@Column(name="email", nullable=false)
	private String email;
	
	@Column(name="nombre", nullable=false)
	private String nombre;
	
	@Column(name="password", nullable=false)
	private String password;
	
	@Column(name="saldo", nullable=false)
	private Double saldo;
	
	@Column(name="divisa", nullable=false)
	private String divisa;
	
	@Column(name="habilitar_separador_automatico", nullable=false)
	private Boolean habilitarSeparadorAutomatico;
	
	@Column(name="habilitar_saldo_ejecucion", nullable=false)
	private Boolean habilitarSaldoEjecucion;
	
	@Column(name="link", nullable=true)
	public String link;
	
	@Column(name="conectado", nullable=false)
	private Boolean conectado;

	@OneToMany(cascade=CascadeType.ALL, mappedBy="usuario")
	private List<Categoria> categorias;
}
