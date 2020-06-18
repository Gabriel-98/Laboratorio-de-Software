package com.planificador.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.planificador.dto.TransaccionDTO;
import com.planificador.service.TransaccionService;

@CrossOrigin("*")
@RestController
@RequestMapping("transacciones")
public class TransaccionController {

	@Autowired
	TransaccionService transaccionService;
	
	@PostMapping("/crear")
	public TransaccionDTO crear(@RequestBody TransaccionDTO transaccionDTO) {
		return transaccionService.crear(transaccionDTO);
	}
	
	@DeleteMapping("/eliminar/{id}")
	public Boolean eliminar(@PathVariable("id") Integer id) {
		return transaccionService.eliminar(id);
	}
	
	@GetMapping("/listar/{email}/filtro/{filtro}")
	public List<TransaccionDTO> listar(@PathVariable("email") String email, @PathVariable("filtro") String filtro){
		return transaccionService.listar(email, filtro);
	}
}
