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
	@Column(name="email", nullable=false, updatable=false)
	private String email;
	
	@Column(name="nombre", nullable=false, updatable=true)
	private String nombre;
	
	@Column(name="password", nullable=false, updatable=true)
	private String password;
	
	@Column(name="saldo", nullable=false, updatable=true)
	private Double saldo;
	
	@Column(name="divisa", nullable=false, updatable=true)
	private String divisa;
	
	@Column(name="habilitar_separador_automatico", nullable=false, updatable=true)
	private Boolean habilitarSeparadorAutomatico;
	
	@Column(name="habilitar_saldo_ejecucion", nullable=false, updatable=true)
	private Boolean habilitarSaldoEjecucion;
	
	@Column(name="codigo_recuperacion", nullable=true, updatable=true)
	public String codigoRecuperacion;
	
	@Column(name="conectado", nullable=false, updatable=true)
	private Boolean conectado;

	@OneToMany(cascade=CascadeType.ALL, mappedBy="usuario")
	private List<Categoria> categorias;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="usuario")
	private List<Etiqueta> etiquetas;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="usuario")
	private List<Transaccion> transacciones;
}
