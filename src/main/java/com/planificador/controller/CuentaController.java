package com.planificador.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.planificador.dto.CuentaDTO;
import com.planificador.dto.TarjetaCreditoDTO;
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
	
	/*@PostMapping("crear/tarjeta-credito")
	public TarjetaCreditoDTO crear(@RequestBody TarjetaCreditoDTO tarjetaCreditoDTO){
		return (TarjetaCreditoDTO)(cuentaService.crear(tarjetaCreditoDTO));
	}*/
}
