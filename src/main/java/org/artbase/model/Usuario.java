package org.artbase.model;

/**
 * Classe que representa a entidade Usuario do sistema (quem faz login).
 * O campo "admin" define se esse usuário tem acesso à tela de Produtos,
 * além do acesso padrão à tela de Clientes que todo usuário logado tem.
 */
public class Usuario {
    private String email;
    private String senha;
    private String nome;
    private boolean admin;

    /**
     * Construtor usado no cadastro de um novo usuário comum (não admin).
     */
    public Usuario(String email, String senha, String nome) {
        this(email, senha, nome, false);
    }

    /**
     * Construtor completo, usado ao ler um usuário já existente do banco
     * (onde o valor de admin já está definido).
     */
    public Usuario(String email, String senha, String nome, boolean admin) {
        this.email = email;
        this.senha = senha;
        this.nome = nome;
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getNome() {
        return nome;
    }

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public String toString() {
        return "Usuario{email='" + email + "', nome='" + nome + "', admin=" + admin + "}";
    }
}
