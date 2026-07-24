package org.artbase.dao;
import org.artbase.database.ConnectionFactory;
import org.artbase.model.Usuario;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDaoJbdc {

    public Usuario getUsuarioByEmail(String email) throws IOException, ClassNotFoundException, SQLException {
        Usuario usuario = null;
        try(Connection con = new ConnectionFactory().getConnection()) {
            PreparedStatement preparedStatement = con
                    .prepareStatement("SELECT * FROM usuario WHERE email = ?");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                String nome = resultSet.getString("nome");
                String email1 = resultSet.getString("email");
                String senha = resultSet.getString("senha");
                usuario = new Usuario(nome, email1, senha);
            }
        }
        return usuario;
    }

    public boolean salvar(Usuario usuario) throws SQLException, IOException, ClassNotFoundException {
        try(Connection conn = new ConnectionFactory().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)"
            );
            preparedStatement.setString(1, usuario.getNome());
            preparedStatement.setString(2, usuario.getEmail());
            preparedStatement.setString(3, usuario.getSenha());
            return preparedStatement.executeUpdate()>0;
        }
    }

    public boolean deletar(String email) throws SQLException, IOException, ClassNotFoundException {
        try(Connection conn = new ConnectionFactory().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "DELETE FROM usuario WHERE email=?"
            );

            preparedStatement.setString(1, email);
            return preparedStatement.executeUpdate()>0;
        }
    }


}