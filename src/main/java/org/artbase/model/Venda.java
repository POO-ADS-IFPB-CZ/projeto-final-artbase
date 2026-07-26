package org.artbase.model;

import java.time.LocalDate;

/**
 * Classe que representa a entidade Venda do sistema.
 * Cada venda pertence a um cliente e é composta por uma ou mais linhas
 * de ItemVenda (os produtos vendidos naquela venda).
 */
public class Venda {

    private Integer id;
    private LocalDate dataDaVenda;
    private Integer clienteId;
    private double valorTotal;
    private String formaDePagamento;
    private String situacaoDaVenda;

    // Preenchido só para exibição em telas/relatórios (não existe coluna
    // "cliente_nome" na tabela venda; vem de um JOIN com cliente)
    private String clienteNome;

    public Venda(Integer id, LocalDate dataDaVenda, Integer clienteId, double valorTotal,
                 String formaDePagamento, String situacaoDaVenda) {
        this.id = id;
        this.dataDaVenda = dataDaVenda;
        this.clienteId = clienteId;
        this.valorTotal = valorTotal;
        this.formaDePagamento = formaDePagamento;
        this.situacaoDaVenda = situacaoDaVenda;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDataDaVenda() {
        return dataDaVenda;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public String getFormaDePagamento() {
        return formaDePagamento;
    }

    public String getSituacaoDaVenda() {
        return situacaoDaVenda;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }
}
