package org.artbase.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Classe que representa a entidade Produto do sistema.
 * Utiliza as anotações do Lombok para gerar automaticamente:
 * - Getters e Setters (@Data)
 * - Construtor com todos os argumentos (@AllArgsConstructor)
 * - Construtor sem argumentos (@NoArgsConstructor) - necessário para
 *   criar objetos antes de popular os dados vindos do banco
 * - Método toString() (@ToString) - útil para debug e logs
 */
@Data @AllArgsConstructor @NoArgsConstructor @ToString
public class Produto {

    // Identificador único do produto no banco de dados (chave primária)
    private int id;

    // Nome do produto exibido nas telas e relatórios
    private String nome;

    // Descrição detalhada do produto
    private String descricao;

    // Valor de venda do produto
    private double preco;

    // Quantidade disponível em estoque
    private int estoque;
}
