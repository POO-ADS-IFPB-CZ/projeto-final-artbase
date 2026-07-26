package org.artbase.view;

import javax.swing.*;
import java.awt.*;

/**
 * Classe utilitária responsável por montar, de forma padronizada, a barra
 * de navegação (JMenuBar) usada em todas as telas do sistema (Painel,
 * Clientes, Produtos e Vendas).
 *
 * Isso garante que, de qualquer tela, o usuário consiga chegar em
 * qualquer outra tela permitida pelo seu perfil, sem precisar voltar até
 * a tela de login. A navegação segue o padrão "substituição de janela":
 * ao clicar em um item do menu, a janela atual é fechada (dispose) e a
 * nova é aberta, evitando acumular várias janelas abertas ao mesmo tempo.
 */
public final class NavegacaoUtil {

    private NavegacaoUtil() {
        // Classe utilitária: não deve ser instanciada
    }

    /**
     * Monta a barra de menu de navegação para a janela informada.
     *
     * @param janelaAtual  a janela (JFrame ou JDialog) que receberá o menu
     *                      e que será fechada ao navegar para outra tela
     * @param nomeUsuario  nome do usuário logado, exibido no menu
     * @param admin        se true, libera acesso às telas de Produtos e Vendas
     * @param origem       identifica a tela atual, para não exibir um link
     *                      redundante para ela mesma
     */
    public static JMenuBar criarMenuBar(Window janelaAtual, String nomeUsuario, boolean admin, Origem origem) {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuNavegar = new JMenu("Navegar");

        if (origem != Origem.DASHBOARD) {
            JMenuItem itemPainel = new JMenuItem("Painel (Dashboard)");
            itemPainel.addActionListener(e -> navegarPara(janelaAtual,
                    () -> new TelaDashboard(nomeUsuario, admin).setVisible(true)));
            menuNavegar.add(itemPainel);
        }

        if (origem != Origem.CLIENTES) {
            JMenuItem itemClientes = new JMenuItem("Clientes");
            itemClientes.addActionListener(e -> navegarPara(janelaAtual,
                    () -> new TelaCadastroCliente(nomeUsuario, admin).setVisible(true)));
            menuNavegar.add(itemClientes);
        }

        if (admin && origem != Origem.PRODUTOS) {
            JMenuItem itemProdutos = new JMenuItem("Produtos");
            itemProdutos.addActionListener(e -> navegarPara(janelaAtual,
                    () -> new TelaProduto(nomeUsuario, admin).setVisible(true)));
            menuNavegar.add(itemProdutos);
        }

        if (admin && origem != Origem.VENDAS) {
            JMenuItem itemVendas = new JMenuItem("Registrar venda");
            itemVendas.addActionListener(e -> navegarPara(janelaAtual,
                    () -> new TelaVenda(nomeUsuario, admin).setVisible(true)));
            menuNavegar.add(itemVendas);
        }

        menuBar.add(menuNavegar);

        JMenu menuConta = new JMenu(nomeUsuario == null || nomeUsuario.isBlank() ? "Conta" : nomeUsuario);
        JMenuItem itemSair = new JMenuItem("Sair (logout)");
        itemSair.addActionListener(e -> navegarPara(janelaAtual,
                () -> new TelaAutenticacao().setVisible(true)));
        menuConta.add(itemSair);
        menuBar.add(menuConta);

        return menuBar;
    }

    /**
     * Fecha a janela atual e executa a ação que abre a próxima tela,
     * sempre a partir da thread de eventos do Swing (EDT).
     */
    private static void navegarPara(Window janelaAtual, Runnable abrirProximaTela) {
        janelaAtual.dispose();
        SwingUtilities.invokeLater(abrirProximaTela);
    }

    /**
     * Identifica qual tela está solicitando o menu, para que o próprio
     * link dela não apareça na lista de navegação.
     */
    public enum Origem {
        DASHBOARD, CLIENTES, PRODUTOS, VENDAS
    }
}
