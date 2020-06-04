package com.planificador.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.planificador.dto.CuentaDTO;
import com.planificador.service.CuentaService;

@RestController
@RequestMapping("cuentas")
public class CuentaController {

	@Autowired
	CuentaService cuentaService;
	
	@PostMapping("/crear")
	public CuentaDTO crear(@RequestBody CuentaDTO cuentaDTO){
		return cuentaService.crear(cuentaDTO);
	}
	
	@GetMapping("/listar/{email}")
	public List<CuentaDTO> listar(@PathVariable("email") String email){
		return cuentaService.listar(email);
	}
}
