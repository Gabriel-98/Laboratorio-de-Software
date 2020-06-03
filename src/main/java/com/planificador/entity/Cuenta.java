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
@Table(name="cuentas")
public class Cuenta {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id", nullable=false, updatable=false)
	private Integer id;
	
	@Column(name="nombre", nullable=false, updatable=true)
	private String nombre;
	
	@Column(name="saldo_inicial", nullable=false, updatable=false)
	private Double saldoInicial;
	
	@Column(name="saldo", nullable=false, updatable=true)
	private Double saldo;
	
	@Column(name="divisa", nullable=false, updatable=false)
	private String divisa;
	
	@Column(name="descripcion", nullable=false, updatable=true)
	private String descripcion;
	
	@Column(name="habilitar_cheques", nullable=false, updatable=true)
	private boolean habilitarCheques;
	
	@Column(name="adicionar_patrimonio_neto", nullable=false, updatable=true)
	private boolean adicionarPatrimonioNeto;
	
	@Column(name="archivada", nullable=false, updatable=false)
	private boolean archivada;
	
	@Column(name="tipo", nullable=false, updatable=false)
	private String tipo;
	
	//private Grupo grupo;
	
	@ManyToOne
	@JoinColumn(name="email_usuario", referencedColumnName="email", nullable=false, updatable=false)
	private Usuario usuario;
}