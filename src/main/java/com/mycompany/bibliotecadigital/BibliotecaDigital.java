package com.mycompany.bibliotecadigital;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import vista.frmBiblioteca;

/**
 * Punto de entrada de la aplicación.
 * Universidad Don Bosco - POO404 - Desafío Práctico #02
 */
public class BibliotecaDigital {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Si falla, se usa el look and feel por defecto de Java (no crítico)
        }

        SwingUtilities.invokeLater(() -> {
            frmBiblioteca principal = new frmBiblioteca();
            principal.setVisible(true);
        });
    }
}

