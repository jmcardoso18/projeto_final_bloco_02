package com.generation.farmacia.controller;//Pacote onde o controlador de produtos está localizado

//====================== IMPORTAÇÕES DE UTILIDADES ======================
import java.util.List;
import java.util.Optional; 
//List → para listas de produtos
//Optional → para tratar resultados que podem estar vazios (ex.: buscar por ID)

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
//Classes e anotações para criar endpoints REST, manipular requisições/respostas e tratar exceções HTTP

import com.generation.farmacia.model.Produto;
import com.generation.farmacia.repository.CategoriaRepository;
import com.generation.farmacia.repository.ProdutoRepository;

//====================== IMPORTAÇÕES DE VALIDAÇÃO ======================
import jakarta.validation.Valid;
//@Valid → valida automaticamente os campos obrigatórios do objeto recebido via JSON

//====================== CONTROLADOR ======================
@RestController 
//Indica que esta classe é um controller REST (responde a requisições HTTP)
@RequestMapping("/produtos") 
//Define o caminho base da rota como /produtos
@CrossOrigin(origins ="*", allowedHeaders = "*") 
//Permite que a API seja acessada de qualquer origem (CORS)
public class ProdutoController {

 @Autowired 
 // Injeta automaticamente o repositório de produtos
 private ProdutoRepository produtoRepository;
 
 @Autowired 
 // Injeta o repositório de categorias (para validação da categoria do produto)
 private CategoriaRepository categoriaRepository;
 
 // ==============================================
 // MÉTODO 1: LISTAR TODOS OS PRODUTOS
 // ==============================================
 @GetMapping
 // GET /produtos
 public ResponseEntity<List<Produto>> getAll() {
     // Retorna 200 OK + lista de todos os produtos
     return ResponseEntity.ok(produtoRepository.findAll());
 }
 
 // ==============================================
 // MÉTODO 2: BUSCAR PRODUTO POR ID
 // ==============================================
 @GetMapping("/{id}")
 // GET /produtos/{id}
 public ResponseEntity<Produto> getById(@PathVariable Long id) {
     // Busca produto pelo ID, retorna 200 OK se encontrado ou 404 Not Found se não existir
     return produtoRepository.findById(id)
             .map(resposta -> ResponseEntity.ok(resposta))
             .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
 }
 
 // ==============================================
 // MÉTODO 3: BUSCAR PRODUTOS POR TÍTULO
 // ==============================================
 @GetMapping("/descricao/{descricao}")
 // GET /produtos/descricao/{descricao}
 public ResponseEntity<List<Produto>> getAllByDescricao(@PathVariable String descricao) {
     // Busca produtos cujo título contenha a string informada (case insensitive)
     return ResponseEntity.ok(produtoRepository.findAllByDescricaoContainingIgnoreCase(descricao));
 }
 
 // ==============================================
 // MÉTODO 4: CADASTRAR NOVO PRODUTO
 // ==============================================
 @PostMapping
 // POST /produtos
 public ResponseEntity<Produto> post(@Valid @RequestBody Produto produto) {
     // Valida se a categoria existe
     if(!categoriaRepository.existsById(produto.getCategoria().getId())) {
         throw new ResponseStatusException(
             HttpStatus.BAD_REQUEST, "A categoria informada não existe!"
         );
     }
     produto.setId(null); // Garante que será criado um novo registro
     return ResponseEntity.status(HttpStatus.CREATED)
             .body(produtoRepository.save(produto)); // Salva e retorna 201 Created
 }
 
 // ==============================================
 // MÉTODO 5: ATUALIZAR PRODUTO EXISTENTE
 // ==============================================
 @PutMapping
 // PUT /produtos
 public ResponseEntity<Produto> put(@Valid @RequestBody Produto produto) {
     // Verifica se o produto existe
     if(!produtoRepository.existsById(produto.getId())) {
         return ResponseEntity.notFound().build(); // 404 Not Found
     }
     // Valida se a categoria existe
     if(!categoriaRepository.existsById(produto.getCategoria().getId())) {
         throw new ResponseStatusException(
             HttpStatus.BAD_REQUEST, "A categoria informada não existe!"
         );
     }
     return ResponseEntity.status(HttpStatus.OK)
             .body(produtoRepository.save(produto)); // Atualiza e retorna 200 OK
 }
 
 // ==============================================
 // MÉTODO 6: EXCLUIR PRODUTO
 // ==============================================
 @ResponseStatus(HttpStatus.NO_CONTENT) 
 // Retorna 204 No Content (sem corpo)
 @DeleteMapping("/{id}")
 // DELETE /produtos/{id}
 public void delete(@PathVariable Long id) {
     Optional<Produto> produto = produtoRepository.findById(id);
     if(produto.isEmpty()) // Se produto não existe → 404 Not Found
         throw new ResponseStatusException(HttpStatus.NOT_FOUND);
     
     produtoRepository.deleteById(id); // Se existe → deleta do banco
 }
}
