package com.planificador.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.planificador.dto.CategoriaDTO;
import com.planificador.service.CategoriaService;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

	@Autowired
	CategoriaService categoriaService;
	
	@PostMapping("/crear")
	public CategoriaDTO crear(@RequestBody CategoriaDTO categoriaDTO){
		return categoriaService.crear(categoriaDTO);
	}
	
	@PutMapping("/editar")
	public CategoriaDTO editar(@RequestBody CategoriaDTO categoriaDTO) {
		return categoriaService.editar(categoriaDTO);
	}
	
	@DeleteMapping("/eliminar/{id}")
	public Boolean eliminar(@PathVariable("id") Integer id){
		return categoriaService.eliminar(id);
	}
	
	@GetMapping("/listar/{email}")
	public List<CategoriaDTO> listar(@PathVariable String email){
		return categoriaService.listar(email);
	}
}
