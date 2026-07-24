package org.artbase.view;

import org.artbase.dao.ClienteDaoJdbc;
import org.artbase.model.Cliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class TelaCadastroCliente extends JFrame {
    private static final Color FUNDO = new Color(245, 247, 250);
    private static final Color AZUL = new Color(35, 91, 168);
    private static final Pattern EMAIL_VALIDO =
            Pattern.compile("^[\\w.!#$%&'*+/=?^`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)+$");

    private JPanel contentPane;
    private JPanel painelPrincipal;
    private JPanel painelFormulario;
    private JPanel painelTabela;
    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoTelefone;
    private JTextField campoEmail;
    private JTextArea campoEndereco;
    private JButton botaoSalvar;
    private JLabel statusLabel;
    private JTable tabelaClientes;

    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new String[]{"ID", "Nome", "CPF", "Telefone", "E-mail", "Endereço"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final ClienteDaoJdbc clienteDao = new ClienteDaoJdbc();

    public TelaCadastroCliente() {
        super("ArtBase - Cadastro de clientes");
        montarInterface();
        configurarJanela();
        configurarTabela();
        inicializarBanco();
    }

    private void montarInterface() {
        contentPane = new JPanel(new BorderLayout(20, 20));
        contentPane.setBackground(FUNDO);
        contentPane.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel cabecalho = new JPanel();
        cabecalho.setOpaque(false);
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Clientes");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 28f));
        titulo.setForeground(new Color(30, 38, 51));

        JLabel subtitulo = new JLabel("Cadastre e consulte as pessoas que compram seus produtos.");
        subtitulo.setFont(subtitulo.getFont().deriveFont(14f));
        subtitulo.setForeground(new Color(95, 105, 120));

        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(4));
        cabecalho.add(subtitulo);
        contentPane.add(cabecalho, BorderLayout.NORTH);

        painelPrincipal = new JPanel(new BorderLayout(20, 0));
        painelPrincipal.setOpaque(false);
        contentPane.add(painelPrincipal, BorderLayout.CENTER);

        painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBackground(Color.WHITE);
        painelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 228, 234)),
                new EmptyBorder(18, 18, 18, 18)
        ));
        painelFormulario.setPreferredSize(new Dimension(360, 0));
        painelPrincipal.add(painelFormulario, BorderLayout.WEST);

        painelTabela = new JPanel(new BorderLayout(0, 10));
        painelTabela.setBackground(Color.WHITE);
        painelTabela.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 228, 234)),
                new EmptyBorder(18, 18, 18, 18)
        ));
        painelPrincipal.add(painelTabela, BorderLayout.CENTER);

        campoNome = new JTextField();
        campoCpf = new JTextField();
        campoTelefone = new JTextField();
        campoEmail = new JTextField();
        campoEndereco = new JTextArea(5, 20);
        campoEndereco.setLineWrap(true);
        campoEndereco.setWrapStyleWord(true);
        campoEndereco.setRows(5);
        campoEndereco.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        campoEndereco.setMargin(new Insets(8, 8, 8, 8));
        botaoSalvar = new JButton("Cadastrar cliente");
        statusLabel = new JLabel(" ");
        tabelaClientes = new JTable();

        construirFormulario();
        construirTabela();
    }

    private void construirFormulario() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        int linha = 0;
        adicionarTituloFormulario(gbc, linha++);
        adicionarCampo(gbc, linha++, "Nome completo *", campoNome);
        adicionarCampo(gbc, linha++, "CPF *", campoCpf);
        adicionarCampo(gbc, linha++, "Telefone *", campoTelefone);
        adicionarCampo(gbc, linha++, "E-mail *", campoEmail);
        JScrollPane rolagemEndereco = new JScrollPane(campoEndereco);
        rolagemEndereco.setPreferredSize(new Dimension(0, 110));
        rolagemEndereco.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        adicionarCampo(gbc, linha++, "Endereço completo *", rolagemEndereco);

        botaoSalvar.setBackground(AZUL);
        botaoSalvar.setForeground(Color.WHITE);
        botaoSalvar.setFocusPainted(false);
        botaoSalvar.setFont(botaoSalvar.getFont().deriveFont(Font.BOLD, 14f));
        botaoSalvar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botaoSalvar.addActionListener(event -> salvarCliente());
        gbc.gridy = linha++;
        gbc.insets = new Insets(16, 0, 8, 0);
        gbc.ipady = 8;
        painelFormulario.add(botaoSalvar, gbc);

        statusLabel.setForeground(new Color(184, 50, 50));
        gbc.gridy = linha;
        gbc.insets = new Insets(2, 0, 0, 0);
        gbc.ipady = 0;
        gbc.weighty = 1;
        painelFormulario.add(statusLabel, gbc);
    }

    private void adicionarTituloFormulario(GridBagConstraints gbc, int linha) {
        JLabel titulo = new JLabel("Novo cliente");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        gbc.gridy = linha;
        gbc.insets = new Insets(0, 0, 12, 0);
        painelFormulario.add(titulo, gbc);
    }

    private void adicionarCampo(GridBagConstraints gbc, int linha, String texto, Component campo) {
        JPanel grupo = new JPanel(new BorderLayout(0, 5));
        grupo.setOpaque(false);
        JLabel label = new JLabel(texto);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));
        grupo.add(label, BorderLayout.NORTH);
        grupo.add(campo, BorderLayout.CENTER);

        gbc.gridy = linha;
        gbc.insets = new Insets(0, 0, 11, 0);
        gbc.ipady = campo instanceof JScrollPane ? 0 : 6;
        painelFormulario.add(grupo, gbc);
    }

    private void construirTabela() {
        tabelaClientes.setModel(modeloTabela);
        tabelaClientes.setRowHeight(28);
        tabelaClientes.setFillsViewportHeight(true);
        tabelaClientes.getTableHeader().setReorderingAllowed(false);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane rolagem = new JScrollPane(tabelaClientes);
        rolagem.setBorder(BorderFactory.createTitledBorder("Clientes cadastrados"));
        rolagem.getViewport().setBackground(Color.WHITE);
        painelTabela.add(rolagem, BorderLayout.CENTER);

        statusLabel.setPreferredSize(new Dimension(1, 22));
        painelTabela.add(statusLabel, BorderLayout.SOUTH);
    }

    private void configurarJanela() {
        setContentPane(contentPane);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 650));
        setSize(1100, 720);
        setLocationRelativeTo(null);
    }

    private void configurarTabela() {
        // A tabela já é configurada em construirTabela().
    }

    private void inicializarBanco() {
        executarNoBanco(() -> {
            clienteDao.criarTabelaSeNecessario();
            atualizarTabela(clienteDao.listarTodos());
            mostrarStatus("Pronto para cadastrar.", false);
        });
    }

    private void salvarCliente() {
        String nome = campoNome.getText().trim();
        String cpf = somenteDigitos(campoCpf.getText());
        String telefone = campoTelefone.getText().trim();
        String email = campoEmail.getText().trim().toLowerCase();
        String endereco = campoEndereco.getText().trim();

        String erro = validar(nome, cpf, telefone, email, endereco);
        if (erro != null) {
            mostrarStatus(erro, true);
            return;
        }

        executarNoBanco(() -> {
            clienteDao.salvar(new Cliente(null, nome, cpf, telefone, email, endereco));
            atualizarTabela(clienteDao.listarTodos());
            limparCampos();
            mostrarStatus("Cliente cadastrado com sucesso!", false);
        });
    }

    private String validar(String nome, String cpf, String telefone, String email, String endereco) {
        if (nome.isBlank() || cpf.isBlank() || telefone.isBlank() || email.isBlank() || endereco.isBlank()) {
            return "Preencha todos os campos obrigatórios.";
        }
        if (nome.length() < 3) {
            return "Informe o nome completo.";
        }
        if (cpf.length() != 11) {
            return "O CPF deve conter 11 números.";
        }
        if (!EMAIL_VALIDO.matcher(email).matches()) {
            return "Informe um e-mail válido.";
        }
        return null;
    }

    private void executarNoBanco(OperacaoBanco operacao) {
        botaoSalvar.setEnabled(false);
        mostrarStatus("Conectando ao banco de dados...", false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                operacao.executar();
                return null;
            }

            @Override
            protected void done() {
                botaoSalvar.setEnabled(true);
                try {
                    get();
                } catch (Exception exception) {
                    Throwable causa = exception.getCause();
                    String mensagem = causa == null ? exception.getMessage() : causa.getMessage();
                    if (causa instanceof SQLException sql && "23505".equals(sql.getSQLState())) {
                        mensagem = "Já existe um cliente com este CPF ou e-mail.";
                    }
                    mostrarStatus("Não foi possível concluir: " + mensagem, true);
                }
            }
        };
        worker.execute();
    }

    private void atualizarTabela(List<Cliente> clientes) {
        SwingUtilities.invokeLater(() -> {
            modeloTabela.setRowCount(0);
            for (Cliente cliente : clientes) {
                modeloTabela.addRow(new Object[]{
                        cliente.getId(),
                        cliente.getNome(),
                        formatarCpf(cliente.getCpf()),
                        cliente.getTelefone(),
                        cliente.getEmail(),
                        cliente.getEndereco()
                });
            }
        });
    }

    private void limparCampos() {
        SwingUtilities.invokeLater(() -> {
            campoNome.setText("");
            campoCpf.setText("");
            campoTelefone.setText("");
            campoEmail.setText("");
            campoEndereco.setText("");
            campoNome.requestFocusInWindow();
        });
    }

    private void mostrarStatus(String mensagem, boolean erro) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setForeground(erro ? new Color(184, 50, 50) : new Color(35, 125, 75));
            statusLabel.setText("<html>" + mensagem + "</html>");
        });
    }

    private String somenteDigitos(String valor) {
        return valor.replaceAll("\\D", "");
    }

    private String formatarCpf(String cpf) {
        return cpf != null && cpf.length() == 11
                ? cpf.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4")
                : cpf;
    }

    @FunctionalInterface
    private interface OperacaoBanco {
        void executar() throws Exception;
    }
}
