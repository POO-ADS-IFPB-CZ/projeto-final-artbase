package org.artbase.view;

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

        // Lógica do botão Registrar
        btnRegistrar.addActionListener(e -> {
            CardLayout cl = (CardLayout) painelCards.getLayout();
            cl.show(painelCards, "CARD_REGISTRO"); // O nome exato que você colocou no Card Name
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaAutenticacao().setVisible(true);
        });
    }
}
