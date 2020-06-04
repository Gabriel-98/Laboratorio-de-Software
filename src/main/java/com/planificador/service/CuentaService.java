package com.planificador.service;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.planificador.dto.CuentaDTO;
import com.planificador.dto.TarjetaCreditoDTO;
import com.planificador.entity.Cuenta;
import com.planificador.entity.Grupo;
import com.planificador.entity.Usuario;
import com.planificador.repository.CuentaRepository;
import com.planificador.repository.GrupoRepository;
import com.planificador.repository.UsuarioRepository;

@Service
public class CuentaService {
	
	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	GrupoRepository grupoRepository;
	
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
		
		Grupo grupo = null;
		if(cuentaDTO.getIdGrupo() != null){
			Optional<Grupo> optionalGrupo = grupoRepository.findById(cuentaDTO.getIdGrupo());
			if(!optionalGrupo.isPresent())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El grupo ingresado no existe");
			grupo = optionalGrupo.get();
			if(!cuentaDTO.getEmailUsuario().equals(grupo.getUsuario().getEmail()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ese grupo no pertenece al mismo usuario");
		}
		
		cuentaDTO.setSaldo(cuentaDTO.getSaldoInicial());
		
		// No se tiene en cuenta la divisa todavia
		if(grupo != null)
		grupo.setSaldo(grupo.getSaldo() + cuentaDTO.getSaldo());
		
		String tipo = cuentaDTO.getTipo();
		if(tipo.equals("CHEQUES") || tipo.equals("AHORROS") || tipo.equals("EFECTIVO")){
			Cuenta cuenta = modelMapper.map(cuentaDTO, Cuenta.class);
			Cuenta cuentaRespuesta = cuentaRepository.save(cuenta);
			CuentaDTO cuentaRespuestaDTO = modelMapper.map(cuentaRespuesta, CuentaDTO.class);
			return cuentaRespuestaDTO;
		}
		else if(tipo.equals("TARJETA-CREDITO")){
			TarjetaCreditoDTO tarjetaCreditoDTO = (TarjetaCreditoDTO)cuentaDTO;
			if(tarjetaCreditoDTO.getLimiteCredito() == null)
			tarjetaCreditoDTO.setLimiteCredito(0.00);
			
			Cuenta cuenta = modelMapper.map(tarjetaCreditoDTO, Cuenta.class);
			Cuenta cuentaRespuesta = cuentaRepository.save(cuenta);
			TarjetaCreditoDTO tarjetaCreditoRespuestaDTO = modelMapper.map(cuentaRespuesta, TarjetaCreditoDTO.class);
			return tarjetaCreditoRespuestaDTO;
		}
		else
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Tipo de cuenta no permitido");
	}
	
	public List<CuentaDTO> listar(String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El email es nulo");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
		
		Usuario usuario = optionalUsuario.get();
		
		List<Cuenta> cuentas = cuentaRepository.findAllByUsuarioOrderByGrupoAsc(usuario);
		ListIterator<Cuenta> iterator = cuentas.listIterator();
		List<CuentaDTO> cuentasDTO = new LinkedList<CuentaDTO>();
		while(iterator.hasNext()){
			Cuenta cuenta = iterator.next();
			CuentaDTO cuentaDTO;
			if(cuenta.getTipo().equals("TARJETA-CREDITO"))
			cuentaDTO = modelMapper.map(cuenta, TarjetaCreditoDTO.class);
			else
			cuentaDTO = modelMapper.map(cuenta, CuentaDTO.class);
			
			if(cuentaDTO.getIdGrupo() == null)
			cuentasDTO.add(cuentaDTO);
		}
		
		iterator = cuentas.listIterator();
		while(iterator.hasNext()){
			Cuenta cuenta = iterator.next();
			CuentaDTO cuentaDTO;
			if(cuenta.getTipo().equals("TARJETA-CREDITO"))
			cuentaDTO = modelMapper.map(cuenta, TarjetaCreditoDTO.class);
			else
			cuentaDTO = modelMapper.map(cuenta, CuentaDTO.class);
			
			if(cuentaDTO.getIdGrupo() != null)
			cuentasDTO.add(cuentaDTO);
			else
			break;
		}
		return cuentasDTO;
 	}
}
