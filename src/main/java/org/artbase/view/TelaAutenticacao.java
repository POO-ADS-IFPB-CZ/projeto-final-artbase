package org.artbase.view;

import org.artbase.dao.UsuarioDaoJbdc;
import org.artbase.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TelaAutenticacao extends JDialog {
    private JPanel contentPane;
    private JPanel painelCards;
    private JPanel CARD_LOGIN;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton entrarButton;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JButton cadastrarButton;
    private JPanel CARD_REGISTRO;
    private JButton btnLogin;
    private JButton btnRegistrar;
    private JButton buttonOK;
    private JButton buttonCancel;

    // DAO usado tanto para validar login quanto para cadastrar novos usuários
    private final UsuarioDaoJbdc usuarioDao = new UsuarioDaoJbdc();

    public TelaAutenticacao() {
        // Se o .form não estiver ligado em tempo de execução, monta uma tela simples
        // para manter o projeto executável sem depender do GUI Designer.
        if (contentPane == null) {
            contentPane = new JPanel(new BorderLayout(12, 12));
            contentPane.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

            JLabel titulo = new JLabel("ArtBase");
            titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));

            JLabel subtitulo = new JLabel("Tela de autenticação");
            subtitulo.setFont(subtitulo.getFont().deriveFont(Font.PLAIN, 14f));

            JPanel topo = new JPanel(new GridLayout(2, 1, 0, 4));
            topo.setOpaque(false);
            topo.add(titulo);
            topo.add(subtitulo);

            JButton abrirLogin = new JButton("Login");
            JButton abrirCadastro = new JButton("Registrar");

            JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            acoes.setOpaque(false);
            acoes.add(abrirLogin);
            acoes.add(abrirCadastro);

            contentPane.add(topo, BorderLayout.NORTH);
            contentPane.add(acoes, BorderLayout.CENTER);

            abrirLogin.addActionListener(e ->
                    JOptionPane.showMessageDialog(this, "Fluxo de login ainda não implementado."));
            abrirCadastro.addActionListener(e ->
                    JOptionPane.showMessageDialog(this, "Fluxo de cadastro ainda não implementado."));
        }

        // Configurações básicas da Janela
        setTitle("ArtBase - Autenticação");
        setContentPane(contentPane); // Diz para o JFrame usar o painel principal do .form
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela

        // Garante que a tabela usuario existe antes de qualquer tentativa
        // de login ou cadastro, evitando o erro "relation usuario does not exist".
        try {
            usuarioDao.criarTabelaSeNecessario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao preparar o banco de dados: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }

        if (btnLogin != null && btnRegistrar != null && painelCards != null) {
            // Lógica do botão Login (alterna para o card de login)
            btnLogin.addActionListener(e -> {
                CardLayout cl = (CardLayout) painelCards.getLayout();
                cl.show(painelCards, "CARD_LOGIN"); // O nome exato que você colocou no Card Name
            });

            // Lógica do botão Registrar (alterna para o card de registro)
            btnRegistrar.addActionListener(e -> {
                CardLayout cl = (CardLayout) painelCards.getLayout();
                cl.show(painelCards, "CARD_REGISTRO"); // O nome exato que você colocou no Card Name
            });
        }

        // Ação real de login: valida email/senha contra o banco
        if (entrarButton != null) {
            entrarButton.addActionListener(e -> realizarLogin());
        }

        // Ação real de cadastro: cria um novo usuário (não admin) no banco
        if (cadastrarButton != null) {
            cadastrarButton.addActionListener(e -> realizarCadastro());
        }
    }

    /**
     * Lê email e senha do card de login, valida contra o banco de dados
     * e, se corretos, fecha esta tela e abre a tela de Clientes.
     * Usuários com admin = true também recebem acesso à tela de Produtos
     * a partir da tela de Clientes.
     */
    private void realizarLogin() {
        String email = textField1.getText().trim();
        String senha = new String(passwordField1.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha email e senha.",
                    "Dados incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario usuario = usuarioDao.getUsuarioByEmail(email);
            if (usuario == null || !usuario.getSenha().equals(senha)) {
                JOptionPane.showMessageDialog(this, "Email ou senha incorretos.",
                        "Falha no login", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Login correto: fecha a tela de autenticação e abre o Painel (Dashboard),
            // já informando o nome do usuário e se ele tem ou não acesso admin
            // (Produtos e Vendas). A partir do Painel, o usuário consegue navegar
            // para todas as outras telas do sistema.
            dispose();
            SwingUtilities.invokeLater(() ->
                    new TelaDashboard(usuario.getNome(), usuario.isAdmin()).setVisible(true));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao tentar fazer login: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lê nome, email e senha do card de registro e cadastra um novo
     * usuário no banco. Por decisão da equipe, todo usuário cadastrado
     * aqui já é admin (tem acesso a Produtos e Vendas além de Clientes).
     * Depois de cadastrar, volta para o card de login.
     */
    private void realizarCadastro() {
        String nome = textField2.getText().trim();
        String email = textField3.getText().trim();
        String senha = textField4.getText();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha nome, email e senha.",
                    "Dados incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (usuarioDao.getUsuarioByEmail(email) != null) {
                JOptionPane.showMessageDialog(this, "Já existe um usuário com esse email.",
                        "Email já cadastrado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Por decisão da equipe, todo usuário cadastrado por aqui já nasce admin
            // (não existe hoje um fluxo separado de "usuário comum" vs "admin" no cadastro).
            usuarioDao.salvar(new Usuario(email, senha, nome, true));
            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso! Agora faça login.");

            textField2.setText("");
            textField3.setText("");
            textField4.setText("");

            if (painelCards != null) {
                CardLayout cl = (CardLayout) painelCards.getLayout();
                cl.show(painelCards, "CARD_LOGIN");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar usuário: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaAutenticacao().setVisible(true);
        });
    }
}
