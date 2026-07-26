package org.artbase.model;

/**
 * Classe que representa uma linha (item) de uma Venda: um produto
 * específico, a quantidade vendida e o preço praticado naquele momento.
 * O preço fica congelado no item mesmo que o produto mude de valor depois.
 */
public class ItemVenda {

    private Integer id;
    private Integer vendaId;
    private Integer produtoId;
    private int quantidade;
    private double precoUnitario;
    private double subtotal;

    // Preenchido só para exibição na tela (não existe coluna "produto_nome"
    // na tabela item_venda)
    private String produtoNome;

    public ItemVenda(Integer id, Integer vendaId, Integer produtoId, int quantidade,
                      double precoUnitario, double subtotal) {
        this.id = id;
        this.vendaId = vendaId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
    }

    public Integer getId() {
        return id;
    }

    public Integer getVendaId() {
        return vendaId;
    }

    public void setVendaId(Integer vendaId) {
        this.vendaId = vendaId;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public void setProdutoNome(String produtoNome) {
        this.produtoNome = produtoNome;
    }
}
