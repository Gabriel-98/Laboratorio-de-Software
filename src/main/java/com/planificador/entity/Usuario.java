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
	private boolean habilitarSeparadorAutomatico;
	
	@Column(name="habilitar_saldo_ejecucion", nullable=false)
	private boolean habilitarSaldoEjecucion;
	
	@Column(name="clave", nullable=true)
	public String clave;
	
	@Column(name="conectado", nullable=false)
	private boolean conectado;

	@OneToMany(cascade=CascadeType.ALL, mappedBy="usuario")
	private List<Categoria> categorias;
}
