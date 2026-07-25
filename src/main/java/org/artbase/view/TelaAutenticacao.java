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

        if (btnLogin != null && btnRegistrar != null && painelCards != null) {
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaAutenticacao().setVisible(true);
        });
    }
}
