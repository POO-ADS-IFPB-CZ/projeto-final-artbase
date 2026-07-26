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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Tela de registro de vendas: o admin escolhe um cliente, vai adicionando
 * produtos (com quantidade) até montar o carrinho da venda, e finaliza.
 * Ao finalizar, o VendaController grava a venda, os itens e desconta o
 * estoque dos produtos vendidos, tudo em uma única transação no banco.
 */
public class TelaVenda extends JDialog {

    private JPanel contentPane;
    private JComboBox comboCliente;
    private JComboBox comboProduto;
    private JTextField textFieldQuantidade;
    private JPanel painelAdicionar;
    private JButton btnAdicionarItem;
    private JTable tabelaItens;
    private JComboBox comboFormaPagamento;
    private JLabel labelTotal;
    private JPanel painelFinal;
    private JButton btnFinalizarVenda;
    private JButton btnCancelar;

    private final VendaController vendaController = new VendaController();
    private final ProdutoController produtoController = new ProdutoController();
    private final ClienteDaoJdbc clienteDao = new ClienteDaoJdbc();

    private DefaultTableModel tableModelItens;

    // Listas paralelas aos itens dos combos, usadas para saber qual
    // Cliente/Produto corresponde ao índice selecionado no JComboBox
    private List<Cliente> clientesDisponiveis = new ArrayList<>();
    private List<Produto> produtosDisponiveis = new ArrayList<>();

    // Itens que já foram adicionados ao "carrinho" da venda atual
    private final List<ItemVenda> itensDaVendaAtual = new ArrayList<>();

    /**
     * Construtor mantido por compatibilidade com código antigo que abria
     * a tela sem informar o usuário logado.
     */
    public TelaVenda() {
        this("Usuário", true);
    }

    /**
     * Construtor usado a partir do Painel/Clientes/Produtos, informando o
     * nome do usuário logado (usado no menu de navegação). Só é possível
     * chegar nesta tela sendo admin, mas o parâmetro é mantido para
     * manter o menu de navegação consistente com as demais telas.
     */
    public TelaVenda(String nomeUsuario, boolean admin) {
        setTitle("ArtBase - Registrar venda");
        setContentPane(contentPane);
        setSize(700, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String nome = (nomeUsuario == null || nomeUsuario.isBlank()) ? "Usuário" : nomeUsuario;
        setJMenuBar(NavegacaoUtil.criarMenuBar(this, nome, admin, NavegacaoUtil.Origem.VENDAS));

        configurarTabelaItens();
        comboFormaPagamento.setModel(new DefaultComboBoxModel<>(
                new String[]{"Dinheiro", "Pix", "Cartão de crédito", "Cartão de débito"}));

        inicializarDados();

        btnAdicionarItem.addActionListener(e -> adicionarItem());
        btnFinalizarVenda.addActionListener(e -> finalizarVenda());
        btnCancelar.addActionListener(e -> dispose());
    }

    /**
     * Define as colunas da tabela de itens da venda atual (o "carrinho").
     * É só leitura: para remover um item o usuário precisa reiniciar a
     * tela (mantido simples de propósito, já que é o fluxo de MVP).
     */
    private void configurarTabelaItens() {
        tableModelItens = new DefaultTableModel(
                new Object[]{"Produto", "Quantidade", "Preço unit.", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaItens.setModel(tableModelItens);
    }

    /**
     * Garante que as tabelas de venda existem, e carrega clientes e
     * produtos do banco para popular os dois combos da tela. Sem
     * clientes/produtos cadastrados, os combos ficam vazios e a venda
     * não pode ser adicionada (mensagem clara é mostrada ao tentar).
     */
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

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar dados para a venda: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * (Re)carrega a lista de produtos disponíveis no combo, mostrando o
     * estoque atual junto do nome. Chamado na abertura da tela e depois
     * de finalizar uma venda, já que o estoque muda a cada venda.
     */
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

    /**
     * Lê o produto e a quantidade selecionados, valida contra o estoque
     * disponível localmente (uma checagem rápida; a checagem definitiva
     * acontece no banco, dentro da transação) e adiciona uma nova linha
     * ao carrinho da venda atual.
     */
    private void adicionarItem() {
        int indiceProduto = comboProduto.getSelectedIndex();
        if (indiceProduto < 0 || produtosDisponiveis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cadastre ao menos um produto antes de registrar uma venda.",
                    "Nenhum produto disponível", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(textFieldQuantidade.getText().trim());
            if (quantidade <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe uma quantidade válida (maior que zero).",
                    "Quantidade inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Produto produto = produtosDisponiveis.get(indiceProduto);
        if (quantidade > produto.getQuantidadeDisponivel()) {
            JOptionPane.showMessageDialog(this,
                    "Estoque insuficiente. Disponível: " + produto.getQuantidadeDisponivel() + ".",
                    "Estoque insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double subtotal = produto.getPreco() * quantidade;
        ItemVenda item = new ItemVenda(null, null, produto.getId(), quantidade, produto.getPreco(), subtotal);
        item.setProdutoNome(produto.getNome());
        itensDaVendaAtual.add(item);

        tableModelItens.addRow(new Object[]{
                produto.getNome(), quantidade, "R$ " + formatarValor(produto.getPreco()), "R$ " + formatarValor(subtotal)
        });

        textFieldQuantidade.setText("");
        atualizarTotal();
    }

    /**
     * Recalcula e exibe o valor total somando o subtotal de todos os
     * itens já adicionados ao carrinho da venda atual.
     */
    private void atualizarTotal() {
        double total = itensDaVendaAtual.stream().mapToDouble(ItemVenda::getSubtotal).sum();
        labelTotal.setText("R$ " + formatarValor(total));
    }

    /**
     * Envia a venda (cliente + itens + forma de pagamento) para o
     * VendaController. Se tudo estiver certo, o banco confirma a venda,
     * desconta o estoque e a tela é reiniciada para uma nova venda.
     */
    private void finalizarVenda() {
        int indiceCliente = comboCliente.getSelectedIndex();
        Integer clienteId = (indiceCliente >= 0 && indiceCliente < clientesDisponiveis.size())
                ? clientesDisponiveis.get(indiceCliente).getId()
                : null;
        String formaDePagamento = (String) comboFormaPagamento.getSelectedItem();

        try {
            Venda venda = vendaController.registrarVenda(clienteId, itensDaVendaAtual, formaDePagamento);
            JOptionPane.showMessageDialog(this,
                    "Venda #" + venda.getId() + " registrada com sucesso! Total: R$ " + formatarValor(venda.getValorTotal()));

            itensDaVendaAtual.clear();
            tableModelItens.setRowCount(0);
            atualizarTotal();
            carregarProdutos(); // o estoque mudou, recarrega os produtos com as novas quantidades

        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Não foi possível registrar a venda", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao registrar venda: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatarValor(double valor) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaVenda("Usuário Teste", true).setVisible(true));
    }
}
