package com.planificador.service;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.planificador.dto.EtiquetaDTO;
import com.planificador.entity.Etiqueta;
import com.planificador.entity.Usuario;
import com.planificador.repository.EtiquetaRepository;
import com.planificador.repository.UsuarioRepository;

@Service
public class EtiquetaService {
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	EtiquetaRepository etiquetaRepository;
	
	@Autowired
	ModelMapper modelMapper;
	
	public EtiquetaDTO crear(EtiquetaDTO etiquetaDTO){
		if(etiquetaDTO == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La etiqueta es nula");
		if(etiquetaDTO.getId() != null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No debe ingresar un identificador");
		if(etiquetaDTO.getNombre() == null || etiquetaDTO.getEmailUsuario() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Algunos campos no pueden ser nulos");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(etiquetaDTO.getEmailUsuario());
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");

		Usuario usuario = optionalUsuario.get();	
		if(etiquetaRepository.existsByUsuarioAndNombre(usuario, etiquetaDTO.getNombre()))
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario ya tiene una etiqueta con ese nombre");
		
		Etiqueta etiqueta = modelMapper.map(etiquetaDTO, Etiqueta.class);
		Etiqueta etiquetaRespuesta = etiquetaRepository.save(etiqueta);
		EtiquetaDTO etiquetaRespuestaDTO = modelMapper.map(etiquetaRespuesta, EtiquetaDTO.class);
		return etiquetaRespuestaDTO;
	}
	
	public EtiquetaDTO editar(EtiquetaDTO etiquetaDTO){
		if(etiquetaDTO == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La etiqueta es nula");
		if(etiquetaDTO.getId() == null || etiquetaDTO.getNombre() == null || etiquetaDTO.getEmailUsuario() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Algunos campos no pueden ser nulos");
	
		Optional<Etiqueta> optionalEtiqueta = etiquetaRepository.findById(etiquetaDTO.getId());
		if(!optionalEtiqueta.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe una etiqueta con ese identificador");
		
		Etiqueta etiquetaPasado = optionalEtiqueta.get();
		EtiquetaDTO etiquetaPasadoDTO = modelMapper.map(etiquetaPasado, EtiquetaDTO.class);
		if(!etiquetaDTO.getEmailUsuario().equals(etiquetaPasadoDTO.getEmailUsuario()))
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El email no puede cambiar");

		etiquetaPasado.setNombre(etiquetaDTO.getNombre());
		
		Etiqueta etiquetaRespuesta = etiquetaRepository.save(etiquetaPasado);
		EtiquetaDTO etiquetaRespuestaDTO = modelMapper.map(etiquetaRespuesta, EtiquetaDTO.class);
		return etiquetaRespuestaDTO;	
	}
	
	public boolean eliminar(Integer id){
		if(id == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El identificador no puede ser nulo");
		
		Optional<Etiqueta> optionalEtiqueta = etiquetaRepository.findById(id);
		if(!optionalEtiqueta.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La etiqueta no existe");
		
		Etiqueta etiqueta = optionalEtiqueta.get();
		etiquetaRepository.delete(etiqueta);
		return true;
	}
	
	public List<EtiquetaDTO> listar(String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El email no puede ser nulo");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
		
		Usuario usuario = optionalUsuario.get();
		List<Etiqueta> etiquetas = etiquetaRepository.findAllByUsuarioOrderByNombreAsc(usuario);
		List<EtiquetaDTO> etiquetasDTO = new LinkedList<EtiquetaDTO>();
		ListIterator<Etiqueta> iterator = etiquetas.listIterator();
		while(iterator.hasNext())
		etiquetasDTO.add(modelMapper.map(iterator.next(), EtiquetaDTO.class));		
		return etiquetasDTO;
	}
}
