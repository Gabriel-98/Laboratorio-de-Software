package com.planificador.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.planificador.dto.UsuarioDTO;
import com.planificador.service.UsuarioService;

//@CrossOrigin(origins="http://localhost:4200", maxAge=3600)
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	@Autowired
	UsuarioService usuarioService;
	
	@PostMapping("/registrar")
	public UsuarioDTO registrar(@RequestBody UsuarioDTO usuarioDTO){
		UsuarioDTO ans =  usuarioService.registrar(usuarioDTO);
		return ans;
	}
	
	@PutMapping("/editar")
	public UsuarioDTO editar(@RequestBody UsuarioDTO usuarioDTO){
		return usuarioService.editar(usuarioDTO);
	}
	
	@DeleteMapping("/eliminar/{email}")
	public boolean eliminar(@PathVariable("email") String email){
		return usuarioService.eliminar(email);
	}
	
	@PutMapping("/solicitar-cambio-contraseña/{email}")
	public boolean solicitarCambioContraseña(@PathVariable("email") String email){
		return usuarioService.solicitarCambioContraseña(email);
	}
	
	@GetMapping("/validar-clave-enlace/{clave-enlace}")
	public boolean validarClaveEnlace(@PathVariable("clave-enlace") String claveEnlace){
		return usuarioService.validarClaveEnlace(claveEnlace);
	}
	
	@PutMapping("/cambiar-contraseña/{clave-enlace}/{password}")
	public boolean cambiarContraseña(@PathVariable("clave-enlace") String claveEnlace, @PathVariable("password") String password){
		return usuarioService.cambiarContraseña(claveEnlace, password);
	}
	
	@PutMapping("/login/{email}/{password}")
	public UsuarioDTO login(@PathVariable("email") String email, @PathVariable("password") String password){
		return usuarioService.login(email, password);
	}
	
	@PutMapping("/cerrar-sesion/{email}")
	public boolean cerrarSesion(@PathVariable("email") String email){
		return usuarioService.cerrarSesion(email);
	}
	
	@GetMapping("/validar-sesion/{email}")
	public boolean validarSesion(@PathVariable("email") String email) {
		return usuarioService.validarSesion(email);
	}
}
