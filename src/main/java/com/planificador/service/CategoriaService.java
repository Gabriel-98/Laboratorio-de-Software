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
	
	public CategoriaDTO getCategoriaDTO(Integer id) {
		Optional<Categoria> optionalCategoria = categoriaRepository.findById(id);
		if(optionalCategoria.isEmpty())
		return null;
		return modelMapper.map(optionalCategoria.get(), CategoriaDTO.class);
	}
	
	
	public CategoriaDTO crear(CategoriaDTO categoriaDTO) {
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
		if(optionalUsuario.isEmpty())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe un usuario con ese email");
		
		Usuario usuario = optionalUsuario.get();
		//if(!usuario.getConectado())
		//throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario no ha iniciado sesion");
			
		Categoria categoria = categoriaRepository.findByUsuarioAndNombre(usuario, nombre);
		if(categoria != null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La categoria ya existe");
		
		try{
			categoria = modelMapper.map(categoriaDTO, Categoria.class);
			Categoria categoriaRespuesta = categoriaRepository.save(categoria);
			CategoriaDTO categoriaRespuestaDTO = modelMapper.map(categoriaRespuesta, CategoriaDTO.class);
			return categoriaRespuestaDTO;
		}
		catch(Exception e){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No se pudo guardar la categoria."); }
	}
	
	public CategoriaDTO editar(CategoriaDTO categoriaDTO) {
		if(categoriaDTO == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La categoria es nula");
		
		if(categoriaDTO.getId() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El identificador es nulo");
		if(categoriaDTO.getNombre() == null || categoriaDTO.getTipo() == null || categoriaDTO.getEmailUsuario() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Algunos datos no pueden ser nulos");		
		
		Optional<Categoria> optionalCategoria = categoriaRepository.findById(categoriaDTO.getId());
		if(optionalCategoria.isEmpty())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! No existe una categoria con ese identificador");
	
		Categoria categoriaPasado = optionalCategoria.get();
		CategoriaDTO categoriaPasadoDTO = modelMapper.map(categoriaPasado, CategoriaDTO.class);
		
		if(!categoriaDTO.getTipo().equals(categoriaPasadoDTO.getTipo()))
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El tipo o usuario de la categoria no debe cambiar");
		if(!categoriaDTO.getEmailUsuario().equals(categoriaPasadoDTO.getEmailUsuario()))
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El tipo o usuario de la categoria no debe cambiar");
		
		Categoria categoriaNueva = modelMapper.map(categoriaDTO, Categoria.class);
		Categoria categoriaRespuesta = categoriaRepository.save(categoriaNueva);
		CategoriaDTO categoriaRespuestaDTO = modelMapper.map(categoriaRespuesta, CategoriaDTO.class);
		return categoriaRespuestaDTO;
	}
	
	public boolean eliminar(Integer id){
		if(id == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El identificador es nulo");
		
		Optional<Categoria> optionalCategoria = categoriaRepository.findById(id);
		if(optionalCategoria.isEmpty())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! La categoria no existe");
	
		categoriaRepository.delete(optionalCategoria.get());
		return true;
	}
	
	public List<CategoriaDTO> listar(String email){
		if(email == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El email es nulo");
		
		Optional<Usuario> optionalUsuario = usuarioRepository.findById(email);
		if(optionalUsuario.isEmpty())
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! El usuario no existe");
		
		Usuario usuario = optionalUsuario.get();
		
		List<Categoria> categorias = categoriaRepository.findAllByUsuario(usuario);
		List<CategoriaDTO> categoriasDTO = new LinkedList<CategoriaDTO>();
		ListIterator<Categoria> iteratorCategorias = categorias.listIterator();
		while(iteratorCategorias.hasNext())
		categoriasDTO.add(modelMapper.map(iteratorCategorias.next(), CategoriaDTO.class));
		return categoriasDTO;
	}
}
