package com.generation.farmacia.model;// Pacote onde o controller de Categoria está localizado

//Importa a anotação do Jackson que permite ignorar certas propriedades durante serialização/deserialização JSON
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;//Evitar loops infinitos em relacionamentos bidirecionais (Categoria -> Produto -> Categoria ...)

//Importações para anotações do Jackson e JPA/Hibernateimport jakarta.persistence.Column;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
//Importações para validação de dados
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity // Indica que esta classe é uma entidade JPA, ou seja, será mapeada para uma tabela no banco
@Table(name="tb_produtos") // Define o nome da tabela no banco como 'tb_produtos'
public class Produto {
  
  @Id // Define que este atributo é a chave primária da tabela
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática de valores (auto_increment no MySQL)
  private Long id;

  @NotBlank(message= "O atrituto nome é obrigatório!") // Valida que o campo não pode ser nulo ou vazio
  @Size(max = 255)//Tamanho do campo até 255
  private String nome;

  @Column(length=255) // Define o tamanho máximo da coluna no banco
  @NotBlank(message= "O atrituto descrição é obrigatório!") // Validação de não nulo ou vazio
  private String descricao;

  @NotNull(message= "O atrituto preço é obrigatório!") // Garante que não pode ser nulo
  @PositiveOrZero(message="O preço deve ser positivo ou zero") // Garante que o valor seja >= 0
  private Double preco;

  @NotBlank(message= "O atrituto foto é obrigatório!") // Valida que o campo não pode estar vazio
  private String foto;

  @ManyToOne // Relacionamento Muitos para Um com Categoria
  @JsonIgnoreProperties("produtos") // Ignora a propriedade 'produto' durante serialização para evitar recursão infinita
  private Categoria categoria;


  // ====================== GETTERS E SETTERS ======================
	public Long getId() {
      return id;
  }

  public void setId(Long id) {
      this.id = id;
  }

  public String getNome() {
	return nome;
}

  public void setNome(String nome) {
	this.nome = nome;
  }

  public String getDescricao() {
	return descricao;
  }

  public void setDescricao(String descricao) {
	this.descricao = descricao;
  }

  public Double getPreco() {
      return preco;
  }

  public void setPreco(Double preco) {
      this.preco = preco;
  }

  public String getFoto() {
      return foto;
  }

  public void setFoto(String foto) {
      this.foto = foto;
  }

  public Categoria getCategoria() {
      return categoria;
  }

  public void setCategoria(Categoria categoria) {
      this.categoria = categoria;
  }
  
}
