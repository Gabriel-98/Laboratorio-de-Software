package com.planificador.service;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.modelmapper.ModelMapper;

import com.planificador.entity.Usuario;
import com.planificador.dto.UsuarioDTO;
import com.planificador.repository.UsuarioRepository;
import com.utilities.MessagesQueue;
import com.utilities.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UsuarioService {
	
	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	MessagesQueue messagesQueue;
	
	public UsuarioDTO registrar(UsuarioDTO usuarioDTO){	
		String email = usuarioDTO.getEmail();
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un usuario con ese email");
		Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);
		
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuario.setSaldo(0.00);
		usuario.setDivisa("USD");
		usuario.setHabilitarSeparadorAutomatico(false);
		usuario.setHabilitarSaldoEjecucion(false);
		usuario.setConectado(true);
		
		Usuario usuarioRespuesta;
		try{ usuarioRespuesta = usuarioRepository.save(usuario); }
		catch(DataIntegrityViolationException e){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error con los datos ingresados"); 
		}
		
		UsuarioDTO usuarioRespuestaDTO = modelMapper.map(usuarioRespuesta, UsuarioDTO.class);
		return usuarioRespuestaDTO;
	}
	
	public UsuarioDTO editar(UsuarioDTO usuarioDTO) {
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioDTO.getEmail());
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
		
		Usuario usuario = optionalUsuario.get();
		if(usuarioDTO.getNombre() != null)
		usuario.setNombre(usuarioDTO.getNombre());
		if(usuarioDTO.getPassword() != null)
		usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword())) ;
		if(usuarioDTO.getSaldo() != null)
		usuario.setSaldo(usuarioDTO.getSaldo());
		if(usuarioDTO.getDivisa() != null)
		usuario.setDivisa(usuarioDTO.getDivisa());
		if(usuarioDTO.getHabilitarSeparadorAutomatico() != null)
		usuario.setHabilitarSeparadorAutomatico(usuarioDTO.getHabilitarSeparadorAutomatico());
		if(usuarioDTO.getHabilitarSaldoEjecucion() != null)
		usuario.setHabilitarSaldoEjecucion(usuarioDTO.getHabilitarSaldoEjecucion());

		Usuario usuarioRespuesta = usuarioRepository.save(usuario);
		UsuarioDTO usuarioRespuestaDTO = modelMapper.map(usuarioRespuesta, UsuarioDTO.class);
		return usuarioRespuestaDTO;
	}
	
	public boolean eliminar(String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error el email es null");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(optionalUsuario.isPresent()) {
			usuarioRepository.deleteById(email);
			return true;
		}
		return false;
	}
	
	public boolean solicitarCambioContraseña(String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El email es null");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(optionalUsuario.isPresent()) {
			Usuario usuario = optionalUsuario.get();
		
			Random random = new Random();
			int size = 20;
			char[] array = new char[size];
			while(true){
				for(int i=0; i<size; i++) {
					int c = random.nextInt(62);
					if(c < 10)
					array[i] = (char)('0' + c);
					else if(c < 36)
					array[i] = (char)('a' + c - 10);
					else
					array[i] = (char)('A' + c - 36);
				}
				String claveEnlace = new String(array);
				
				if(usuarioRepository.countByLink(claveEnlace) == 0) {
					/*
			 		.... Ingresar el mensaje a la cola
					*/
					String subject = "Solicitud de cambio de contraseña";
					String text = "Ingrese al siguiente link " + usuario.getLink();
					messagesQueue.add(email, subject, text);
					//***
					usuario.setLink(claveEnlace);
					usuarioRepository.save(usuario);
					return true;
				}
			}	
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
	}
	
	public boolean cambiarContraseña(String claveEnlace, String password){
		if(claveEnlace == null || password == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Los datos no pueden ser nulos");
			
		List<Usuario> usuarios = usuarioRepository.findByLink(claveEnlace);
		if(usuarios.isEmpty())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Este enlace no existe");
		if(usuarios.size() >= 2)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No es permitido cambiar la contraseña");
		Usuario usuario = usuarios.get(0);
		usuario.setPassword(passwordEncoder.encode(password));
		usuario.setLink(null);
		usuarioRepository.save(usuario);
		return true;
	}
	
	public UsuarioDTO login(String email, String password){
		if(email == null || password == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Los datos no pueden ser nulos");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(optionalUsuario.isPresent()) {
			Usuario usuario = optionalUsuario.get();
			if(passwordEncoder.matches(password, usuario.getPassword())) {
				usuario.setConectado(true);
				Usuario usuarioRespuesta = usuarioRepository.save(usuario);
				UsuarioDTO usuarioRespuestaDTO = modelMapper.map(usuarioRespuesta, UsuarioDTO.class);
				return usuarioRespuestaDTO;
			}
			else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La contraseña es incorrecta");
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
	}
	
	public boolean cerrarSesion(String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Los datos no pueden ser nulos");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(optionalUsuario.isPresent()){
			Usuario usuario = optionalUsuario.get();
			if(usuario.getConectado()) {
				usuario.setConectado(false);
				usuarioRepository.save(usuario);
			}
			return true;
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
	}
	
	public boolean validarSesion(String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Los datos no pueden ser nulos");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(optionalUsuario.isPresent()) {
			Usuario usuario = optionalUsuario.get();
			if(usuario.getConectado())
			return true;
			return false;
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
	}
}
