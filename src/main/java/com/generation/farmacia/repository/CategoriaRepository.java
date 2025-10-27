package com.generation.farmacia.repository;

import java.util.List; // Importa a interface List para retorno de listas de categorias

//Importa a interface JpaRepository do Spring Data JPA
import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.farmacia.model.Categoria;

//Interface que estende JpaRepository para operações CRUD automáticas em Categorias
//JpaRepository<Categoria, Long> → Categoria é a entidade e Long é o tipo do ID
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

 // Busca todas as categorias cuja descrição contenha a string informada, ignorando maiúsculas/minúsculas
 // Equivalente a: SELECT * FROM tb_Categorias WHERE descricao LIKE '%?%';
 public List<Categoria> findAllByDescricaoContainingIgnoreCase(String descricao);

}