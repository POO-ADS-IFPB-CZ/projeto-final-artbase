package org.artbase.model;

/**
 * Classe que representa a entidade Produto do sistema.
 * Os campos seguem exatamente o diagrama do banco de dados combinado
 * pela equipe: id, nome, descricao, preco, quantidade_disponivel,
 * estoque_minimo e categoria.
 */
public class Produto {

    // Identificador único do produto no banco de dados (chave primária)
    private Integer id;

    // Nome do produto exibido nas telas e relatórios
    private String nome;

    // Descrição detalhada do produto
    private String descricao;

    // Valor de venda do produto
    private double preco;

    // Quantidade disponível em estoque no momento
    private int quantidadeDisponivel;

    // Quantidade mínima que deve existir em estoque antes de soar um alerta de reposição
    private int estoqueMinimo;

    // Categoria/segmento do produto (ex: "Tintas", "Pincéis", "Telas")
    private String categoria;

    public Produto(Integer id, String nome, String descricao, double preco,
                    int quantidadeDisponivel, int estoqueMinimo, String categoria) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.estoqueMinimo = estoqueMinimo;
        this.categoria = categoria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(int estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Produto{id=" + id + ", nome='" + nome + "', preco=" + preco
                + ", quantidadeDisponivel=" + quantidadeDisponivel + "}";
    }
}
