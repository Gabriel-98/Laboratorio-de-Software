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

import com.planificador.converters.Converters;
import com.planificador.dto.AjusteDTO;
import com.planificador.dto.CuentaDTO;
import com.planificador.dto.TransaccionDTO;
import com.planificador.dto.TransferenciaDTO;
import com.planificador.entity.Ajuste;
import com.planificador.entity.Cuenta;
import com.planificador.entity.Grupo;
import com.planificador.entity.Transaccion;
import com.planificador.entity.Usuario;
import com.planificador.repository.CuentaRepository;
import com.planificador.repository.TransaccionRepository;
import com.planificador.repository.UsuarioRepository;
import com.utilities.CurrencyConverter;

@Service
public class TransaccionService {
	
	@Autowired
	TransaccionRepository transaccionRepository;
	
	@Autowired
	CuentaRepository cuentaRepository;
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	CurrencyConverter currencyConverter;
	
	public TransaccionDTO crear(TransaccionDTO transaccionDTO){
		if(transaccionDTO == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La transaccion es nula");
		if(transaccionDTO.getTipo() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El tipo de transaccion es nulo");
		if(transaccionDTO.getId() != null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No debe ingresar un id");
			
		if(transaccionDTO.getTipo().equals("AJUSTE")){
			AjusteDTO ajusteDTO = (AjusteDTO)transaccionDTO;
			if(ajusteDTO.getIdCuenta() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El id de la cuenta es nulo");
			if(ajusteDTO.getSaldoAdicional() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El saldo adicional es nulo");
			
			Optional<Cuenta> optionalCuenta = cuentaRepository.findById(ajusteDTO.getIdCuenta());
			if(!optionalCuenta.isPresent())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe una cuenta con ese id");
			
			Cuenta cuenta = optionalCuenta.get();
			Usuario usuario = cuenta.getUsuario();
			Grupo grupo = cuenta.getGrupo();
			cuenta.setSaldo(cuenta.getSaldo() + ajusteDTO.getSaldoAdicional());
			if(cuenta.getAdicionarPatrimonioNeto()){
				if(grupo != null){
					try{
						double saldoAdicionalGrupo = currencyConverter.convert(cuenta.getDivisa(), grupo.getDivisa(), ajusteDTO.getSaldoAdicional());
						grupo.setSaldo(grupo.getSaldo() + saldoAdicionalGrupo);
					}catch(Exception e){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ocurrio un problema al hacer conversion de divisas de " + cuenta.getDivisa() + " a " + grupo.getDivisa()); }
				}
				
				try{
					double saldoAdicionalUsuario = currencyConverter.convert(cuenta.getDivisa(), usuario.getDivisa(), ajusteDTO.getSaldoAdicional());
					usuario.setSaldo(usuario.getSaldo() + saldoAdicionalUsuario);
				}catch(Exception e){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ocurrio un problema al hacer conversion de divisas de " + cuenta.getDivisa() + " a " + usuario.getDivisa()); }
			}
			Transaccion transaccion = modelMapper.map(ajusteDTO, Transaccion.class);
			transaccion.setUsuario(usuario);
			Transaccion transaccionRespuesta = transaccionRepository.save(transaccion);
			AjusteDTO ajusteRespuestaDTO = Converters.transaccionEntityToAjusteDTO(transaccionRespuesta);
			return ajusteRespuestaDTO;
		}
		else if(transaccionDTO.getTipo().equals("TRANSFERENCIA")){
			TransferenciaDTO transferenciaDTO = (TransferenciaDTO)transaccionDTO;
			if(transferenciaDTO.getIdCuentaOrigen() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El id de la cuenta de origen es nulo");
			if(transferenciaDTO.getIdCuentaDestino() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El id de la cuenta de destino es nulo");
			if(transferenciaDTO.getCantidadEnviada() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La cantidad denviada es nulo");
			if(transferenciaDTO.getCantidadRecibida() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La cantidad recibida es nulo");
			
			Optional<Cuenta> optionalCuentaOrigen = cuentaRepository.findById(transferenciaDTO.getIdCuentaOrigen());
			if(!optionalCuentaOrigen.isPresent())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe la cuenta de origen");
			Optional<Cuenta> optionalCuentaDestino = cuentaRepository.findById(transferenciaDTO.getIdCuentaDestino());
			if(!optionalCuentaDestino.isPresent())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe la cuenta de destino");
			
			Cuenta cuentaOrigen = optionalCuentaOrigen.get();
			Cuenta cuentaDestino = optionalCuentaDestino.get();
			
			if(!cuentaOrigen.getUsuario().equals(cuentaDestino.getUsuario()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Las cuentas no pertenecen al mismo usuario");
			
			Usuario usuario = cuentaOrigen.getUsuario();
			Grupo grupoOrigen = cuentaOrigen.getGrupo();
			Grupo grupoDestino = cuentaDestino.getGrupo();
			cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - transferenciaDTO.getCantidadEnviada());
			cuentaDestino.setSaldo(cuentaDestino.getSaldo() + transferenciaDTO.getCantidadRecibida());
			if(cuentaOrigen.getAdicionarPatrimonioNeto()){
				if(grupoOrigen != null){
					try{
						double saldoAdicionalGrupo = currencyConverter.convert(cuentaOrigen.getDivisa(), grupoOrigen.getDivisa(), transferenciaDTO.getCantidadEnviada());
						grupoOrigen.setSaldo(grupoOrigen.getSaldo() - saldoAdicionalGrupo);
					}catch(Exception e){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ocurrio un problema al hacer conversion de divisas de " + cuentaOrigen.getDivisa() + " a " + grupoOrigen.getDivisa()); }
				}
				
				try{
					double saldoAdicionalUsuario = currencyConverter.convert(cuentaOrigen.getDivisa(), usuario.getDivisa(), transferenciaDTO.getCantidadEnviada());
					usuario.setSaldo(usuario.getSaldo() - saldoAdicionalUsuario);
				}catch(Exception e){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ocurrio un problema al hacer conversion de divisas de " + cuentaOrigen.getDivisa() + " a " + usuario.getDivisa()); }
			}
			
			if(cuentaDestino.getAdicionarPatrimonioNeto()){
				if(grupoDestino != null){
					try{
						double saldoAdicionalGrupo = currencyConverter.convert(cuentaDestino.getDivisa(), grupoDestino.getDivisa(), transferenciaDTO.getCantidadRecibida());
						grupoDestino.setSaldo(grupoDestino.getSaldo() + saldoAdicionalGrupo);
					}catch(Exception e){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ocurrio un problema al hacer conversion de divisas de " + cuentaDestino.getDivisa() + " a " + grupoDestino.getDivisa()); }
				}
				
				try{
					double saldoAdicionalUsuario = currencyConverter.convert(cuentaDestino.getDivisa(), usuario.getDivisa(), transferenciaDTO.getCantidadRecibida());
					usuario.setSaldo(usuario.getSaldo() + saldoAdicionalUsuario);
				}catch(Exception e){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ocurrio un problema al hacer conversion de divisas de " + cuentaDestino.getDivisa() + " a " + usuario.getDivisa()); }
			}
			Transaccion transaccion = modelMapper.map(transferenciaDTO, Transaccion.class);
			transaccion.setUsuario(usuario);
			Transaccion transaccionRespuesta = transaccionRepository.save(transaccion);
			TransferenciaDTO transferenciaRespuestaDTO = Converters.transaccionEntityToTransferenciaDTO(transaccionRespuesta);
			return transferenciaRespuestaDTO;
		}
		else
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe ese tipo de transaccion");
	}
	
	public Boolean eliminar(Integer id){
		/*if(id == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El id es nulo");
		
		Optional<Transaccion> optionalTransaccion = transaccionRepository.findById(id);
		if(!optionalTransaccion.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe una transaccion con ese id");
		
		Transaccion transaccion = optionalTransaccion.get();
		if(transaccion.getTipo().equals("AJUSTE")){
			Ajuste ajuste = transaccion.getAjuste();
			Cuenta cuenta = ajuste.getCuenta();
			Usuario usuario = cuenta.getUsuario();
			Grupo grupo = cuenta.getGrupo();
			
			if(cuenta.getAdicionarPatrimonioNeto()) {
				
			}
			transaccionRepository.delete(transaccion);
		}		
		else if(transaccion.getTipo().equals("TRANSFERENCIA")) {
			
		}*/
		
		return true;
	}
	
	public List<TransaccionDTO> listar(String email, String filtro){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El email es nulo");
		if(filtro == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El filtro es nulo");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
		Usuario usuario = optionalUsuario.get();
		
		List<Transaccion> transacciones;
		List<TransaccionDTO> transaccionesDTO = new LinkedList<TransaccionDTO>();
		if(filtro.equals("*"))
		transacciones = transaccionRepository.findAllByUsuarioOrderById(usuario);
		else if(filtro.equals("AJUSTE") || filtro.equals("TRANSFERENCIA"))
		transacciones = transaccionRepository.findAllByUsuarioAndTipoOrderById(usuario, filtro);
		else
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El filtro no existe");
		
		ListIterator<Transaccion> iterator = transacciones.listIterator();
		while(iterator.hasNext()){
			Transaccion transaccion = iterator.next();
			if(transaccion.getTipo().equals("AJUSTE"))
			transaccionesDTO.add(Converters.transaccionEntityToAjusteDTO(transaccion));
			else if(transaccion.getTipo().equals("TRANSFERENCIA"))
			transaccionesDTO.add(Converters.transaccionEntityToTransferenciaDTO(transaccion));
		}
		return transaccionesDTO;
	}
}
