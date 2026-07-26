package org.artbase.controller;

import org.artbase.dao.ProdutoDaoJdbc;
import org.artbase.model.Produto;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Classe responsável por intermediar a comunicação entre a View (telas)
 * e o DAO de Produto, seguindo o padrão MVC.
 * Aqui ficam as regras de negócio e validações antes de qualquer dado
 * ser enviado para persistência no banco, mantendo a View "burra"
 * (sem lógica) e o DAO focado apenas em acesso a dados.
 */
public class ProdutoController {

    // Instância do DAO utilizada para executar as operações no banco
    private final ProdutoDaoJdbc produtoDao = new ProdutoDaoJdbc();

    /**
     * Garante que a tabela produto existe no banco. Deve ser chamado uma
     * vez ao abrir a tela de produtos, antes de qualquer outra operação.
     */
    public void garantirTabela() throws SQLException, IOException, ClassNotFoundException {
        produtoDao.criarTabelaSeNecessario();
    }

    /**
     * Cadastra um novo produto após validar os dados básicos.
     * Lança IllegalArgumentException caso alguma regra de negócio
     * seja violada, evitando que dados inválidos cheguem até o banco.
     */
    public boolean cadastrar(String nome, String descricao, double preco,
                              int quantidadeDisponivel, int estoqueMinimo, String categoria)
            throws SQLException, IOException, ClassNotFoundException {

        validarDados(nome, preco, quantidadeDisponivel, estoqueMinimo);

        // id = null pois ainda não existe no banco; será gerado pela identity do PostgreSQL
        Produto produto = new Produto(null, nome, descricao, preco, quantidadeDisponivel, estoqueMinimo, categoria);
        return produtoDao.salvar(produto);
    }

    /**
     * Atualiza um produto já existente, identificado pelo id.
     * Também passa pelas mesmas validações do cadastro para garantir
     * que uma edição não deixe o produto em estado inconsistente.
     */
    public boolean atualizar(int id, String nome, String descricao, double preco,
                              int quantidadeDisponivel, int estoqueMinimo, String categoria)
            throws SQLException, IOException, ClassNotFoundException {

        validarDados(nome, preco, quantidadeDisponivel, estoqueMinimo);

        Produto produto = new Produto(id, nome, descricao, preco, quantidadeDisponivel, estoqueMinimo, categoria);
        return produtoDao.atualizar(produto);
    }

    /**
     * Remove um produto pelo id.
     * Delegado diretamente ao DAO, pois não há regra de negócio
     * adicional necessária para uma exclusão simples.
     */
    public boolean remover(int id) throws SQLException, IOException, ClassNotFoundException {
        return produtoDao.deletar(id);
    }

    /**
     * Busca um produto específico pelo id, repassando o resultado do DAO.
     * Retorna null caso o produto não seja encontrado.
     */
    public Produto buscarPorId(int id) throws SQLException, IOException, ClassNotFoundException {
        return produtoDao.buscarPorId(id);
    }

    /**
     * Retorna a lista completa de produtos cadastrados, usada para
     * preencher tabelas/listas na tela de Produtos (TelaProduto).
     */
    public List<Produto> listarTodos() throws SQLException, IOException, ClassNotFoundException {
        return produtoDao.listarTodos();
    }

    /**
     * Centraliza as validações de negócio para cadastro/atualização de
     * produtos, evitando duplicação de código entre os métodos acima.
     * Lança IllegalArgumentException com uma mensagem amigável, que a
     * View pode capturar e exibir diretamente ao usuário em um JOptionPane.
     */
    private void validarDados(String nome, double preco, int quantidadeDisponivel, int estoqueMinimo) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto não pode ser vazio.");
        }
        if (preco < 0) {
            throw new IllegalArgumentException("O preço do produto não pode ser negativo.");
        }
        if (quantidadeDisponivel < 0) {
            throw new IllegalArgumentException("A quantidade disponível não pode ser negativa.");
        }
        if (estoqueMinimo < 0) {
            throw new IllegalArgumentException("O estoque mínimo não pode ser negativo.");
        }
    }
}
