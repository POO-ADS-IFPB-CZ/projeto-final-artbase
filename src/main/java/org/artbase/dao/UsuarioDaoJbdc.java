package org.artbase.dao;

import org.artbase.database.ConnectionFactory;
import org.artbase.model.Usuario;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsuarioDaoJbdc {

    /**
     * Cria a tabela "usuario" caso ela ainda não exista, e garante que as
     * colunas "nome" e "admin" existam mesmo em bancos onde a tabela já
     * tinha sido criada antes (com ADD COLUMN IF NOT EXISTS), evitando
     * quebra de compatibilidade com quem já tinha usuários cadastrados.
     */
    public void criarTabelaSeNecessario() throws SQLException, IOException, ClassNotFoundException {
        String criarTabela = """
                CREATE TABLE IF NOT EXISTS usuario (
                    email VARCHAR(160) PRIMARY KEY,
                    senha VARCHAR(255) NOT NULL,
                    nome VARCHAR(120),
                    admin BOOLEAN NOT NULL DEFAULT FALSE
                )
                """;

        try (Connection connection = new ConnectionFactory().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(criarTabela);
            statement.execute("ALTER TABLE usuario ADD COLUMN IF NOT EXISTS nome VARCHAR(120)");
            statement.execute("ALTER TABLE usuario ADD COLUMN IF NOT EXISTS admin BOOLEAN NOT NULL DEFAULT FALSE");
        }
    }

    public Usuario getUsuarioByEmail(String email) throws IOException, ClassNotFoundException, SQLException {
        Usuario usuario = null;
        try (Connection con = new ConnectionFactory().getConnection()) {
            PreparedStatement preparedStatement = con
                    .prepareStatement("SELECT email, senha, nome, admin FROM usuario WHERE email = ?");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                usuario = new Usuario(
                        resultSet.getString("email"),
                        resultSet.getString("senha"),
                        resultSet.getString("nome"),
                        resultSet.getBoolean("admin")
                );
            }
        }
        return usuario;
    }

    public boolean salvar(Usuario usuario) throws SQLException, IOException, ClassNotFoundException {
        try (Connection conn = new ConnectionFactory().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO usuario (email, senha, nome, admin) VALUES (?, ?, ?, ?)"
            );

            preparedStatement.setString(1, usuario.getEmail());
            preparedStatement.setString(2, usuario.getSenha());
            preparedStatement.setString(3, usuario.getNome());
            preparedStatement.setBoolean(4, usuario.isAdmin());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean deletar(String email) throws SQLException, IOException, ClassNotFoundException {
        try (Connection conn = new ConnectionFactory().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "DELETE FROM usuario WHERE email=?"
            );

            preparedStatement.setString(1, email);
            return preparedStatement.executeUpdate() > 0;
        }
    }

}
