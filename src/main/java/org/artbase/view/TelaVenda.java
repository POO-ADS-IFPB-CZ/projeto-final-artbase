package org.artbase.view;

import org.artbase.controller.ProdutoController;
import org.artbase.controller.VendaController;
import org.artbase.dao.ClienteDaoJdbc;
import org.artbase.model.Cliente;
import org.artbase.model.ItemVenda;
import org.artbase.model.Produto;
import org.artbase.model.Venda;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TelaVenda extends JDialog {
    private final VendaController vendaController = new VendaController();
    private final ProdutoController produtoController = new ProdutoController();
    private final ClienteDaoJdbc clienteDao = new ClienteDaoJdbc();
    private final String nomeUsuario;
    private final boolean admin;

    private JComboBox<String> comboCliente;
    private JComboBox<String> comboProduto;
    private JTextField campoQuantidade;
    private JComboBox<String> comboFormaPagamento;
    private JTable tabelaItens;
    private DefaultTableModel tableModelItens;
    private JLabel labelTotal;
    private JLabel statusLabel;
    private JButton btnAdicionarItem;
    private JButton btnFinalizarVenda;
    private JButton btnCancelar;

    private List<Cliente> clientesDisponiveis = new ArrayList<>();
    private List<Produto> produtosDisponiveis = new ArrayList<>();
    private final List<ItemVenda> itensDaVendaAtual = new ArrayList<>();

    public TelaVenda() {
        this("Usuário", true);
    }

    public TelaVenda(String nomeUsuario, boolean admin) {
        this.nomeUsuario = (nomeUsuario == null || nomeUsuario.isBlank()) ? "Usuário" : nomeUsuario;
        this.admin = admin;

        setTitle("ArtBase - Registrar venda");
        setContentPane(montarInterface());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setJMenuBar(NavegacaoUtil.criarMenuBar(this, this.nomeUsuario, admin, NavegacaoUtil.Origem.VENDAS));

        configurarTabelaItens();
        configurarAcoes();
        inicializarDados();
    }

    private JPanel montarInterface() {
        JPanel raiz = EstiloTelaPadrao.criarPainelRaiz();
        raiz.add(EstiloTelaPadrao.criarCabecalho(
                "Registro de vendas",
                "Monte a venda em etapas com o mesmo layout-base da tela de clientes."
        ), BorderLayout.NORTH);

        JPanel corpo = new JPanel(new GridLayout(1, 2, 18, 0));
        corpo.setOpaque(false);
        corpo.add(montarPainelVenda());
        corpo.add(montarPainelCarrinho());
        raiz.add(corpo, BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(EstiloTelaPadrao.TEXTO_SUAVE);
        raiz.add(statusLabel, BorderLayout.SOUTH);
        return raiz;
    }

    private JPanel montarPainelVenda() {
        JPanel card = EstiloTelaPadrao.criarCard(new BorderLayout(0, 16));
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 0);

        comboCliente = new JComboBox<>();
        comboProduto = new JComboBox<>();
        campoQuantidade = new JTextField();
        comboFormaPagamento = new JComboBox<>(new String[]{"Dinheiro", "Pix", "Cartão de crédito", "Cartão de débito"});

        EstiloTelaPadrao.estilizarCombo(comboCliente);
        EstiloTelaPadrao.estilizarCombo(comboProduto);
        EstiloTelaPadrao.estilizarCampo(campoQuantidade);
        EstiloTelaPadrao.estilizarCombo(comboFormaPagamento);

        adicionarCampo(campos, gbc, "Cliente", comboCliente);
        adicionarCampo(campos, gbc, "Produto", comboProduto);
        adicionarCampo(campos, gbc, "Quantidade", campoQuantidade);
        adicionarCampo(campos, gbc, "Forma de pagamento", comboFormaPagamento);

        btnAdicionarItem = new JButton("Adicionar item");
        EstiloTelaPadrao.estilizarBotaoPrimario(btnAdicionarItem);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.add(btnAdicionarItem);

        card.add(campos, BorderLayout.CENTER);
        card.add(acoes, BorderLayout.SOUTH);
        return card;
    }

    private JPanel montarPainelCarrinho() {
        JPanel card = EstiloTelaPadrao.criarCard(new BorderLayout(0, 12));

        JLabel titulo = EstiloTelaPadrao.criarSubtitulo("Itens da venda");
        titulo.setForeground(EstiloTelaPadrao.TEXTO);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));

        tabelaItens = new JTable();
        card.add(titulo, BorderLayout.NORTH);
        card.add(EstiloTelaPadrao.criarScroll(tabelaItens), BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout(12, 0));
        rodape.setOpaque(false);
        labelTotal = new JLabel("R$ 0,00");
        labelTotal.setFont(new Font("SansSerif", Font.BOLD, 22));
        labelTotal.setForeground(EstiloTelaPadrao.TEXTO);

        JPanel totalPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        totalPanel.setOpaque(false);
        JLabel subtitulo = EstiloTelaPadrao.criarSubtitulo("Total da venda");
        totalPanel.add(subtitulo);
        totalPanel.add(labelTotal);

        JPanel botoes = new JPanel(new GridLayout(1, 2, 10, 0));
        botoes.setOpaque(false);
        btnFinalizarVenda = new JButton("Finalizar venda");
        btnCancelar = new JButton("Cancelar");
        EstiloTelaPadrao.estilizarBotaoPrimario(btnFinalizarVenda);
        EstiloTelaPadrao.estilizarBotaoSecundario(btnCancelar);
        botoes.add(btnFinalizarVenda);
        botoes.add(btnCancelar);

        rodape.add(totalPanel, BorderLayout.WEST);
        rodape.add(botoes, BorderLayout.EAST);
        card.add(rodape, BorderLayout.SOUTH);
        return card;
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, String rotulo, Component campo) {
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

    private void configurarTabelaItens() {
        tableModelItens = new DefaultTableModel(
                new Object[]{"Produto", "Quantidade", "Preço unit.", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaItens.setModel(tableModelItens);
        EstiloTelaPadrao.estilizarTabela(tabelaItens);
    }

    private void configurarAcoes() {
        btnAdicionarItem.addActionListener(e -> adicionarItem());
        btnFinalizarVenda.addActionListener(e -> finalizarVenda());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void inicializarDados() {
        try {
            vendaController.garantirTabelas();
            clientesDisponiveis = clienteDao.listarTodos();
            DefaultComboBoxModel<String> modeloClientes = new DefaultComboBoxModel<>();
            for (Cliente cliente : clientesDisponiveis) {
                modeloClientes.addElement(cliente.getNome() + " (CPF " + cliente.getCpf() + ")");
            }
            comboCliente.setModel(modeloClientes);
            carregarProdutos();
            mostrarStatus("Tela pronta para registrar vendas.", false);
        } catch (Exception ex) {
            mostrarStatus("Erro ao carregar dados para a venda: " + ex.getMessage(), true);
        }
    }

    private void carregarProdutos() throws Exception {
        produtosDisponiveis = produtoController.listarTodos();
        DefaultComboBoxModel<String> modeloProdutos = new DefaultComboBoxModel<>();
        for (Produto produto : produtosDisponiveis) {
            modeloProdutos.addElement(produto.getNome()
                    + " - R$ " + formatarValor(produto.getPreco())
                    + " (estoque: " + produto.getQuantidadeDisponivel() + ")");
        }
        comboProduto.setModel(modeloProdutos);
    }

    private void adicionarItem() {
        int indiceProduto = comboProduto.getSelectedIndex();
        if (indiceProduto < 0 || produtosDisponiveis.isEmpty()) {
            mostrarStatus("Cadastre ao menos um produto antes de registrar uma venda.", true);
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(campoQuantidade.getText().trim());
            if (quantidade <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            mostrarStatus("Informe uma quantidade válida maior que zero.", true);
            return;
        }

        Produto produto = produtosDisponiveis.get(indiceProduto);
        if (quantidade > produto.getQuantidadeDisponivel()) {
            mostrarStatus("Estoque insuficiente. Disponível: " + produto.getQuantidadeDisponivel() + ".", true);
            return;
        }

        double subtotal = produto.getPreco() * quantidade;
        ItemVenda item = new ItemVenda(null, null, produto.getId(), quantidade, produto.getPreco(), subtotal);
        item.setProdutoNome(produto.getNome());
        itensDaVendaAtual.add(item);
        tableModelItens.addRow(new Object[]{
                produto.getNome(), quantidade, "R$ " + formatarValor(produto.getPreco()), "R$ " + formatarValor(subtotal)
        });

        campoQuantidade.setText("");
        atualizarTotal();
        mostrarStatus("Item adicionado à venda.", false);
    }

    private void atualizarTotal() {
        double total = itensDaVendaAtual.stream().mapToDouble(ItemVenda::getSubtotal).sum();
        labelTotal.setText("R$ " + formatarValor(total));
    }

    private void finalizarVenda() {
        int indiceCliente = comboCliente.getSelectedIndex();
        Integer clienteId = (indiceCliente >= 0 && indiceCliente < clientesDisponiveis.size())
                ? clientesDisponiveis.get(indiceCliente).getId()
                : null;
        String formaDePagamento = (String) comboFormaPagamento.getSelectedItem();

        try {
            Venda venda = vendaController.registrarVenda(clienteId, itensDaVendaAtual, formaDePagamento);
            itensDaVendaAtual.clear();
            tableModelItens.setRowCount(0);
            atualizarTotal();
            carregarProdutos();
            mostrarStatus("Venda #" + venda.getId() + " registrada com sucesso.", false);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarStatus(ex.getMessage(), true);
        } catch (Exception ex) {
            mostrarStatus("Erro ao registrar venda: " + ex.getMessage(), true);
        }
    }

    private void mostrarStatus(String mensagem, boolean erro) {
        statusLabel.setForeground(erro ? EstiloTelaPadrao.ERRO : EstiloTelaPadrao.SUCESSO);
        statusLabel.setText("<html>" + mensagem + "</html>");
    }

    private String formatarValor(double valor) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaVenda("Usuário Teste", true).setVisible(true));
    }
}
