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
     * Cadastra um novo produto após validar os dados básicos.
     * Lança IllegalArgumentException caso alguma regra de negócio
     * seja violada (nome vazio, preço ou estoque negativos), evitando
     * que dados inválidos cheguem até o banco de dados.
     */
    public boolean cadastrar(String nome, String descricao, double preco, int estoque)
            throws SQLException, IOException, ClassNotFoundException {

        validarDados(nome, preco, estoque);

        // id = 0 pois ainda não existe no banco; será gerado pelo SERIAL do PostgreSQL
        Produto produto = new Produto(0, nome, descricao, preco, estoque);
        return produtoDao.salvar(produto);
    }

    /**
     * Atualiza um produto já existente, identificado pelo id.
     * Também passa pelas mesmas validações do cadastro para garantir
     * que uma edição não deixe o produto em estado inconsistente.
     */
    public boolean atualizar(int id, String nome, String descricao, double preco, int estoque)
            throws SQLException, IOException, ClassNotFoundException {

        validarDados(nome, preco, estoque);

        Produto produto = new Produto(id, nome, descricao, preco, estoque);
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
    private void validarDados(String nome, double preco, int estoque) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto não pode ser vazio.");
        }
        if (preco < 0) {
            throw new IllegalArgumentException("O preço do produto não pode ser negativo.");
        }
        if (estoque < 0) {
            throw new IllegalArgumentException("O estoque do produto não pode ser negativo.");
        }
    }
}
