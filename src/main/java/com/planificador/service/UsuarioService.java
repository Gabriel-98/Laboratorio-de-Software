package com.planificador.service;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
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
	UsuarioRepository usuarioRepository;
	
	@Autowired
	ModelMapper modelMapper;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	MessagesQueue messagesQueue;
	
	public UsuarioDTO registrar(UsuarioDTO usuarioDTO){
		if(usuarioDTO == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario es nulo");
		if(usuarioDTO.getEmail() == null || usuarioDTO.getNombre() == null || usuarioDTO.getPassword() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No se permite que algunos datos sean nulos");

		Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioDTO.getEmail());
		if(optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ya existe un usuario con ese email");
		Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);
		
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		if(usuarioDTO.getSaldo() == null)
		usuario.setSaldo(0.00);
		if(usuarioDTO.getDivisa() == null)
		usuario.setDivisa("USD");
		if(usuarioDTO.getHabilitarSeparadorAutomatico() == null)
		usuario.setHabilitarSeparadorAutomatico(false);
		if(usuarioDTO.getHabilitarSaldoEjecucion() == null)
		usuario.setHabilitarSaldoEjecucion(false);
		usuario.setConectado(true);
		
		Usuario usuarioRespuesta = usuarioRepository.save(usuario);		
		UsuarioDTO usuarioRespuestaDTO = modelMapper.map(usuarioRespuesta, UsuarioDTO.class);
		usuarioRespuestaDTO.setPassword(null);
		return usuarioRespuestaDTO;
	}
	
	public UsuarioDTO editar(UsuarioDTO usuarioDTO){
		if(usuarioDTO == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario es nulo");
		if(usuarioDTO.getEmail() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El email es nulo");		
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioDTO.getEmail());
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
		
		Usuario usuario = optionalUsuario.get();
		if(usuarioDTO.getNombre() != null)
		usuario.setNombre(usuarioDTO.getNombre());
		if(usuarioDTO.getPassword() != null)
		usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
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
		usuarioRespuestaDTO.setPassword(null);
		return usuarioRespuestaDTO;
	}
	
	public boolean eliminar(String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El email es nulo");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario no existe");
		
		usuarioRepository.deleteById(email);
		return true;
	}
	
	public boolean solicitarCambioContraseña(String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El email es nulo");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario no existe");
		
		Usuario usuario = optionalUsuario.get();
		
		Random random = new Random();
		int size = 20;
		char[] array = new char[size];
		while(true){
			for(int i=0; i<size; i++){
				int c = random.nextInt(62);
				if(c < 10)
				array[i] = (char)('0' + c);
				else if(c < 36)
				array[i] = (char)('a' + c - 10);
				else
				array[i] = (char)('A' + c - 36);
			}
			String codigoRecuperacion = new String(array);
			usuario.setCodigoRecuperacion(codigoRecuperacion);
			
			if(usuarioRepository.countByCodigoRecuperacion(codigoRecuperacion) == 0){
				// Se agrega el mensaje a la cola
				String subject = "Solicitud de cambio de contraseña";
				String text = "Ingrese el siguiente codigo para recuperar la contraseña: " + usuario.getCodigoRecuperacion();
				messagesQueue.add(email, subject, text);
				
				usuarioRepository.save(usuario);
				return true;
			}
		}
	}
	
	public boolean validarCodigoRecuperacion(String codigoRecuperacion){
		if(codigoRecuperacion == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El codigo es nulo");

		List<Usuario> usuarios = usuarioRepository.findByCodigoRecuperacion(codigoRecuperacion);
		if(usuarios.isEmpty())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Este codigo no existe");
		if(usuarios.size() >= 2)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No es permitido cambiar la contraseña");
		return true;
	}
	
	public boolean cambiarContraseña(String codigoRecuperacion, String password){
		if(codigoRecuperacion == null || password == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Los datos no pueden ser nulos");
			
		List<Usuario> usuarios = usuarioRepository.findByCodigoRecuperacion(codigoRecuperacion);
		if(usuarios.isEmpty())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Este codigo no existe");
		if(usuarios.size() >= 2)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No es permitido cambiar la contraseña");
		Usuario usuario = usuarios.get(0);
		usuario.setPassword(passwordEncoder.encode(password));
		usuario.setCodigoRecuperacion(null);
		usuarioRepository.save(usuario);
		return true;
	}
	
	public UsuarioDTO login(String email, String password){
		if(email == null || password == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Los datos no pueden ser nulos");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
		
		Usuario usuario = optionalUsuario.get();
		if(passwordEncoder.matches(password, usuario.getPassword())) {
			usuario.setConectado(true);
			Usuario usuarioRespuesta = usuarioRepository.save(usuario);
			UsuarioDTO usuarioRespuestaDTO = modelMapper.map(usuarioRespuesta, UsuarioDTO.class);
			usuarioRespuestaDTO.setPassword(null);
			return usuarioRespuestaDTO;
		}
		else
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La contraseña es incorrecta");		
	}
	
	public boolean cerrarSesion(String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El email es nulo");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");

		Usuario usuario = optionalUsuario.get();
		if(usuario.getConectado()) {
			usuario.setConectado(false);
			usuarioRepository.save(usuario);
		}
		return true;
	}
	
	public boolean validarSesion(String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Los datos no pueden ser nulos");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
		
		Usuario usuario = optionalUsuario.get();
		if(usuario.getConectado())
		return true;
		return false;
	}
}
