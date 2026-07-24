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

    public TelaAutenticacao() {
        // Configurações básicas da Janela
        setTitle("ArtBase - Autenticação");
        setContentPane(contentPane); // Diz para o JFrame usar o painel principal do .form
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela

        // Lógica do botão Login
        btnLogin.addActionListener(e -> {
            CardLayout cl = (CardLayout) painelCards.getLayout();
            cl.show(painelCards, "CARD_LOGIN"); // O nome exato que você colocou no Card Name
        });

        // Lógica do botão Registrar (Menu Lateral)
        btnRegistrar.addActionListener(e -> {
            CardLayout cl = (CardLayout) painelCards.getLayout();
            cl.show(painelCards, "CARD_REGISTRO");
        });

        // Lógica do botão Cadastrar
        cadastrarButton.addActionListener(e -> {
            // 1. Captura os dados digitados
            String nome = textField2.getText();
            String email = textField3.getText();
            String senha = textField4.getText();

            // 2. Validação básica para evitar campos vazios
            if (nome.trim().isEmpty() || email.trim().isEmpty() || senha.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, preencha todos os campos!",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return; // Interrompe a execução aqui se faltar algo
            }

            try {
                UsuarioDaoJbdc dao = new UsuarioDaoJbdc();

                // 3. Verifica se o email já existe no banco antes de salvar
                if (dao.getUsuarioByEmail(email) != null) {
                    JOptionPane.showMessageDialog(this,
                            "Este email já está cadastrado!",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 4. Cria o objeto modelo e tenta salvar
                Usuario novoUsuario = new Usuario(nome, email, senha);
                boolean sucesso = dao.salvar(novoUsuario);

                if (sucesso) {
                    JOptionPane.showMessageDialog(this,
                            "Usuário cadastrado com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Limpa os campos após o cadastro
                    textField2.setText("");
                    textField3.setText("");
                    textField4.setText("");

                    // Opcional: Redireciona o usuário direto para a tela de Login
                    CardLayout cl = (CardLayout) painelCards.getLayout();
                    cl.show(painelCards, "CARD_LOGIN");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Não foi possível cadastrar o usuário.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                // 5. Tratamento das exceções do DAO (SQLException, etc.)
                ex.printStackTrace(); // Imprime o erro no console para te ajudar a debugar
                JOptionPane.showMessageDialog(this,
                        "Erro de conexão com o banco: " + ex.getMessage(),
                        "Erro Crítico",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaAutenticacao().setVisible(true);
        });
    }
}
