package com.planificador.service;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.planificador.dto.CategoriaDTO;
import com.planificador.entity.Categoria;
import com.planificador.entity.Usuario;
import com.planificador.repository.CategoriaRepository;
import com.planificador.repository.UsuarioRepository;

@Service
public class CategoriaService {

	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	CategoriaRepository categoriaRepository;

	@Autowired
	ModelMapper modelMapper;
	
	public CategoriaDTO crear(CategoriaDTO categoriaDTO){
		if(categoriaDTO == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La categoria es nula");	
		if(categoriaDTO.getId() != null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No debe ingresar un identificador");

		String email = categoriaDTO.getEmailUsuario();
		String nombre = categoriaDTO.getNombre();
		String tipo = categoriaDTO.getTipo();
		if(email == null || nombre == null || tipo == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Algunos datos no pueden ser nulos");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
		
		Usuario usuario = optionalUsuario.get();
			
		if(categoriaRepository.existsByUsuarioAndNombre(usuario, nombre))
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La categoria ya existe");
		if(!(tipo.equals("INGRESO") || tipo.equals("GASTO")))
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe ese tipo de categoria");
		
		Categoria categoria = modelMapper.map(categoriaDTO, Categoria.class);
		Categoria categoriaRespuesta = categoriaRepository.save(categoria);
		CategoriaDTO categoriaRespuestaDTO = modelMapper.map(categoriaRespuesta, CategoriaDTO.class);
		return categoriaRespuestaDTO;
	}
	
	public CategoriaDTO editar(CategoriaDTO categoriaDTO) {
		if(categoriaDTO == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La categoria es nula");
		if(categoriaDTO.getId() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El identificador es nulo");
		if(categoriaDTO.getNombre() == null || categoriaDTO.getTipo() == null || categoriaDTO.getEmailUsuario() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Algunos datos no pueden ser nulos");
		
		Optional<Categoria> optionalCategoria = categoriaRepository.findById(categoriaDTO.getId());
		if(!optionalCategoria.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe una categoria con ese identificador");
	
		Categoria categoriaPasado = optionalCategoria.get();
		CategoriaDTO categoriaPasadoDTO = modelMapper.map(categoriaPasado, CategoriaDTO.class);
		
		if(!categoriaDTO.getTipo().equals(categoriaPasadoDTO.getTipo()))
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El tipo o usuario de la categoria no debe cambiar");
		if(!categoriaDTO.getEmailUsuario().equals(categoriaPasadoDTO.getEmailUsuario()))
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El tipo o usuario de la categoria no debe cambiar");
		
		if(!categoriaDTO.getNombre().equals(categoriaPasadoDTO.getNombre())){
			Usuario usuario = categoriaPasado.getUsuario();
			if(categoriaRepository.existsByUsuarioAndNombre(usuario, categoriaDTO.getNombre()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario tiene otra categoria con el nombre ingresado");
		}
		
		Categoria categoria = modelMapper.map(categoriaDTO, Categoria.class);
		categoriaPasado.setNombre(categoria.getNombre());
		categoriaPasado.setCodigoIcono(categoria.getCodigoIcono());	
		
		Categoria categoriaRespuesta = categoriaRepository.save(categoriaPasado);
		CategoriaDTO categoriaRespuestaDTO = modelMapper.map(categoriaRespuesta, CategoriaDTO.class);
		return categoriaRespuestaDTO;
	}
	
	public boolean eliminar(Integer id){
		if(id == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El identificador es nulo");
		
		Optional<Categoria> optionalCategoria = categoriaRepository.findById(id);
		if(!optionalCategoria.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La categoria no existe");
	
		categoriaRepository.delete(optionalCategoria.get());
		return true;
	}
	
	public List<CategoriaDTO> listar(String tipo, String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El email es nulo");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(!optionalUsuario.isPresent())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario no existe");
		
		Usuario usuario = optionalUsuario.get();
		
		List<Categoria> categorias;
		if(tipo.equals("*"))
		categorias = categoriaRepository.findAllByUsuarioOrderByNombreAsc(usuario);
		else if(tipo.equals("INGRESO") || tipo.equals("GASTO"))
		categorias = categoriaRepository.findAllByUsuarioAndTipoOrderByNombreAsc(usuario, tipo);
		else
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Ese tipo de categoria no existe");
		
		List<CategoriaDTO> categoriasDTO = new LinkedList<CategoriaDTO>();
		ListIterator<Categoria> iteratorCategorias = categorias.listIterator();
		while(iteratorCategorias.hasNext())
		categoriasDTO.add(modelMapper.map(iteratorCategorias.next(), CategoriaDTO.class));
		return categoriasDTO;
	}
}
