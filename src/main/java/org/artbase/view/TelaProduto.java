package org.artbase.view;

import org.artbase.controller.ProdutoController;
import org.artbase.model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaProduto extends JDialog {
    private final ProdutoController controller = new ProdutoController();
    private final String nomeUsuario;
    private final boolean admin;

    private JTextField campoNome;
    private JTextArea campoDescricao;
    private JTextField campoPreco;
    private JTextField campoQuantidadeDisponivel;
    private JTextField campoEstoqueMinimo;
    private JTextField campoCategoria;
    private JButton btnCadastrar;
    private JButton btnAtualizar;
    private JButton btnRemover;
    private JButton btnLimpar;
    private JLabel statusLabel;
    private JTable tabelaProdutos;
    private DefaultTableModel tableModel;
    private Integer idSelecionado;

    public TelaProduto() {
        this("Usuário", true);
    }

    public TelaProduto(String nomeUsuario, boolean admin) {
        this.nomeUsuario = (nomeUsuario == null || nomeUsuario.isBlank()) ? "Usuário" : nomeUsuario;
        this.admin = admin;

        setTitle("ArtBase - Produtos");
        setContentPane(montarInterface());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setJMenuBar(NavegacaoUtil.criarMenuBar(this, this.nomeUsuario, admin, NavegacaoUtil.Origem.PRODUTOS));

        configurarTabela();
        configurarAcoes();
        inicializarBanco();
    }

    private JPanel montarInterface() {
        JPanel raiz = EstiloTelaPadrao.criarPainelRaiz();
        raiz.add(EstiloTelaPadrao.criarCabecalho(
                "Cadastro de produtos",
                "Gerencie o estoque com o mesmo padrão visual aplicado à base de clientes."
        ), BorderLayout.NORTH);

        JPanel corpo = new JPanel(new GridLayout(1, 2, 18, 0));
        corpo.setOpaque(false);
        corpo.add(montarFormulario());
        corpo.add(montarLista());
        raiz.add(corpo, BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(EstiloTelaPadrao.TEXTO_SUAVE);
        raiz.add(statusLabel, BorderLayout.SOUTH);
        return raiz;
    }

    private JPanel montarFormulario() {
        JPanel card = EstiloTelaPadrao.criarCard(new BorderLayout(0, 16));

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 12, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        campoNome = new JTextField();
        campoDescricao = new JTextArea(4, 20);
        campoPreco = new JTextField();
        campoQuantidadeDisponivel = new JTextField();
        campoEstoqueMinimo = new JTextField();
        campoCategoria = new JTextField();

        EstiloTelaPadrao.estilizarCampo(campoNome);
        EstiloTelaPadrao.estilizarArea(campoDescricao);
        EstiloTelaPadrao.estilizarCampo(campoPreco);
        EstiloTelaPadrao.estilizarCampo(campoQuantidadeDisponivel);
        EstiloTelaPadrao.estilizarCampo(campoEstoqueMinimo);
        EstiloTelaPadrao.estilizarCampo(campoCategoria);

        adicionarCampo(campos, gbc, "Nome do produto", campoNome);
        adicionarCampo(campos, gbc, "Descrição", EstiloTelaPadrao.criarScroll(campoDescricao));
        adicionarCampo(campos, gbc, "Preço", campoPreco);
        adicionarCampo(campos, gbc, "Quantidade disponível", campoQuantidadeDisponivel);
        adicionarCampo(campos, gbc, "Estoque mínimo", campoEstoqueMinimo);
        adicionarCampo(campos, gbc, "Categoria", campoCategoria);

        JPanel botoes = new JPanel(new GridLayout(1, 4, 10, 0));
        botoes.setOpaque(false);
        btnCadastrar = new JButton("Cadastrar");
        btnAtualizar = new JButton("Atualizar");
        btnRemover = new JButton("Remover");
        btnLimpar = new JButton("Limpar");
        EstiloTelaPadrao.estilizarBotaoPrimario(btnCadastrar);
        EstiloTelaPadrao.estilizarBotaoSecundario(btnAtualizar);
        EstiloTelaPadrao.estilizarBotaoPerigoso(btnRemover);
        EstiloTelaPadrao.estilizarBotaoSecundario(btnLimpar);
        botoes.add(btnCadastrar);
        botoes.add(btnAtualizar);
        botoes.add(btnRemover);
        botoes.add(btnLimpar);

        card.add(campos, BorderLayout.CENTER);
        card.add(botoes, BorderLayout.SOUTH);
        return card;
    }

    private JPanel montarLista() {
        JPanel card = EstiloTelaPadrao.criarCard(new BorderLayout(0, 12));
        JLabel titulo = EstiloTelaPadrao.criarSubtitulo("Produtos cadastrados");
        titulo.setForeground(EstiloTelaPadrao.TEXTO);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));

        tabelaProdutos = new JTable();
        card.add(titulo, BorderLayout.NORTH);
        card.add(EstiloTelaPadrao.criarScroll(tabelaProdutos), BorderLayout.CENTER);
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

    private void configurarTabela() {
        tableModel = new DefaultTableModel(
                new Object[]{"Id", "Nome", "Descrição", "Preço", "Qtd. disponível", "Estoque mínimo", "Categoria"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos.setModel(tableModel);
        EstiloTelaPadrao.estilizarTabela(tabelaProdutos);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaProdutos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int linha = tabelaProdutos.getSelectedRow();
                if (linha >= 0) {
                    preencherCamposComLinhaSelecionada(linha);
                }
            }
        });
    }

    private void configurarAcoes() {
        btnCadastrar.addActionListener(e -> cadastrarProduto());
        btnAtualizar.addActionListener(e -> atualizarProduto());
        btnRemover.addActionListener(e -> removerProduto());
        btnLimpar.addActionListener(e -> limparCampos());
    }

    private void inicializarBanco() {
        try {
            controller.garantirTabela();
            carregarProdutos();
            mostrarStatus("Sistema pronto para gerenciar produtos.", false);
        } catch (Exception ex) {
            mostrarStatus("Erro ao preparar o banco de dados: " + ex.getMessage(), true);
        }
    }

    private void carregarProdutos() {
        try {
            tableModel.setRowCount(0);
            List<Produto> produtos = controller.listarTodos();
            for (Produto p : produtos) {
                tableModel.addRow(new Object[]{
                        p.getId(), p.getNome(), p.getDescricao(), p.getPreco(),
                        p.getQuantidadeDisponivel(), p.getEstoqueMinimo(), p.getCategoria()
                });
            }
        } catch (Exception ex) {
            mostrarStatus("Erro ao carregar produtos: " + ex.getMessage(), true);
        }
    }

    private void preencherCamposComLinhaSelecionada(int linha) {
        int modelRow = tabelaProdutos.convertRowIndexToModel(linha);
        idSelecionado = (Integer) tableModel.getValueAt(modelRow, 0);
        campoNome.setText(String.valueOf(tableModel.getValueAt(modelRow, 1)));
        campoDescricao.setText(String.valueOf(tableModel.getValueAt(modelRow, 2)));
        campoPreco.setText(String.valueOf(tableModel.getValueAt(modelRow, 3)));
        campoQuantidadeDisponivel.setText(String.valueOf(tableModel.getValueAt(modelRow, 4)));
        campoEstoqueMinimo.setText(String.valueOf(tableModel.getValueAt(modelRow, 5)));
        campoCategoria.setText(String.valueOf(tableModel.getValueAt(modelRow, 6)));
        mostrarStatus("Produto selecionado para edição.", false);
    }

    private void cadastrarProduto() {
        try {
            double preco = Double.parseDouble(campoPreco.getText().trim().replace(",", "."));
            int quantidadeDisponivel = Integer.parseInt(campoQuantidadeDisponivel.getText().trim());
            int estoqueMinimo = Integer.parseInt(campoEstoqueMinimo.getText().trim());

            controller.cadastrar(campoNome.getText().trim(), campoDescricao.getText().trim(), preco,
                    quantidadeDisponivel, estoqueMinimo, campoCategoria.getText().trim());
            carregarProdutos();
            limparCampos();
            mostrarStatus("Produto cadastrado com sucesso.", false);
        } catch (NumberFormatException ex) {
            mostrarStatus("Preço, quantidade disponível e estoque mínimo devem ser números válidos.", true);
        } catch (Exception ex) {
            mostrarStatus("Erro ao cadastrar produto: " + ex.getMessage(), true);
        }
    }

    private void atualizarProduto() {
        if (idSelecionado == null) {
            mostrarStatus("Selecione um produto para atualizar.", true);
            return;
        }
        try {
            double preco = Double.parseDouble(campoPreco.getText().trim().replace(",", "."));
            int quantidadeDisponivel = Integer.parseInt(campoQuantidadeDisponivel.getText().trim());
            int estoqueMinimo = Integer.parseInt(campoEstoqueMinimo.getText().trim());

            controller.atualizar(idSelecionado, campoNome.getText().trim(), campoDescricao.getText().trim(), preco,
                    quantidadeDisponivel, estoqueMinimo, campoCategoria.getText().trim());
            carregarProdutos();
            limparCampos();
            mostrarStatus("Produto atualizado com sucesso.", false);
        } catch (NumberFormatException ex) {
            mostrarStatus("Preço, quantidade disponível e estoque mínimo devem ser números válidos.", true);
        } catch (Exception ex) {
            mostrarStatus("Erro ao atualizar produto: " + ex.getMessage(), true);
        }
    }

    private void removerProduto() {
        if (idSelecionado == null) {
            mostrarStatus("Selecione um produto para remover.", true);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja remover este produto?",
                "Confirmar remoção",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.remover(idSelecionado);
            carregarProdutos();
            limparCampos();
            mostrarStatus("Produto removido com sucesso.", false);
        } catch (Exception ex) {
            mostrarStatus("Erro ao remover produto: " + ex.getMessage(), true);
        }
    }

    private void limparCampos() {
        campoNome.setText("");
        campoDescricao.setText("");
        campoPreco.setText("");
        campoQuantidadeDisponivel.setText("");
        campoEstoqueMinimo.setText("");
        campoCategoria.setText("");
        idSelecionado = null;
        tabelaProdutos.clearSelection();
    }

    private void mostrarStatus(String mensagem, boolean erro) {
        statusLabel.setForeground(erro ? EstiloTelaPadrao.ERRO : EstiloTelaPadrao.SUCESSO);
        statusLabel.setText("<html>" + mensagem + "</html>");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaProduto("Usuário Teste", true).setVisible(true));
    }
}
