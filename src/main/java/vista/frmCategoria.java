package vista;

import beans.CategoriaBeans;
import datos.CategoriaDatos;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Formulario secundario (JFrame) para el CRUD de Categorías literarias.
 * Al cerrarse, refresca el combo de categorías del formulario principal.
 */
public class frmCategoria extends JFrame {

    private final CategoriaDatos categoriaDatos = new CategoriaDatos();
    private final frmBiblioteca padre;

    private JTextField txtNombreCategoria;
    private JTable tblCategorias;
    private DefaultTableModel modeloTabla;
    private JButton btnGuardar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private int idCategoriaSeleccionada = 0;

    public frmCategoria(frmBiblioteca padre) {
        this.padre = padre;
        initComponents();
        cargarTabla();
        habilitarBotonesEdicion(false);
    }

    private void initComponents() {
        setTitle("Gestión de Categorías Literarias");
        setSize(520, 400);
        setLocationRelativeTo(padre);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (padre != null) {
                    padre.cargarCategoriasEnCombo();
                }
            }
        });
