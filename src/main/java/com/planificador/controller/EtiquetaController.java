package com.planificador.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.planificador.dto.EtiquetaDTO;
import com.planificador.service.EtiquetaService;

@RestController
@RequestMapping("/etiquetas")
public class EtiquetaController {
	
	@Autowired
	EtiquetaService etiquetaService;
	
	@PostMapping("/crear")
	public EtiquetaDTO crear(@RequestBody EtiquetaDTO etiquetaDTO) {
		return etiquetaService.crear(etiquetaDTO);
	}
	
	@PutMapping("/editar")
	public EtiquetaDTO editar(@RequestBody EtiquetaDTO etiquetaDTO){
		return etiquetaService.editar(etiquetaDTO);
	}
	
	@DeleteMapping("/eliminar/{id}")
	public boolean eliminar(@PathVariable("id") Integer id){
		return etiquetaService.eliminar(id);
	}
	
	@GetMapping("/listar/{email}")
	public List<EtiquetaDTO> listar(@PathVariable("email") String email){
		return etiquetaService.listar(email);
	}
}
