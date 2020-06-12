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

import com.planificador.dto.GrupoDTO;
import com.planificador.entity.Cuenta;
import com.planificador.entity.Grupo;
import com.planificador.entity.Usuario;
import com.planificador.repository.CuentaRepository;
import com.planificador.repository.GrupoRepository;
import com.planificador.repository.UsuarioRepository;
import com.utilities.CurrencyConverter;

@Service
public class GrupoService {
	
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
	
	public GrupoDTO crear(GrupoDTO grupoDTO){
		if(grupoDTO == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El grupo es nulo");
		if(grupoDTO.getId() != null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No debe ingresar un identificador");
		
		ResponseStatusException nullException = new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error! El nombre y emailUsuario no pueden ser nulos");
		if(grupoDTO.getNombre() == null)
		throw nullException;
		if(grupoDTO.getEmailUsuario() == null)
		throw nullException;
		
		if(grupoDTO.getSaldo() != null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No debe ingresar el saldo");
		if(grupoDTO.getDivisa() == null)
		grupoDTO.setDivisa("USD");
		if(!currencyConverter.isEnabled(grupoDTO.getDivisa()))
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La divisa no existe o no esta habilitada");
	
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(grupoDTO.getEmailUsuario());
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe el usuario");
		
		Usuario usuario = optionalUsuario.get();
		if(grupoRepository.existsByUsuarioAndNombre(usuario, grupoDTO.getNombre()))
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario ya tiene un grupo con ese nombre");
		
		grupoDTO.setSaldo(0.00);
		
		Grupo grupo = modelMapper.map(grupoDTO, Grupo.class);
		Grupo grupoRespuesta = grupoRepository.save(grupo);
		GrupoDTO grupoRespuestaDTO = modelMapper.map(grupoRespuesta, GrupoDTO.class);
		return grupoRespuestaDTO;
	}
	
	public GrupoDTO editar(GrupoDTO grupoDTO){
		if(grupoDTO == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El grupo es nulo");
		if(grupoDTO.getId() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El identificador es nulo");
		
		if(grupoDTO.getSaldo() != null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No debe ingresar el saldo");
		if(grupoDTO.getEmailUsuario() != null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No debe ingresar el email");
		
		if(grupoDTO.getDivisa() != null && !currencyConverter.isEnabled(grupoDTO.getDivisa()))
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La divisa no existe o no esta habilitada");
		
		Optional<Grupo> optionalGrupo = grupoRepository.findById(grupoDTO.getId());
		if(!optionalGrupo.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un grupo con ese id");
		
		Grupo grupoPasado = optionalGrupo.get();
		
		if(grupoDTO.getNombre() != null && !grupoPasado.getNombre().equals(grupoDTO.getNombre())){
			Usuario usuario = grupoPasado.getUsuario();
			if(grupoRepository.existsByUsuarioAndNombre(usuario, grupoDTO.getNombre()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario ya tiene un grupo con ese nombre");
		}
		
		// Actualizar el saldo del grupo
		if(grupoDTO.getDivisa() != null && grupoDTO.getDivisa() != grupoPasado.getDivisa()){
			grupoDTO.setSaldo(0.00);
			List<Cuenta> cuentas = grupoPasado.getCuentas();
			ListIterator<Cuenta> iterator = cuentas.listIterator();
		
			while(iterator.hasNext()){
				Cuenta cuenta = iterator.next();
				Double saldoAdicional = currencyConverter.convert(cuenta.getDivisa(), grupoDTO.getDivisa(), cuenta.getSaldo());
				if(saldoAdicional == null)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ocurrio un problema al hacer conversion de divisas de " + cuenta.getDivisa() + " a " + grupoDTO.getDivisa());
				
				grupoDTO.setSaldo(grupoDTO.getSaldo() + saldoAdicional);
			}
		}
		
		Grupo grupo = modelMapper.map(grupoDTO, Grupo.class);
		
		// Sobreescribir el grupo
		if(grupo.getNombre() != null)
		grupoPasado.setNombre(grupo.getNombre());
		if(grupo.getSaldo() != null)
		grupoPasado.setSaldo(grupo.getSaldo());
		if(grupo.getDivisa() != null)
		grupoPasado.setDivisa(grupo.getDivisa());
		
		Grupo grupoRespuesta = grupoRepository.save(grupoPasado);
		GrupoDTO grupoRespuestaDTO = modelMapper.map(grupoRespuesta, GrupoDTO.class);
		return grupoRespuestaDTO;
	}
	
	public boolean eliminar(Integer id){
		if(id == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El id es nulo");	
		
		Optional<Grupo> optionalGrupo = grupoRepository.findById(id);
		if(!optionalGrupo.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un grupo con ese id");	
		
		Grupo grupo = optionalGrupo.get();	
		List<Cuenta> cuentas = grupo.getCuentas();
		
		ListIterator<Cuenta> iterator = cuentas.listIterator();
		while(iterator.hasNext()){
			Cuenta cuenta = iterator.next();
			cuenta.setGrupo(null);
		}
		cuentaRepository.saveAll(cuentas);
		
		grupo.setCuentas(new LinkedList<Cuenta>());
		grupoRepository.save(grupo);
		grupoRepository.deleteById(id);
				
		return true;
	}
	
	public List<GrupoDTO> listar(String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El email es nulo");
	
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario no existe");
		
		Usuario usuario = optionalUsuario.get();
		List<Grupo> grupos = grupoRepository.findAllByUsuarioOrderByIdAsc(usuario);
		ListIterator<Grupo> iterator = grupos.listIterator();
		List<GrupoDTO> gruposDTO = new LinkedList<GrupoDTO>();
		
		while(iterator.hasNext())
		gruposDTO.add(modelMapper.map(iterator.next(), GrupoDTO.class));
		return gruposDTO;
	}
}
