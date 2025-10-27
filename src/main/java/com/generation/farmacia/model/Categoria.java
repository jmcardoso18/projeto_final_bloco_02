package com.generation.farmacia.model;// Pacote onde o controller de Categoria está localizado

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
//Importações para validação de dados
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity // Indica que esta classe será mapeada para uma tabela no banco de dados
@Table(name = "tb_categorias") // Define o nome da tabela no banco como 'tb_categorias'
public class Categoria {

    @Id // Define este atributo como chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática de valores (auto_increment)
    private Long id; 

    @Column(length = 1000) // Define o tamanho máximo da coluna como 100 caracteres
    @NotBlank(message = "O Atributo descricao é obrigatório") // Valida que não pode estar vazio ou nulo
    @Size(min = 3, max = 1000, message = "A descricao deve ter entre 3 e 1000 caracteres") // Valida tamanho mínimo e máximo
    private String descricao;

  
    // ====================== GETTERS E SETTERS ======================
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


}

