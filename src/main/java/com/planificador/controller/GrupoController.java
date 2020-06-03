package com.planificador.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.planificador.dto.GrupoDTO;
import com.planificador.service.GrupoService;

@RestController
@RequestMapping("grupos")
public class GrupoController {

	@Autowired
	GrupoService grupoService;
	
	@PostMapping("/crear")
	public GrupoDTO crear(@RequestBody GrupoDTO grupoDTO){
		return grupoService.crear(grupoDTO);
	}
	
	/*@PutMapping("/editar")
	public GrupoDTO editar(@RequestBody GrupoDTO grupoDTO) {
		return grupoService.editar(grupoDTO);
	}*/
	
	/*@DeleteMapping("/eliminar/{id}")
	public GrupoDTO eliminar(@PathVariable Integer id) {
		return grupoService.eliminar(id);
	}*/
	
	@GetMapping("/listar/{email}")
	public List<GrupoDTO> listar(@PathVariable("email") String email) {
		return grupoService.listar(email);
	}
}
