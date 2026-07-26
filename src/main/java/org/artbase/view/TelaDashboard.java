package org.artbase.view;

import org.artbase.dao.ClienteDaoJdbc;
import org.artbase.dao.ProdutoDaoJdbc;
import org.artbase.dao.VendaDaoJdbc;
import org.artbase.model.Cliente;
import org.artbase.model.Produto;
import org.artbase.model.Venda;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Tela de Dashboard: primeira tela exibida após um login bem-sucedido.
 * Funciona como o "hub" central do sistema, reunindo um resumo dos
 * principais indicadores (clientes, produtos, vendas e faturamento) e
 * dando acesso, a partir de um único lugar, a todas as outras telas que
 * o usuário logado tem permissão de usar.
 *
 * Construída inteiramente em código (sem depender de um arquivo .form),
 * para não correr o risco de abrir com o painel de conteúdo vazio caso
 * o projeto seja compilado fora do GUI Designer do IntelliJ.
 */
public class TelaDashboard extends JFrame {

    private static final Color VERDE = new Color(22, 163, 74);
    private static final Color LARANJA = new Color(217, 119, 6);
    private static final Color ROXO = new Color(124, 58, 237);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String nomeUsuario;
    private final boolean admin;

    private final ClienteDaoJdbc clienteDao = new ClienteDaoJdbc();
    private final ProdutoDaoJdbc produtoDao = new ProdutoDaoJdbc();
    private final VendaDaoJdbc vendaDao = new VendaDaoJdbc();

    private JLabel labelTotalClientes;
    private JLabel labelTotalProdutos;
    private JLabel labelTotalVendas;
    private JLabel labelFaturamento;
    private JLabel labelStatus;

    private DefaultTableModel modeloUltimasVendas;
    private DefaultTableModel modeloEstoqueBaixo;

    public TelaDashboard(String nomeUsuario, boolean admin) {
        super("ArtBase - Painel");
        this.nomeUsuario = (nomeUsuario == null || nomeUsuario.isBlank()) ? "Usuário" : nomeUsuario;
        this.admin = admin;

        montarInterface();
        setJMenuBar(NavegacaoUtil.criarMenuBar(this, this.nomeUsuario, admin, NavegacaoUtil.Origem.DASHBOARD));

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setSize(1200, 780);
        setLocationRelativeTo(null);

        carregarDados();
    }

    private void montarInterface() {
        JPanel raiz = EstiloTelaPadrao.criarPainelRaiz();
        setContentPane(raiz);

        raiz.add(montarCabecalho(), BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.add(montarCartoesResumo());
        centro.add(Box.createVerticalStrut(18));
        centro.add(montarAtalhos());
        centro.add(Box.createVerticalStrut(18));
        centro.add(montarPainelListas());

        JScrollPane scroll = new JScrollPane(centro);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        raiz.add(scroll, BorderLayout.CENTER);

        labelStatus = new JLabel(" ");
        labelStatus.setForeground(EstiloTelaPadrao.TEXTO_SUAVE);
        raiz.add(labelStatus, BorderLayout.SOUTH);
    }

    private JPanel montarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);

        String perfil = admin ? "Administrador" : "Usuário";
        cabecalho.add(EstiloTelaPadrao.criarCabecalho(
                "Painel geral",
                "Bem-vindo(a), " + nomeUsuario + " · Perfil: " + perfil
        ), BorderLayout.WEST);
        return cabecalho;
    }

    private JPanel montarCartoesResumo() {
        JPanel painel = new JPanel(new GridLayout(1, 4, 16, 0));
        painel.setOpaque(false);
        painel.setPreferredSize(new Dimension(10, 120));
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        labelTotalClientes = new JLabel("--");
        labelTotalProdutos = new JLabel("--");
        labelTotalVendas = new JLabel("--");
        labelFaturamento = new JLabel("--");

        painel.add(criarCartao("Clientes cadastrados", labelTotalClientes, EstiloTelaPadrao.AZUL));
        painel.add(criarCartao("Produtos cadastrados", labelTotalProdutos, ROXO));
        painel.add(criarCartao("Vendas realizadas", labelTotalVendas, VERDE));
        painel.add(criarCartao("Faturamento total", labelFaturamento, LARANJA));

        return painel;
    }

    private JPanel criarCartao(String titulo, JLabel valorLabel, Color destaque) {
        JPanel card = EstiloTelaPadrao.criarCard(new BorderLayout(0, 8));

        JPanel faixa = new JPanel();
        faixa.setBackground(destaque);
        faixa.setPreferredSize(new Dimension(100, 4));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        labelTitulo.setForeground(EstiloTelaPadrao.TEXTO_SUAVE);

        valorLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        valorLabel.setForeground(EstiloTelaPadrao.TEXTO);

        JPanel textoWrapper = new JPanel(new BorderLayout());
        textoWrapper.setOpaque(false);
        textoWrapper.add(labelTitulo, BorderLayout.NORTH);
        textoWrapper.add(valorLabel, BorderLayout.CENTER);

        card.add(faixa, BorderLayout.NORTH);
        card.add(textoWrapper, BorderLayout.CENTER);
        return card;
    }

    private JPanel montarAtalhos() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        painel.setOpaque(false);

        JButton btnClientes = criarBotaoAtalho("Gerenciar clientes", EstiloTelaPadrao.AZUL);
        btnClientes.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new TelaCadastroCliente(nomeUsuario, admin).setVisible(true));
        });
        painel.add(btnClientes);

        if (admin) {
            JButton btnProdutos = criarBotaoAtalho("Gerenciar produtos", ROXO);
            btnProdutos.addActionListener(e -> {
                dispose();
                SwingUtilities.invokeLater(() -> new TelaProduto(nomeUsuario, admin).setVisible(true));
            });
            painel.add(btnProdutos);

            JButton btnVendas = criarBotaoAtalho("Registrar venda", VERDE);
            btnVendas.addActionListener(e -> {
                dispose();
                SwingUtilities.invokeLater(() -> new TelaVenda(nomeUsuario, admin).setVisible(true));
            });
            painel.add(btnVendas);
        }

        JButton btnAtualizar = criarBotaoAtalho("Atualizar dados", new Color(100, 116, 139));
        btnAtualizar.addActionListener(e -> carregarDados());
        painel.add(btnAtualizar);

        return painel;
    }

    private JButton criarBotaoAtalho(String texto, Color cor) {
        JButton botao = new JButton(texto);
        EstiloTelaPadrao.estilizarBotaoPrimario(botao);
        botao.setBackground(cor);
        return botao;
    }

    private JPanel montarPainelListas() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 16, 0));
        painel.setOpaque(false);
        painel.setPreferredSize(new Dimension(10, 320));
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        modeloUltimasVendas = new DefaultTableModel(
                new String[]{"ID", "Cliente", "Data", "Forma pgto.", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        painel.add(criarCardTabela("Últimas vendas", modeloUltimasVendas));

        modeloEstoqueBaixo = new DefaultTableModel(
                new String[]{"Produto", "Categoria", "Disponível", "Mínimo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        painel.add(criarCardTabela("Produtos com estoque baixo", modeloEstoqueBaixo));

        return painel;
    }

    private JPanel criarCardTabela(String titulo, DefaultTableModel modelo) {
        JPanel card = EstiloTelaPadrao.criarCard(new BorderLayout(0, 10));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        labelTitulo.setForeground(EstiloTelaPadrao.TEXTO);

        JTable tabela = new JTable(modelo);
        EstiloTelaPadrao.estilizarTabela(tabela);
        tabela.setRowHeight(26);

        card.add(labelTitulo, BorderLayout.NORTH);
        card.add(EstiloTelaPadrao.criarScroll(tabela), BorderLayout.CENTER);
        return card;
    }

    /**
     * Garante que as tabelas do banco existem (cliente antes de venda,
     * produto antes de item_venda) e busca, em segundo plano, todos os
     * dados necessários para preencher os cartões e as duas tabelas do
     * painel. É chamado ao abrir a tela e também pelo botão "Atualizar
     * dados", já que os números mudam conforme o uso do sistema.
     */
    private void carregarDados() {
        labelStatus.setText("Carregando dados...");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private List<Cliente> clientes;
            private List<Produto> produtos;
            private List<Venda> vendas;

            @Override
            protected Void doInBackground() throws Exception {
                clienteDao.criarTabelaSeNecessario();
                produtoDao.criarTabelaSeNecessario();
                vendaDao.criarTabelaSeNecessario();

                clientes = clienteDao.listarTodos();
                produtos = produtoDao.listarTodos();
                vendas = vendaDao.listarTodas();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    preencherResumo(clientes, produtos, vendas);
                    labelStatus.setText("Dados atualizados.");
                } catch (Exception ex) {
                    Throwable causa = ex.getCause();
                    String mensagem = causa == null ? ex.getMessage() : causa.getMessage();
                    labelStatus.setText("Não foi possível carregar os dados: " + mensagem);
                }
            }
        };
        worker.execute();
    }

    private void preencherResumo(List<Cliente> clientes, List<Produto> produtos, List<Venda> vendas) {
        labelTotalClientes.setText(String.valueOf(clientes.size()));
        labelTotalProdutos.setText(String.valueOf(produtos.size()));
        labelTotalVendas.setText(String.valueOf(vendas.size()));

        double faturamentoTotal = vendas.stream().mapToDouble(Venda::getValorTotal).sum();
        labelFaturamento.setText("R$ " + formatarValor(faturamentoTotal));

        modeloUltimasVendas.setRowCount(0);
        vendas.stream().limit(8).forEach(venda -> modeloUltimasVendas.addRow(new Object[]{
                venda.getId(),
                venda.getClienteNome(),
                venda.getDataDaVenda().format(FORMATO_DATA),
                venda.getFormaDePagamento(),
                "R$ " + formatarValor(venda.getValorTotal())
        }));

        modeloEstoqueBaixo.setRowCount(0);
        produtos.stream()
                .filter(p -> p.getQuantidadeDisponivel() <= p.getEstoqueMinimo())
                .forEach(p -> modeloEstoqueBaixo.addRow(new Object[]{
                        p.getNome(), p.getCategoria(), p.getQuantidadeDisponivel(), p.getEstoqueMinimo()
                }));
    }

    private String formatarValor(double valor) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaDashboard("Usuário Teste", true).setVisible(true));
    }
}
