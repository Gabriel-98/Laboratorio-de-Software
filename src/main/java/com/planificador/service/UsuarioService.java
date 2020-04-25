package com.planificador.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.modelmapper.ModelMapper;

import com.planificador.entity.Usuario;
import com.planificador.dto.UsuarioDTO;
import com.planificador.repository.UsuarioRepository;

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
	
	public UsuarioDTO registrar(UsuarioDTO usuarioDTO) {	
		try {
			String email = usuarioDTO.getEmail();
			Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
			if(optionalUsuario.isPresent())
			return null;
			Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);
			
			usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
			usuario.setSaldo(0.00);
			usuario.setDivisa("USD");
			usuario.setHabilitarSeparadorAutomatico(false);
			usuario.setHabilitarSaldoEjecucion(false);
			
			Usuario usuarioRespuesta = usuarioRepository.save(usuario);
			UsuarioDTO usuarioRespuestaDTO = modelMapper.map(usuarioRespuesta, UsuarioDTO.class);
			return usuarioRespuestaDTO;
		}
		catch(Exception e) { return null; }
	}
	
	public void editar(UsuarioDTO usuarioDTO) {
		
	}
	
	public boolean eliminar(String email) {
		if(email == null)
		return false;
		usuarioRepository.deleteById(email);
		return true;
	}
	
	public boolean solicitudCambiarContraseña(UsuarioDTO usuarioDTO) {
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioDTO.getEmail());
		if(optionalUsuario.isPresent()) {
			Usuario usuario = optionalUsuario.get();
		
			Random random = new Random();
			int size = 20;
			char[] array = new char[size];
			while(true) {
				for(int i=0; i<size; i++) {
					int c = random.nextInt(62);
					if(c < 10)
					array[i] = (char)('0' + c);
					if(c < 36)
					array[i] = (char)('a' + c - 10);
					else
					array[i] = (char)('A' + c - 36);
				}
				String claveEnlace = new String(array);
				
				if(usuarioRepository.countByClave(claveEnlace) == 0) {
					usuario.setClave(claveEnlace);
					break;
				}
			}
		}
		return true;
	}
	
	public boolean login(String email, String password) {
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		Usuario usuario;
		if(optionalUsuario.isPresent()) {
			usuario = optionalUsuario.get();
			if(passwordEncoder.matches(password, usuario.getPassword())) {
				usuario.setConectado(true);
				return true;
			}
		}
		return false;
	}
	
	public void cerrarSesion(String email) {
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		Usuario usuario;
		if(optionalUsuario.isPresent()) {
			usuario = optionalUsuario.get();
			if(usuario.isConectado())
			usuario.setConectado(false);
		}
	}
	
	public boolean validarSesion(String email) {
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		Usuario usuario;
		if(optionalUsuario.isPresent()) {
			usuario = optionalUsuario.get();
			if(usuario.isConectado())
			return true;
		}
		return false;
	}
}
