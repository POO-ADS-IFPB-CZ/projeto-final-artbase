package org.artbase.controller;

import org.artbase.dao.VendaDaoJdbc;
import org.artbase.model.ItemVenda;
import org.artbase.model.Venda;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe responsável por intermediar a comunicação entre a TelaVenda e
 * o VendaDaoJdbc, seguindo o padrão MVC. Concentra as validações de
 * negócio antes de mandar qualquer coisa para o banco.
 */
public class VendaController {

    private final VendaDaoJdbc vendaDao = new VendaDaoJdbc();

    /**
     * Garante que as tabelas venda e item_venda existem. Deve ser chamado
     * ao abrir a tela de vendas, antes de qualquer outra operação.
     */
    public void garantirTabelas() throws SQLException, IOException, ClassNotFoundException {
        vendaDao.criarTabelaSeNecessario();
    }

    /**
     * Fecha uma venda: valida que existe cliente e pelo menos um item,
     * calcula o valor total a partir dos itens e delega ao DAO o
     * registro da venda, dos itens e a baixa de estoque, tudo em uma
     * única transação.
     */
    public Venda registrarVenda(Integer clienteId, List<ItemVenda> itens, String formaDePagamento)
            throws SQLException, IOException, ClassNotFoundException {

        if (clienteId == null) {
            throw new IllegalArgumentException("Selecione um cliente para a venda.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Adicione pelo menos um item à venda.");
        }
        if (formaDePagamento == null || formaDePagamento.trim().isEmpty()) {
            throw new IllegalArgumentException("Selecione a forma de pagamento.");
        }

        double valorTotal = itens.stream().mapToDouble(ItemVenda::getSubtotal).sum();

        Venda venda = new Venda(null, LocalDate.now(), clienteId, valorTotal, formaDePagamento, "CONCLUIDA");
        return vendaDao.registrarVenda(venda, itens);
    }

    /**
     * Retorna todas as vendas já registradas, usada para preencher um
     * histórico/relatório simples de vendas.
     */
    public List<Venda> listarTodas() throws SQLException, IOException, ClassNotFoundException {
        return vendaDao.listarTodas();
    }
}
