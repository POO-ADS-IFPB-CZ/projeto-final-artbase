package org.artbase.view;

import org.artbase.controller.ProdutoController;
import org.artbase.model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * Tela responsável pelo CRUD visual de Produtos.
 * Os componentes (campos de texto, botões e tabela) vêm do arquivo
 * TelaProduto.form, montado pelo GUI Designer do IntelliJ, e são
 * automaticamente injetados nos campos abaixo através do binding do
 * próprio .form (por isso os campos não têm "new" nem inicialização
 * manual aqui). Toda a lógica de negócio fica no ProdutoController;
 * esta classe só cuida de exibir dados e reagir aos cliques do usuário.
 */
public class TelaProduto extends JDialog {

    // Painel raiz, obrigatório para o setContentPane funcionar (vem do .form)
    private JPanel contentPane;

    // Campos de entrada de dados do produto
    private JTextField textFieldNome;
    private JTextField textFieldDescricao;
    private JTextField textFieldPreco;
    private JTextField textFieldEstoque;

    // Painel que agrupa os botões de ação
    private JPanel painelBotoes;
    private JButton btnCadastrar;
    private JButton btnAtualizar;
    private JButton btnRemover;
    private JButton btnLimpar;

    // Tabela que lista os produtos cadastrados
    private JTable tabelaProdutos;

    // Controller responsável pelas regras de negócio e acesso ao banco
    private final ProdutoController controller = new ProdutoController();

    // Modelo da tabela, usado para popular e limpar as linhas exibidas
    private DefaultTableModel tableModel;

    // Guarda o id do produto atualmente selecionado na tabela (para
    // permitir atualizar/remover); fica null quando nada está selecionado
    private Integer idSelecionado;

    public TelaProduto() {
        setTitle("ArtBase - Produtos");
        setContentPane(contentPane); // Usa o painel montado no .form
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        configurarTabela();
        carregarProdutos();

        // Ao clicar em uma linha da tabela, preenche os campos com os
        // dados daquele produto, permitindo editar ou remover
        tabelaProdutos.getSelectionModel().addListSelectionListener(e -> {
            int linha = tabelaProdutos.getSelectedRow();
            if (linha >= 0) {
                preencherCamposComLinhaSelecionada(linha);
            }
        });

        btnCadastrar.addActionListener(e -> cadastrarProduto());
        btnAtualizar.addActionListener(e -> atualizarProduto());
        btnRemover.addActionListener(e -> removerProduto());
        btnLimpar.addActionListener(e -> limparCampos());
    }

    /**
     * Define as colunas da tabela e associa o modelo a ela.
     * Usa um DefaultTableModel "somente leitura" (isCellEditable retorna
     * false) para impedir que o usuário edite os valores direto na
     * tabela; toda edição deve passar pelos campos de texto e pelo
     * botão Atualizar.
     */
    private void configurarTabela() {
        tableModel = new DefaultTableModel(new Object[]{"Id", "Nome", "Descrição", "Preço", "Estoque"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos.setModel(tableModel);
    }

    /**
     * Busca todos os produtos no banco através do Controller e recarrega
     * as linhas da tabela. Chamado na abertura da tela e sempre que um
     * cadastro, atualização ou remoção é concluído, para manter a lista
     * sincronizada com o banco de dados.
     */
    private void carregarProdutos() {
        try {
            tableModel.setRowCount(0); // Limpa as linhas antes de repopular
            List<Produto> produtos = controller.listarTodos();
            for (Produto p : produtos) {
                tableModel.addRow(new Object[]{
                        p.getId(), p.getNome(), p.getDescricao(), p.getPreco(), p.getEstoque()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar produtos: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lê os dados da linha clicada na tabela e preenche os campos de
     * texto com eles, além de guardar o id do produto selecionado.
     */
    private void preencherCamposComLinhaSelecionada(int linha) {
        idSelecionado = (Integer) tableModel.getValueAt(linha, 0);
        textFieldNome.setText(String.valueOf(tableModel.getValueAt(linha, 1)));
        textFieldDescricao.setText(String.valueOf(tableModel.getValueAt(linha, 2)));
        textFieldPreco.setText(String.valueOf(tableModel.getValueAt(linha, 3)));
        textFieldEstoque.setText(String.valueOf(tableModel.getValueAt(linha, 4)));
    }

    /**
     * Lê os campos de texto, converte para os tipos corretos e chama o
     * Controller para cadastrar um novo produto. Qualquer erro de
     * validação (vindo do Controller) ou de conversão de número é
     * exibido ao usuário em um JOptionPane, sem derrubar a aplicação.
     */
    private void cadastrarProduto() {
        try {
            double preco = Double.parseDouble(textFieldPreco.getText().replace(",", "."));
            int estoque = Integer.parseInt(textFieldEstoque.getText());

            controller.cadastrar(textFieldNome.getText(), textFieldDescricao.getText(), preco, estoque);

            JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
            limparCampos();
            carregarProdutos();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Preço e estoque devem ser números válidos.",
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar produto: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Atualiza o produto atualmente selecionado na tabela com os novos
     * valores digitados nos campos. Exige que um produto tenha sido
     * selecionado antes (idSelecionado != null).
     */
    private void atualizarProduto() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um produto na tabela para atualizar.",
                    "Nenhum produto selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double preco = Double.parseDouble(textFieldPreco.getText().replace(",", "."));
            int estoque = Integer.parseInt(textFieldEstoque.getText());

            controller.atualizar(idSelecionado, textFieldNome.getText(), textFieldDescricao.getText(), preco, estoque);

            JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!");
            limparCampos();
            carregarProdutos();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Preço e estoque devem ser números válidos.",
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar produto: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Remove o produto atualmente selecionado na tabela, após confirmar
     * a ação com o usuário para evitar exclusões acidentais.
     */
    private void removerProduto() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um produto na tabela para remover.",
                    "Nenhum produto selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover este produto?",
                "Confirmar remoção", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                controller.remover(idSelecionado);
                JOptionPane.showMessageDialog(this, "Produto removido com sucesso!");
                limparCampos();
                carregarProdutos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao remover produto: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Limpa todos os campos de texto e desmarca o produto selecionado,
     * deixando a tela pronta para um novo cadastro.
     */
    private void limparCampos() {
        textFieldNome.setText("");
        textFieldDescricao.setText("");
        textFieldPreco.setText("");
        textFieldEstoque.setText("");
        idSelecionado = null;
        tabelaProdutos.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaProduto().setVisible(true));
    }
}
