package org.artbase.view;

import org.artbase.dao.ClienteDaoJdbc;
import org.artbase.model.Cliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class TelaCadastroCliente extends JFrame {
    private static final Color FUNDO = new Color(243, 246, 250);
    private static final Color AZUL = new Color(37, 99, 235);
    private static final Color SELECAO_LINHA = new Color(219, 234, 254);
    private static final Color SELECAO_TEXTO = new Color(15, 23, 42);
    private static final Color CINZA_TEXTO = new Color(71, 85, 105);
    private static final Pattern EMAIL_VALIDO =
            Pattern.compile("^[\\w.!#$%&'*+/=?^`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)+$");

    private JPanel contentPane;
    private JPanel painelFormulario;
    private JPanel painelLista;
    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoTelefone;
    private JTextField campoEmail;
    private JTextArea campoEndereco;
    private JButton botaoSalvar;
    private JButton botaoLimpar;
    private JButton botaoEditar;
    private JButton botaoExcluir;
    private JButton botaoBuscar;
    private JTextField campoBuscaCpf;
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
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabela);
    private final ClienteDaoJdbc clienteDao = new ClienteDaoJdbc();
    private Integer clienteEditandoId;

    // Define se o usuário logado é admin; controla o acesso à tela de Produtos
    private final boolean admin;

    // Nome do usuário logado, usado no menu de navegação
    private final String nomeUsuario;

    /**
     * Construtor padrão, usado quando não se sabe (ou não importa) se o
     * usuário é admin. Mantido para não quebrar quem já chamava
     * "new TelaCadastroCliente()" em outros pontos do código.
     */
    public TelaCadastroCliente() {
        this("Usuário", false);
    }

    /**
     * Construtor mantido por compatibilidade com código antigo que só
     * informava se o usuário é admin, sem o nome.
     */
    public TelaCadastroCliente(boolean admin) {
        this("Usuário", admin);
    }

    /**
     * Construtor usado pelo Painel/Autenticação após um login bem-sucedido,
     * informando o nome do usuário logado e se ele é admin ou não.
     */
    public TelaCadastroCliente(String nomeUsuario, boolean admin) {
        super("ArtBase - Clientes");
        this.nomeUsuario = (nomeUsuario == null || nomeUsuario.isBlank()) ? "Usuário" : nomeUsuario;
        this.admin = admin;
        configurarJanela();
        configurarAparencia();
        configurarAcoes();
        configurarTabela();
        setJMenuBar(NavegacaoUtil.criarMenuBar(this, this.nomeUsuario, admin, NavegacaoUtil.Origem.CLIENTES));
        inicializarBanco();
    }

    private void configurarJanela() {
        setContentPane(contentPane);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        setSize(1280, 820);
        setLocationRelativeTo(null);
    }

    private void configurarAparencia() {
        contentPane.setBackground(FUNDO);
        contentPane.setBorder(new EmptyBorder(22, 24, 22, 24));

        estilizarCard(painelFormulario);
        estilizarCard(painelLista);

        campoEndereco.setLineWrap(true);
        campoEndereco.setWrapStyleWord(true);
        campoEndereco.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campoEndereco.setMargin(new Insets(8, 8, 8, 8));
        campoBuscaCpf.setToolTipText("Digite o CPF com ou sem pontuação");

        statusLabel.setText(" ");
        statusLabel.setForeground(CINZA_TEXTO);

        estilizarBotaoPrimario(botaoSalvar);
        estilizarBotaoPrimario(botaoBuscar);
        estilizarBotaoSecundario(botaoLimpar);
        estilizarBotaoSecundario(botaoEditar);
        estilizarBotaoPerigoso(botaoExcluir);
    }

    private void estilizarCard(JPanel card) {
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(18, 18, 18, 18)
        ));
    }

    private void estilizarBotaoPrimario(JButton botao) {
        botao.setBackground(AZUL);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorderPainted(false);
        botao.setOpaque(true);
    }

    private void estilizarBotaoSecundario(JButton botao) {
        botao.setBackground(new Color(226, 232, 240));
        botao.setForeground(new Color(15, 23, 42));
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorderPainted(false);
        botao.setOpaque(true);
    }

    private void estilizarBotaoPerigoso(JButton botao) {
        botao.setBackground(new Color(220, 38, 38));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorderPainted(false);
        botao.setOpaque(true);
    }

    private void configurarAcoes() {
        botaoSalvar.addActionListener(event -> salvarOuAtualizar());
        botaoLimpar.addActionListener(event -> limparFormulario());
        botaoBuscar.addActionListener(event -> buscarPorCpf());
        botaoEditar.addActionListener(event -> carregarSelecionadoParaEdicao());
        botaoExcluir.addActionListener(event -> excluirSelecionado());
    }

    private void configurarTabela() {
        tabelaClientes.setModel(modeloTabela);
        tabelaClientes.setRowHeight(30);
        tabelaClientes.setFillsViewportHeight(true);
        tabelaClientes.getTableHeader().setReorderingAllowed(false);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaClientes.setSelectionBackground(SELECAO_LINHA);
        tabelaClientes.setSelectionForeground(SELECAO_TEXTO);
        tabelaClientes.setRowSorter(sorter);
        tabelaClientes.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                int viewRow = tabelaClientes.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = tabelaClientes.convertRowIndexToModel(viewRow);
                    clienteEditandoId = (Integer) modeloTabela.getValueAt(modelRow, 0);
                }
            }
        });
    }

    private void inicializarBanco() {
        executarNoBanco(() -> {
            clienteDao.criarTabelaSeNecessario();
            carregarClientes();
            mostrarStatus("Sistema pronto para uso.", false);
        });
    }

    private void salvarOuAtualizar() {
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
            if (clienteEditandoId == null) {
                Cliente existente = clienteDao.buscarPorCpf(cpf);
                if (existente != null) {
                    throw new IllegalStateException("Já existe um cliente com este CPF.");
                }
                clienteDao.salvar(new Cliente(null, nome, cpf, telefone, email, endereco));
                mostrarStatus("Cliente cadastrado com sucesso.", false);
            } else {
                Cliente duplicado = clienteDao.buscarPorCpf(cpf);
                if (duplicado != null && !duplicado.getId().equals(clienteEditandoId)) {
                    throw new IllegalStateException("Já existe outro cliente com este CPF.");
                }
                clienteDao.atualizar(new Cliente(clienteEditandoId, nome, cpf, telefone, email, endereco));
                mostrarStatus("Cliente atualizado com sucesso.", false);
            }

            carregarClientes();
            limparFormulario();
        });
    }

    private void carregarClientes() throws SQLException, java.io.IOException, ClassNotFoundException {
        atualizarTabela(clienteDao.listarTodos());
    }

    private void buscarPorCpf() {
        String cpf = somenteDigitos(campoBuscaCpf.getText());
        if (cpf.isBlank()) {
            mostrarStatus("Digite um CPF para buscar.", true);
            return;
        }

        executarNoBanco(() -> {
            Cliente cliente = clienteDao.buscarPorCpf(cpf);
            SwingUtilities.invokeLater(() -> {
                modeloTabela.setRowCount(0);
                if (cliente == null) {
                    mostrarStatus("Nenhum cliente encontrado com este CPF.", true);
                    return;
                }
                adicionarClienteNaTabela(cliente);
                mostrarStatus("Cliente encontrado.", false);
            });
        });
    }

    private void carregarSelecionadoParaEdicao() {
        int viewRow = tabelaClientes.getSelectedRow();
        if (viewRow < 0) {
            mostrarStatus("Selecione um cliente na tabela para editar.", true);
            return;
        }

        int modelRow = tabelaClientes.convertRowIndexToModel(viewRow);
        Integer id = (Integer) modeloTabela.getValueAt(modelRow, 0);

        executarNoBanco(() -> {
            Cliente cliente = clienteDao.buscarPorId(id);
            if (cliente == null) {
                throw new IllegalStateException("Cliente não encontrado.");
            }
            SwingUtilities.invokeLater(() -> preencherFormulario(cliente));
        });
    }

    private void excluirSelecionado() {
        int viewRow = tabelaClientes.getSelectedRow();
        if (viewRow < 0) {
            mostrarStatus("Selecione um cliente na tabela para excluir.", true);
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja excluir o cliente selecionado?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        int modelRow = tabelaClientes.convertRowIndexToModel(viewRow);
        Integer id = (Integer) modeloTabela.getValueAt(modelRow, 0);

        executarNoBanco(() -> {
            boolean removido = clienteDao.excluir(id);
            if (!removido) {
                throw new IllegalStateException("Não foi possível excluir o cliente.");
            }
            carregarClientes();
            limparFormulario();
            mostrarStatus("Cliente excluído com sucesso.", false);
        });
    }

    private void preencherFormulario(Cliente cliente) {
        clienteEditandoId = cliente.getId();
        campoNome.setText(cliente.getNome());
        campoCpf.setText(formatarCpf(cliente.getCpf()));
        campoTelefone.setText(cliente.getTelefone());
        campoEmail.setText(cliente.getEmail());
        campoEndereco.setText(cliente.getEndereco());
        botaoSalvar.setText("Atualizar cliente");
        mostrarStatus("Editando cliente ID " + cliente.getId() + ".", false);
    }

    private void limparFormulario() {
        clienteEditandoId = null;
        campoNome.setText("");
        campoCpf.setText("");
        campoTelefone.setText("");
        campoEmail.setText("");
        campoEndereco.setText("");
        campoBuscaCpf.setText("");
        botaoSalvar.setText("Cadastrar");
        campoNome.requestFocusInWindow();
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
        setAcoesHabilitadas(false);
        mostrarStatus("Processando...", false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                operacao.executar();
                return null;
            }

            @Override
            protected void done() {
                setAcoesHabilitadas(true);
                try {
                    get();
                } catch (Exception exception) {
                    Throwable causa = exception.getCause();
                    String mensagem = causa == null ? exception.getMessage() : causa.getMessage();
                    if (causa instanceof SQLException sql && "23505".equals(sql.getSQLState())) {
                        mensagem = "CPF ou e-mail já cadastrado.";
                    }
                    mostrarStatus("Não foi possível concluir: " + mensagem, true);
                }
            }
        };
        worker.execute();
    }

    private void setAcoesHabilitadas(boolean habilitado) {
        botaoSalvar.setEnabled(habilitado);
        botaoLimpar.setEnabled(habilitado);
        botaoEditar.setEnabled(habilitado);
        botaoExcluir.setEnabled(habilitado);
        botaoBuscar.setEnabled(habilitado);
    }

    private void atualizarTabela(List<Cliente> clientes) {
        SwingUtilities.invokeLater(() -> {
            modeloTabela.setRowCount(0);
            for (Cliente cliente : clientes) {
                adicionarClienteNaTabela(cliente);
            }
        });
    }

    private void adicionarClienteNaTabela(Cliente cliente) {
        modeloTabela.addRow(new Object[]{
                cliente.getId(),
                cliente.getNome(),
                formatarCpf(cliente.getCpf()),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getEndereco()
        });
    }

    private void mostrarStatus(String mensagem, boolean erro) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setForeground(erro ? new Color(185, 28, 28) : new Color(21, 128, 61));
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
