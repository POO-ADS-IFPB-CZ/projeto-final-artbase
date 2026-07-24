

import org.artbase.view.TelaCadastroCliente;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Mantém o tema padrão do Swing caso o tema do sistema não esteja disponível.
            }
            new TelaCadastroCliente().setVisible(true);
        });
    }
}
