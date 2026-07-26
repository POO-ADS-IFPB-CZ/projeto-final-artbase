import org.artbase.view.TelaAutenticacao;

import javax.swing.SwingUtilities;

/**
 * Ponto de entrada da aplicação ArtBase.
 * Sempre abre primeiro a tela de autenticação (login/registro). Só depois
 * de um login bem-sucedido é que o sistema segue para a tela de Clientes
 * e, no caso de usuários admin, dá acesso também à tela de Produtos.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaAutenticacao().setVisible(true));
    }
}
