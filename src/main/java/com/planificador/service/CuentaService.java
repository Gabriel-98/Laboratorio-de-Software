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
import com.utilities.CurrencyConverter;

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
	
	@Autowired
	CurrencyConverter currencyConverter;
	
	public CuentaDTO crear(CuentaDTO cuentaDTO){
		if(cuentaDTO == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La cuenta no puede ser nula");
		if(cuentaDTO.getId() != null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No debe ingresar un identificador");	
		if(cuentaDTO.getSaldo() != null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No debe ingresar el saldo");
		
		// Validar que algunos datos no sean nulos
		ResponseStatusException nullException = new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error! El nombre, tipo y emailUsuario no pueden ser nulos");
		if(cuentaDTO.getNombre() == null)
		throw nullException;
		if(cuentaDTO.getTipo() == null)
		throw nullException;
		if(cuentaDTO.getEmailUsuario() == null)
		throw nullException;
		
		// Valores por defecto
		if(cuentaDTO.getSaldoInicial() == null)
		cuentaDTO.setSaldoInicial(0.00);
		if(cuentaDTO.getDivisa() == null)
		cuentaDTO.setDivisa("USD");
		if(cuentaDTO.getDescripcion() == null)
		cuentaDTO.setDescripcion("");
		if(cuentaDTO.getHabilitarCheques() == null)
		cuentaDTO.setHabilitarCheques(true);
		if(cuentaDTO.getAdicionarPatrimonioNeto() == null)
		cuentaDTO.setAdicionarPatrimonioNeto(true);
		
		// Validar que algunos datos tengan valores correctos
		if(!currencyConverter.isEnabled(cuentaDTO.getDivisa()))
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La divisa no existe o no esta habilitada");
		
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
		if(cuentaDTO.getAdicionarPatrimonioNeto()){
			Double saldoAdicional = currencyConverter.convert(cuentaDTO.getDivisa(), grupo.getDivisa(), cuentaDTO.getSaldo());
			if(saldoAdicional == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ocurrio un problema al hacer conversion de divisas de " + cuentaDTO.getDivisa() + " a " + grupo.getDivisa());
			grupo.setSaldo(grupo.getSaldo() + saldoAdicional);
			
			saldoAdicional = currencyConverter.convert(cuentaDTO.getDivisa(), usuario.getDivisa(), cuentaDTO.getSaldo());
			if(saldoAdicional == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ocurrio un problema al hacer conversion de divisas de " + cuentaDTO.getDivisa() + " a " + usuario.getDivisa());
			usuario.setSaldo(usuario.getSaldo() + saldoAdicional);
		}
		
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
	
	public CuentaDTO editar(CuentaDTO cuentaDTO){
		if(cuentaDTO == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La cuenta no puede ser nula");
		if(cuentaDTO.getId() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El id no puede ser nulo");
		
		ResponseStatusException nonUpdatableException = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No puede modificar el saldoInicial, saldo, divisa, tipo o emailUsuario");
		if(cuentaDTO.getSaldoInicial() != null)
		throw nonUpdatableException;
		if(cuentaDTO.getSaldo() != null)
		throw nonUpdatableException;
		if(cuentaDTO.getDivisa() != null)
		throw nonUpdatableException;
		if(cuentaDTO.getTipo() != null)
		throw nonUpdatableException;
		if(cuentaDTO.getEmailUsuario() != null)
		throw nonUpdatableException;
		
		Optional<Cuenta> optionalCuenta = cuentaRepository.findById(cuentaDTO.getId());
		if(!optionalCuenta.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe una cuenta con ese id");
		
		Cuenta cuentaPasado = optionalCuenta.get();
		Usuario usuario = cuentaPasado.getUsuario();
		Grupo grupoPasado = cuentaPasado.getGrupo();
		if(cuentaDTO.getNombre() != null && !cuentaDTO.getNombre().equals(cuentaPasado.getNombre())){
			if(cuentaRepository.existsByUsuarioAndNombre(cuentaPasado.getUsuario(), cuentaDTO.getNombre()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario ya tiene una cuenta con ese nombre");
		}
		
		Grupo grupo = null;
		if(cuentaDTO.getIdGrupo() != null && !cuentaDTO.getIdGrupo().equals(grupoPasado.getId())){
			Optional<Grupo> optionalGrupo = grupoRepository.findById(cuentaDTO.getIdGrupo());
			if(!optionalGrupo.isPresent())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un grupo con ese id");
			
			grupo = optionalGrupo.get();
			if(!usuario.getEmail().equals(grupo.getUsuario().getEmail()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ese grupo no pertenece al mismo usuario");
		}
		
		if(cuentaPasado.getAdicionarPatrimonioNeto()){
			if(grupoPasado != null){
				Double saldoDescontar = currencyConverter.convert(cuentaPasado.getDivisa(), grupoPasado.getDivisa(), cuentaPasado.getSaldo());
				if(saldoDescontar == null)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ocurrio un problema al hacer conversion de divisas de " + cuentaPasado.getDivisa() + " a " + grupoPasado.getDivisa());
				grupoPasado.setSaldo(grupoPasado.getSaldo() - saldoDescontar);
			}
			
			Double saldoDescontar = currencyConverter.convert(cuentaPasado.getDivisa(), usuario.getDivisa(), cuentaPasado.getSaldo());
			if(saldoDescontar == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ocurrio un problema al hacer conversion de divisas de " + cuentaPasado.getDivisa() + " a " + usuario.getDivisa());
			usuario.setSaldo(usuario.getSaldo() - saldoDescontar);
		}
		
		if(cuentaDTO.getAdicionarPatrimonioNeto()){
			if(grupo != null) {
				Double saldoAdicional = currencyConverter.convert(cuentaPasado.getDivisa(), grupo.getDivisa(), cuentaPasado.getSaldo());
				if(saldoAdicional == null)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ocurrio un problema al hacer conversion de divisas de " + cuentaPasado.getDivisa() + " a " + grupo.getDivisa());
				grupo.setSaldo(grupo.getSaldo() + saldoAdicional);
			}
			
			Double saldoAdicional = currencyConverter.convert(cuentaPasado.getDivisa(), usuario.getDivisa(), cuentaPasado.getSaldo());
			if(saldoAdicional == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ocurrio un problema al hacer conversion de divisas de " + cuentaPasado.getDivisa() + " a " + usuario.getDivisa());
			usuario.setSaldo(usuario.getSaldo() + saldoAdicional);
		}
		
		Cuenta cuenta = modelMapper.map(cuentaDTO, Cuenta.class);
		if(cuenta.getNombre() != null)
		cuentaPasado.setNombre(cuenta.getNombre());
		if(cuenta.getDescripcion() != null)
		cuentaPasado.setDescripcion(cuenta.getDescripcion());
		if(cuenta.getHabilitarCheques() != null)
		cuentaPasado.setHabilitarCheques(cuenta.getHabilitarCheques());
		if(cuenta.getAdicionarPatrimonioNeto() != null)
		cuentaPasado.setAdicionarPatrimonioNeto(cuenta.getAdicionarPatrimonioNeto());
		if(cuenta.getGrupo() != null)
		cuentaPasado.setGrupo(cuenta.getGrupo());
		
		Cuenta cuentaRespuesta = cuentaRepository.save(cuentaPasado);
		CuentaDTO cuentaRespuestaDTO = modelMapper.map(cuentaRespuesta, CuentaDTO.class);
		return cuentaRespuestaDTO;
	}
	
	//public Boolean eliminar(Integer id){}
	
	public List<CuentaDTO> listar(String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El email es nulo");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
		
		Usuario usuario = optionalUsuario.get();
		
		List<Cuenta> cuentas = cuentaRepository.findAllByUsuarioOrderByGrupoAscNombreAsc(usuario);
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
