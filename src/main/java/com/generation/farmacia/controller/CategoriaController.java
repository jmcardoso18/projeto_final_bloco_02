package com.generation.farmacia.controller;
//Pacote onde o controlador de categorias está localizado

//====================== IMPORTAÇÕES DE UTILIDADES ======================
import java.util.List;
import java.util.Optional; 
//List → para listas de categorias
//Optional → para tratar resultados que podem não existir (ex.: buscar por ID)

//====================== IMPORTAÇÕES DO SPRING ======================
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
//Anotações e classes do Spring para criar endpoints REST, manipular respostas e lançar erros HTTP

import com.generation.farmacia.model.Categoria;
import com.generation.farmacia.repository.CategoriaRepository;

//====================== IMPORTAÇÕES DE VALIDAÇÃO ======================
import jakarta.validation.Valid;
//@Valid → valida automaticamente os campos obrigatórios do objeto recebido via JSON

//====================== CONTROLADOR ======================
@RestController 
//Define a classe como um controlador REST
@RequestMapping("/categorias") 
//Define o caminho base da rota como /categorias
@CrossOrigin(origins = "*", allowedHeaders = "*") 
//Libera requisições de qualquer origem (CORS)
public class CategoriaController {

 @Autowired 
 // Injeta automaticamente o repositório de categorias
 private CategoriaRepository categoriaRepository;

 // ==============================================
 // MÉTODO 1: LISTAR TODAS AS CATEGORIAS
 // ==============================================
 @GetMapping
 // GET /categorias
 public ResponseEntity<List<Categoria>> getAll() {
     // Retorna 200 OK + lista completa de categorias
     return ResponseEntity.ok(categoriaRepository.findAll());
 }

 // ==============================================
 // MÉTODO 2: BUSCAR CATEGORIA POR ID
 // ==============================================
 @GetMapping("/{id}")
 // GET /categorias/{id}
 public ResponseEntity<Categoria> getById(@PathVariable Long id) {
     // Busca categoria pelo ID
     // Retorna 200 OK se encontrada, ou 404 Not Found se não existir
     return categoriaRepository.findById(id)
             .map(resposta -> ResponseEntity.ok(resposta))
             .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
 }

 // ==============================================
 // MÉTODO 3: BUSCAR CATEGORIAS POR DESCRIÇÃO
 // ==============================================
 @GetMapping("/descricao/{descricao}")
 // GET /categorias/descricao/{descricao}
 public ResponseEntity<List<Categoria>> getAllByDescricao(@PathVariable String descricao) {
     // Busca categorias cujo campo descricao contenha a string informada (case-insensitive)
     return ResponseEntity.ok(
         categoriaRepository.findAllByDescricaoContainingIgnoreCase(descricao)
     );
 }

 // ==============================================
 // MÉTODO 4: CRIAR NOVA CATEGORIA
 // ==============================================
 @PostMapping
 // POST /categorias
 public ResponseEntity<Categoria> post(@Valid @RequestBody Categoria categoria) {
     // Garante que será criado um novo registro
     categoria.setId(null);
     // Salva a categoria e retorna 201 Created
     return ResponseEntity.status(HttpStatus.CREATED)
             .body(categoriaRepository.save(categoria));
 }

 // ==============================================
 // MÉTODO 5: ATUALIZAR CATEGORIA EXISTENTE
 // ==============================================
 @PutMapping
 // PUT /categorias
 public ResponseEntity<Categoria> put(@Valid @RequestBody Categoria categoria) {
     // Verifica se a categoria existe pelo ID
     return categoriaRepository.findById(categoria.getId())
         .map(resposta -> ResponseEntity.status(HttpStatus.OK)
         .body(categoriaRepository.save(categoria))) // Atualiza e retorna 200 OK
         .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Se não existir → 404
 }

 // ==============================================
 // MÉTODO 6: DELETAR CATEGORIA
 // ==============================================
 @ResponseStatus(HttpStatus.NO_CONTENT) 
 // Retorna 204 No Content (sem corpo na resposta)
 @DeleteMapping("/{id}")
 // DELETE /categorias/{id}
 public void delete(@PathVariable Long id) {
     Optional<Categoria> categoria = categoriaRepository.findById(id);
     if(categoria.isEmpty()) // Se não encontrada → lança 404
         throw new ResponseStatusException(HttpStatus.NOT_FOUND);
     // Se encontrada → deleta do banco
     categoriaRepository.deleteById(id);
 }
}
