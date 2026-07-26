package org.artbase.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

final class EstiloTelaPadrao {
    static final Color FUNDO = new Color(243, 246, 250);
    static final Color CARD = Color.WHITE;
    static final Color BORDA = new Color(226, 232, 240);
    static final Color AZUL = new Color(37, 99, 235);
    static final Color AZUL_CLARO = new Color(219, 234, 254);
    static final Color TEXTO = new Color(15, 23, 42);
    static final Color TEXTO_SUAVE = new Color(71, 85, 105);
    static final Color SUCESSO = new Color(21, 128, 61);
    static final Color ERRO = new Color(185, 28, 28);
    static final Color PERIGO = new Color(220, 38, 38);
    static final Color SECUNDARIO = new Color(226, 232, 240);

    private EstiloTelaPadrao() {
    }

    static JPanel criarPainelRaiz() {
        JPanel painel = new JPanel(new BorderLayout(0, 18));
        painel.setBackground(FUNDO);
        painel.setBorder(new EmptyBorder(22, 24, 22, 24));
        return painel;
    }

    static JPanel criarCard(LayoutManager layout) {
        JPanel card = new JPanel(layout);
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                new EmptyBorder(18, 18, 18, 18)
        ));
        return card;
    }

    static JLabel criarTitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 26));
        label.setForeground(TEXTO);
        return label;
    }

    static JLabel criarSubtitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(TEXTO_SUAVE);
        return label;
    }

    static JPanel criarCabecalho(String titulo, String subtitulo) {
        JPanel cabecalho = new JPanel(new GridLayout(2, 1, 0, 4));
        cabecalho.setOpaque(false);
        cabecalho.add(criarTitulo(titulo));
        cabecalho.add(criarSubtitulo(subtitulo));
        return cabecalho;
    }

    static void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    static void estilizarArea(JTextArea area) {
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                new EmptyBorder(8, 10, 8, 10)
        ));
        area.setMargin(new Insets(0, 0, 0, 0));
    }

    static void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
    }

    static void estilizarBotaoPrimario(AbstractButton botao) {
        estilizarBotao(botao, AZUL, Color.WHITE);
    }

    static void estilizarBotaoSecundario(AbstractButton botao) {
        estilizarBotao(botao, SECUNDARIO, TEXTO);
    }

    static void estilizarBotaoPerigoso(AbstractButton botao) {
        estilizarBotao(botao, PERIGO, Color.WHITE);
    }

    private static void estilizarBotao(AbstractButton botao, Color fundo, Color texto) {
        botao.setBackground(fundo);
        botao.setForeground(texto);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setFont(new Font("SansSerif", Font.BOLD, 13));
        botao.setBorder(new EmptyBorder(10, 16, 10, 16));
    }

    static void estilizarTabela(JTable tabela) {
        tabela.setRowHeight(30);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionBackground(AZUL_CLARO);
        tabela.setSelectionForeground(TEXTO);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setGridColor(BORDA);
        JTableHeader header = tabela.getTableHeader();
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXTO);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    static JScrollPane criarScroll(Component componente) {
        JScrollPane scroll = new JScrollPane(componente);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }
}
