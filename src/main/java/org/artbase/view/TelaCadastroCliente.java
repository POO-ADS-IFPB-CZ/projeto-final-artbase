package org.artbase.view;

import org.artbase.dao.ClienteDaoJdbc;
import org.artbase.model.Cliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class TelaCadastroCliente extends JFrame {
    private static final Color FUNDO = new Color(243, 246, 250);
    private static final Color AZUL = new Color(37, 99, 235);
    private static final Color AZUL_ESCURO = new Color(29, 78, 216);
    private static final Color SELECAO_LINHA = new Color(219, 234, 254);
    private static final Color SELECAO_TEXTO = new Color(15, 23, 42);
    private static final Color CINZA_TEXTO = new Color(71, 85, 105);
    private static final Pattern EMAIL_VALIDO =
            Pattern.compile("^[\\w.!#$%&'*+/=?^`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)+$");

    private JPanel contentPane;
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

    public TelaCadastroCliente() {
        super("ArtBase - Clientes");
        montarInterface();
        configurarJanela();
        configurarTabela();
        inicializarBanco();
    }

    private void montarInterface() {
        contentPane = new JPanel(new BorderLayout(20, 20));
        contentPane.setBackground(FUNDO);
        contentPane.setBorder(new EmptyBorder(22, 24, 22, 24));

        contentPane.add(criarCabecalho(), BorderLayout.NORTH);
        contentPane.add(criarCorpo(), BorderLayout.CENTER);
    }

    private JComponent criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Gestão de Clientes");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        titulo.setForeground(new Color(15, 23, 42));

        JLabel subtitulo = new JLabel("Cadastre, busque, edite e exclua clientes com rapidez.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(CINZA_TEXTO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        cabecalho.add(textos, BorderLayout.WEST);
        return cabecalho;
    }

    private JComponent criarCorpo() {
        JPanel corpo = new JPanel(new BorderLayout(18, 0));
        corpo.setOpaque(false);
        corpo.add(criarFormulario(), BorderLayout.WEST);
        corpo.add(criarPainelLista(), BorderLayout.CENTER);
        return corpo;
    }

    private JComponent criarFormulario() {
        JPanel card = criarCard();
        card.setPreferredSize(new Dimension(390, 0));

        GridBagConstraints gbc = baseGrid();
        int linha = 0;

        adicionarTitulo(card, gbc, linha++, "Novo cliente");
        campoNome = new JTextField();
        campoCpf = new JTextField();
        campoTelefone = new JTextField();
        campoEmail = new JTextField();
        campoEndereco = new JTextArea(5, 20);
        campoEndereco.setLineWrap(true);
        campoEndereco.setWrapStyleWord(true);
        campoEndereco.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campoEndereco.setMargin(new Insets(8, 8, 8, 8));

        adicionarCampo(card, gbc, linha++, "Nome completo *", campoNome);
        adicionarCampo(card, gbc, linha++, "CPF *", campoCpf);
        adicionarCampo(card, gbc, linha++, "Telefone *", campoTelefone);
        adicionarCampo(card, gbc, linha++, "E-mail *", campoEmail);

        JScrollPane enderecoScroll = new JScrollPane(campoEndereco);
        enderecoScroll.setPreferredSize(new Dimension(0, 120));
        enderecoScroll.setBorder(new LineBorder(new Color(203, 213, 225), 1, true));
        adicionarCampo(card, gbc, linha++, "Endereço completo *", enderecoScroll);

        JPanel botoes = new JPanel(new GridLayout(1, 2, 10, 0));
        botoes.setOpaque(false);
        botaoSalvar = criarBotaoPrimario("Cadastrar");
        botaoSalvar.addActionListener(event -> salvarOuAtualizar());
        botaoLimpar = criarBotaoSecundario("Limpar");
        botaoLimpar.addActionListener(event -> limparFormulario());
        botoes.add(botaoSalvar);
        botoes.add(botaoLimpar);

        gbc.gridy = linha++;
        gbc.insets = new Insets(14, 0, 8, 0);
        card.add(botoes, gbc);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = linha;
        gbc.insets = new Insets(4, 0, 0, 0);
        card.add(statusLabel, gbc);
        return card;
    }

    private JComponent criarPainelLista() {
        JPanel card = criarCard();
        card.setLayout(new BorderLayout(14, 14));

        JPanel topo = new JPanel(new BorderLayout(10, 0));
        topo.setOpaque(false);

        JPanel busca = new JPanel(new BorderLayout(8, 0));
        busca.setOpaque(false);
        campoBuscaCpf = new JTextField();
        campoBuscaCpf.setToolTipText("Digite o CPF com ou sem pontuação");
        botaoBuscar = criarBotaoPrimario("Buscar CPF");
        botaoBuscar.addActionListener(event -> buscarPorCpf());
        busca.add(campoBuscaCpf, BorderLayout.CENTER);
        busca.add(botaoBuscar, BorderLayout.EAST);

        JPanel acoes = new JPanel(new GridLayout(1, 2, 10, 0));
        acoes.setOpaque(false);
        botaoEditar = criarBotaoSecundario("Editar selecionado");
        botaoEditar.addActionListener(event -> carregarSelecionadoParaEdicao());
        botaoExcluir = criarBotaoPerigoso("Excluir selecionado");
        botaoExcluir.addActionListener(event -> excluirSelecionado());
        acoes.add(botaoEditar);
        acoes.add(botaoExcluir);

        topo.add(busca, BorderLayout.CENTER);
        topo.add(acoes, BorderLayout.SOUTH);

        tabelaClientes = new JTable(modeloTabela);
        JScrollPane rolagem = new JScrollPane(tabelaClientes);
        rolagem.setBorder(BorderFactory.createTitledBorder("Clientes cadastrados"));

        card.add(topo, BorderLayout.NORTH);
        card.add(rolagem, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(18, 18, 18, 18)
        ));
        return card;
    }

    private GridBagConstraints baseGrid() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        return gbc;
    }

    private void adicionarTitulo(JPanel painel, GridBagConstraints gbc, int linha, String texto) {
        JLabel titulo = new JLabel(texto);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(new Color(15, 23, 42));
        gbc.gridy = linha;
        gbc.insets = new Insets(0, 0, 12, 0);
        painel.add(titulo, gbc);
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, String labelTexto, Component campo) {
        JPanel grupo = new JPanel(new BorderLayout(0, 5));
        grupo.setOpaque(false);

        JLabel label = new JLabel(labelTexto);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(CINZA_TEXTO);
        grupo.add(label, BorderLayout.NORTH);
        grupo.add(campo, BorderLayout.CENTER);

        gbc.gridy = linha;
        gbc.insets = new Insets(0, 0, 11, 0);
        gbc.ipady = campo instanceof JScrollPane ? 0 : 7;
        painel.add(grupo, gbc);
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(AZUL);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        return botao;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(new Color(226, 232, 240));
        botao.setForeground(new Color(15, 23, 42));
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        return botao;
    }

    private JButton criarBotaoPerigoso(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(new Color(220, 38, 38));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        return botao;
    }

    private void configurarJanela() {
        setContentPane(contentPane);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        setSize(1280, 820);
        setLocationRelativeTo(null);
    }

    private void configurarTabela() {
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
