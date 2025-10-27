package com.generation.farmacia.repository;

import java.util.List; // Importa a interface List para retorno de listas de produtos

//Importa a interface JpaRepository do Spring Data JPA
import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.farmacia.model.Produto;

//Interface que estende JpaRepository para operações CRUD automáticas em Produtos
//JpaRepository<Produto, Long> → Produto é a entidade e Long é o tipo do ID
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

 // Busca todos os produtos cujo título contenha a string informada, ignorando maiúsculas/minúsculas
 // Equivalente a: SELECT * FROM tb_produto WHERE titulo LIKE '%?%';
 public List<Produto> findAllByDescricaoContainingIgnoreCase(String descricao);

}

