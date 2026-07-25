package org.artbase.dao;

import org.artbase.database.ConnectionFactory;
import org.artbase.model.Produto;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por toda a persistência (CRUD) da entidade Produto
 * no banco de dados PostgreSQL, seguindo o mesmo padrão de acesso via
 * JDBC utilizado em UsuarioDaoJbdc: cada método abre sua própria conexão
 * através da ConnectionFactory, utilizando try-with-resources para
 * garantir o fechamento automático da conexão ao final da operação.
 */
public class ProdutoDaoJdbc {

    /**
     * Insere um novo produto no banco de dados.
     * Recebe um objeto Produto já preenchido (sem o id, que é gerado
     * automaticamente pelo banco) e retorna true caso a inserção
     * tenha afetado ao menos uma linha.
     */
    public boolean salvar(Produto produto) throws SQLException, IOException, ClassNotFoundException {
        try (Connection conn = new ConnectionFactory().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO produto (nome, descricao, preco, estoque) VALUES (?, ?, ?, ?)"
            );

            preparedStatement.setString(1, produto.getNome());
            preparedStatement.setString(2, produto.getDescricao());
            preparedStatement.setDouble(3, produto.getPreco());
            preparedStatement.setInt(4, produto.getEstoque());

            return preparedStatement.executeUpdate() > 0;
        }
    }

    /**
     * Atualiza os dados de um produto já existente, localizando-o pelo id.
     * Retorna true caso alguma linha tenha sido efetivamente alterada.
     */
    public boolean atualizar(Produto produto) throws SQLException, IOException, ClassNotFoundException {
        try (Connection conn = new ConnectionFactory().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "UPDATE produto SET nome = ?, descricao = ?, preco = ?, estoque = ? WHERE id = ?"
            );

            preparedStatement.setString(1, produto.getNome());
            preparedStatement.setString(2, produto.getDescricao());
            preparedStatement.setDouble(3, produto.getPreco());
            preparedStatement.setInt(4, produto.getEstoque());
            preparedStatement.setInt(5, produto.getId());

            return preparedStatement.executeUpdate() > 0;
        }
    }

    /**
     * Remove um produto do banco de dados a partir do seu id.
     * Retorna true caso o registro tenha sido encontrado e removido.
     */
    public boolean deletar(int id) throws SQLException, IOException, ClassNotFoundException {
        try (Connection conn = new ConnectionFactory().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "DELETE FROM produto WHERE id = ?"
            );

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    /**
     * Busca um único produto pelo seu id.
     * Retorna o objeto Produto preenchido, ou null caso nenhum registro
     * seja encontrado com o id informado.
     */
    public Produto buscarPorId(int id) throws SQLException, IOException, ClassNotFoundException {
        Produto produto = null;
        try (Connection conn = new ConnectionFactory().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT * FROM produto WHERE id = ?"
            );
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                produto = new Produto(
                        resultSet.getInt("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("descricao"),
                        resultSet.getDouble("preco"),
                        resultSet.getInt("estoque")
                );
            }
        }
        return produto;
    }

    /**
     * Lista todos os produtos cadastrados no banco de dados.
     * Percorre o ResultSet inteiro, montando um objeto Produto para
     * cada linha retornada e adicionando-o à lista de resultado.
     */
    public List<Produto> listarTodos() throws SQLException, IOException, ClassNotFoundException {
        List<Produto> produtos = new ArrayList<>();
        try (Connection conn = new ConnectionFactory().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT * FROM produto ORDER BY nome"
            );
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Produto produto = new Produto(
                        resultSet.getInt("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("descricao"),
                        resultSet.getDouble("preco"),
                        resultSet.getInt("estoque")
                );
                produtos.add(produto);
            }
        }
        return produtos;
    }
}
