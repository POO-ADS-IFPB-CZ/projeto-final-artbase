package org.artbase.view;

import org.artbase.dao.UsuarioDaoJbdc;
import org.artbase.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class TelaAutenticacao extends JDialog {
    private final UsuarioDaoJbdc usuarioDao = new UsuarioDaoJbdc();

    private CardLayout cardLayout;
    private JPanel painelCards;
    private JTextField campoEmailLogin;
    private JPasswordField campoSenhaLogin;
    private JTextField campoNomeCadastro;
    private JTextField campoEmailCadastro;
    private JPasswordField campoSenhaCadastro;
    private JLabel statusLabel;

    public TelaAutenticacao() {
        setTitle("ArtBase - Autenticação");
        setContentPane(montarInterface());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(980, 620));
        setSize(1040, 680);
        setLocationRelativeTo(null);

        try {
            usuarioDao.criarTabelaSeNecessario();
            mostrarStatus("Informe seus dados para entrar ou criar acesso.", false);
        } catch (Exception ex) {
            mostrarStatus("Erro ao preparar o banco de dados: " + ex.getMessage(), true);
        }
    }

    private JPanel montarInterface() {
        JPanel raiz = EstiloTelaPadrao.criarPainelRaiz();
        raiz.setLayout(new GridLayout(1, 2, 18, 0));
        raiz.add(montarPainelMarca());
        raiz.add(montarPainelAcesso());
        return raiz;
    }

    private JPanel montarPainelMarca() {
        JPanel card = EstiloTelaPadrao.criarCard(new BorderLayout(0, 18));

        JPanel topo = new JPanel(new GridLayout(3, 1, 0, 8));
        topo.setOpaque(false);
        JLabel titulo = EstiloTelaPadrao.criarTitulo("ArtBase");
        JLabel subtitulo = EstiloTelaPadrao.criarSubtitulo("Acesse o sistema com a mesma identidade visual das telas operacionais.");
        JLabel descricao = EstiloTelaPadrao.criarSubtitulo("Clientes, produtos e vendas agora seguem um mesmo padrão de leitura, espaçamento e ações.");
        topo.add(titulo);
        topo.add(subtitulo);
        topo.add(descricao);

        JTextArea manifesto = new JTextArea(
                "Centralize seu fluxo em uma interface mais uniforme, com blocos claros, formulários consistentes e feedback visual direto."
        );
        manifesto.setEditable(false);
        manifesto.setOpaque(false);
        manifesto.setForeground(EstiloTelaPadrao.TEXTO);
        manifesto.setFont(new Font("SansSerif", Font.PLAIN, 16));
        manifesto.setLineWrap(true);
        manifesto.setWrapStyleWord(true);

        JPanel destaque = new JPanel(new GridLayout(3, 1, 0, 10));
        destaque.setOpaque(false);
        destaque.add(criarPill("Base visual: cadastro de clientes"));
        destaque.add(criarPill("Ações principais em azul"));
        destaque.add(criarPill("Cards brancos com borda suave"));

        card.add(topo, BorderLayout.NORTH);
        card.add(manifesto, BorderLayout.CENTER);
        card.add(destaque, BorderLayout.SOUTH);
        return card;
    }

    private JPanel montarPainelAcesso() {
        JPanel card = EstiloTelaPadrao.criarCard(new BorderLayout(0, 18));

        JPanel navegacao = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        navegacao.setOpaque(false);
        JButton btnLogin = new JButton("Login");
        JButton btnRegistrar = new JButton("Registrar");
        EstiloTelaPadrao.estilizarBotaoPrimario(btnLogin);
        EstiloTelaPadrao.estilizarBotaoSecundario(btnRegistrar);
        navegacao.add(btnLogin);
        navegacao.add(btnRegistrar);

        cardLayout = new CardLayout();
        painelCards = new JPanel(cardLayout);
        painelCards.setOpaque(false);
        painelCards.add(montarCardLogin(), "login");
        painelCards.add(montarCardRegistro(), "registro");

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(EstiloTelaPadrao.TEXTO_SUAVE);

        btnLogin.addActionListener(e -> cardLayout.show(painelCards, "login"));
        btnRegistrar.addActionListener(e -> cardLayout.show(painelCards, "registro"));

        card.add(navegacao, BorderLayout.NORTH);
        card.add(painelCards, BorderLayout.CENTER);
        card.add(statusLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel montarCardLogin() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 0);

        campoEmailLogin = new JTextField();
        campoSenhaLogin = new JPasswordField();
        EstiloTelaPadrao.estilizarCampo(campoEmailLogin);
        EstiloTelaPadrao.estilizarCampo(campoSenhaLogin);
        adicionarCampo(painel, gbc, "E-mail", campoEmailLogin);
        adicionarCampo(painel, gbc, "Senha", campoSenhaLogin);

        JButton entrarButton = new JButton("Entrar");
        EstiloTelaPadrao.estilizarBotaoPrimario(entrarButton);
        entrarButton.addActionListener(e -> realizarLogin());

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.add(entrarButton);
        painel.add(acoes, gbc);
        return painel;
    }

    private JPanel montarCardRegistro() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 0);

        campoNomeCadastro = new JTextField();
        campoEmailCadastro = new JTextField();
        campoSenhaCadastro = new JPasswordField();
        EstiloTelaPadrao.estilizarCampo(campoNomeCadastro);
        EstiloTelaPadrao.estilizarCampo(campoEmailCadastro);
        EstiloTelaPadrao.estilizarCampo(campoSenhaCadastro);
        adicionarCampo(painel, gbc, "Nome", campoNomeCadastro);
        adicionarCampo(painel, gbc, "E-mail", campoEmailCadastro);
        adicionarCampo(painel, gbc, "Senha", campoSenhaCadastro);

        JButton cadastrarButton = new JButton("Criar conta");
        EstiloTelaPadrao.estilizarBotaoPrimario(cadastrarButton);
        cadastrarButton.addActionListener(e -> realizarCadastro());

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.add(cadastrarButton);
        painel.add(acoes, gbc);
        return painel;
    }

    private JPanel criarPill(String texto) {
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        pill.setBackground(new Color(248, 250, 252));
        pill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloTelaPadrao.BORDA),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        JLabel label = new JLabel(texto);
        label.setForeground(EstiloTelaPadrao.TEXTO);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        pill.add(label);
        return pill;
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, String rotulo, JComponent campo) {
        JLabel label = new JLabel(rotulo);
        label.setForeground(EstiloTelaPadrao.TEXTO_SUAVE);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));

        JPanel bloco = new JPanel(new BorderLayout(0, 6));
        bloco.setOpaque(false);
        bloco.add(label, BorderLayout.NORTH);
        bloco.add(campo, BorderLayout.CENTER);

        painel.add(bloco, gbc);
        gbc.gridy++;
    }

    private void realizarLogin() {
        String email = campoEmailLogin.getText().trim();
        String senha = new String(campoSenhaLogin.getPassword());
        if (email.isEmpty() || senha.isEmpty()) {
            mostrarStatus("Preencha e-mail e senha.", true);
            return;
        }

        try {
            Usuario usuario = usuarioDao.getUsuarioByEmail(email);
            if (usuario == null || !usuario.getSenha().equals(senha)) {
                mostrarStatus("E-mail ou senha incorretos.", true);
                return;
            }

            dispose();
            SwingUtilities.invokeLater(() -> new TelaDashboard(usuario.getNome(), usuario.isAdmin()).setVisible(true));
        } catch (Exception ex) {
            mostrarStatus("Erro ao tentar fazer login: " + ex.getMessage(), true);
        }
    }

    private void realizarCadastro() {
        String nome = campoNomeCadastro.getText().trim();
        String email = campoEmailCadastro.getText().trim();
        String senha = new String(campoSenhaCadastro.getPassword());
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            mostrarStatus("Preencha nome, e-mail e senha.", true);
            return;
        }

        try {
            if (usuarioDao.getUsuarioByEmail(email) != null) {
                mostrarStatus("Já existe um usuário com esse e-mail.", true);
                return;
            }

            usuarioDao.salvar(new Usuario(email, senha, nome, true));
            campoNomeCadastro.setText("");
            campoEmailCadastro.setText("");
            campoSenhaCadastro.setText("");
            cardLayout.show(painelCards, "login");
            mostrarStatus("Usuário cadastrado com sucesso. Faça login para continuar.", false);
        } catch (Exception ex) {
            mostrarStatus("Erro ao cadastrar usuário: " + ex.getMessage(), true);
        }
    }

    private void mostrarStatus(String mensagem, boolean erro) {
        statusLabel.setForeground(erro ? EstiloTelaPadrao.ERRO : EstiloTelaPadrao.SUCESSO);
        statusLabel.setText("<html>" + mensagem + "</html>");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaAutenticacao().setVisible(true));
    }
}
