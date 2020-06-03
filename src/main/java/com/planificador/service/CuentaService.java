package com.planificador.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.planificador.dto.CuentaDTO;
import com.planificador.dto.TarjetaCreditoDTO;
import com.planificador.entity.Cuenta;
import com.planificador.entity.Usuario;
import com.planificador.repository.CuentaRepository;
import com.planificador.repository.UsuarioRepository;

@Service
public class CuentaService {
	
	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	CuentaRepository cuentaRepository;
	
	@Autowired
	ModelMapper modelMapper;
	
	public CuentaDTO crear(CuentaDTO cuentaDTO){
		if(cuentaDTO == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La cuenta no puede ser nula");
		if(cuentaDTO.getId() != null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No debe ingresar un identificador");	
		if(cuentaDTO.getSaldo() != null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No debe ingresar el saldo");
		
		ResponseStatusException nullException = new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error! El nombre, tipo y emailUsuario no pueden ser nulos");
		if(cuentaDTO.getNombre() == null)
		throw nullException;
		if(cuentaDTO.getTipo() == null)
		throw nullException;
		if(cuentaDTO.getEmailUsuario() == null)
		throw nullException;
		
		if(cuentaDTO.getSaldoInicial() == null)
		cuentaDTO.setSaldoInicial(0.00);
		if(cuentaDTO.getDivisa() == null)
		cuentaDTO.setDivisa("USD");
		if(cuentaDTO.getDescripcion() == null)
		cuentaDTO.setDescripcion("");
		if(cuentaDTO.getHabilitarCheques() == null)
		cuentaDTO.setHabilitarCheques(false);
		if(cuentaDTO.getAdicionarPatrimonioNeto() == null)
		cuentaDTO.setAdicionarPatrimonioNeto(true);
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(cuentaDTO.getEmailUsuario());	
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe el usuario");
		
		Usuario usuario = optionalUsuario.get();
		if(cuentaRepository.existsByUsuarioAndNombre(usuario, cuentaDTO.getNombre()))
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario ya tiene una cuenta con ese nombre");
		
		cuentaDTO.setSaldo(cuentaDTO.getSaldoInicial());
		
		String tipo = cuentaDTO.getTipo();
		if(tipo.equals("CHEQUES") || tipo.equals("AHORROS") || tipo.equals("EFECTIVO")){
			Cuenta cuenta = modelMapper.map(cuentaDTO, Cuenta.class);
			Cuenta cuentaRespuesta = cuentaRepository.save(cuenta);
			CuentaDTO cuentaRespuestaDTO = modelMapper.map(cuentaRespuesta, CuentaDTO.class);
			return cuentaRespuestaDTO;
		}
		/*else if(tipo.equals("TARJETA-CREDITO")) {
			//TarjetaCreditoDTO tarjetaCreditoDTO = (TarjetaCreditoDTO)cuentaDTO;
		}*/
		else
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Tipo de cuenta no permitido");
	}
}
