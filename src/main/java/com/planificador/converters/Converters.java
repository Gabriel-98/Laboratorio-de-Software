package com.planificador.converters;

import com.planificador.dto.AjusteDTO;
import com.planificador.dto.TransferenciaDTO;
import com.planificador.entity.Transaccion;

public class Converters {
	
	public static AjusteDTO transaccionEntityToAjusteDTO(Transaccion transaccion){
		AjusteDTO ajusteDTO = new AjusteDTO();
		ajusteDTO.setId(transaccion.getId());
		ajusteDTO.setTipo("AJUSTE");
		ajusteDTO.setIdCuenta(transaccion.getAjuste().getCuenta().getId());
		ajusteDTO.setSaldoAdicional(transaccion.getAjuste().getSaldoAdicional());
		return ajusteDTO;
	}
	
	public static TransferenciaDTO transaccionEntityToTransferenciaDTO(Transaccion transaccion){
		TransferenciaDTO transferenciaDTO = new TransferenciaDTO();
		transferenciaDTO.setId(transaccion.getId());
		transferenciaDTO.setTipo("TRANSFERENCIA");
		transferenciaDTO.setIdCuentaOrigen(transaccion.getTransferencia().getCuentaOrigen().getId());
		transferenciaDTO.setIdCuentaDestino(transaccion.getTransferencia().getCuentaDestino().getId());
		transferenciaDTO.setCantidadEnviada(transaccion.getTransferencia().getCantidadEnviada());
		transferenciaDTO.setCantidadRecibida(transaccion.getTransferencia().getCantidadRecibida());
		return transferenciaDTO;
	}
}
