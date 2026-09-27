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

        
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos de la categoría"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Nombre de categoría:"), gbc);
        gbc.gridx = 1;
        txtNombreCategoria = new JTextField(18);
        panelForm.add(txtNombreCategoria, gbc);

        add(panelForm, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre de categoría"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCategorias = new JTable(modeloTabla);
        tblCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCategorias.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblCategorias.getSelectedRow() != -1) {
                cargarSeleccionEnFormulario();
            }
        });
        add(new JScrollPane(tblCategorias), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardar());
        btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editar());
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setForeground(Color.RED.darker());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiar());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        add(panelBotones, BorderLayout.SOUTH);
    }

        private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<CategoriaBeans> categorias = categoriaDatos.listarTodos();
        for (CategoriaBeans c : categorias) {
            modeloTabla.addRow(new Object[]{c.getIdCategoria(), c.getNombreCategoria()});
        }
    }

    private void cargarSeleccionEnFormulario() {
        int fila = tblCategorias.getSelectedRow();
        idCategoriaSeleccionada = (int) modeloTabla.getValueAt(fila, 0);
        txtNombreCategoria.setText(String.valueOf(modeloTabla.getValueAt(fila, 1)));
        habilitarBotonesEdicion(true);
    }

    private void habilitarBotonesEdicion(boolean habilitar) {
        btnEditar.setEnabled(habilitar);
        btnEliminar.setEnabled(habilitar);
    }

    private void guardar() {
        if (!validar()) {
            return;
        }
        CategoriaBeans c = new CategoriaBeans(txtNombreCategoria.getText().trim());
        if (categoriaDatos.insertar(c)) {
            JOptionPane.showMessageDialog(this, "Categoría registrada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarTabla();
        }
    }

    private void editar() {
        if (idCategoriaSeleccionada == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una categoría de la tabla para editarla.",
                    "Ningún registro seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validar()) {
            return;
        }
        CategoriaBeans c = new CategoriaBeans(idCategoriaSeleccionada, txtNombreCategoria.getText().trim());
        if (categoriaDatos.actualizar(c)) {
            JOptionPane.showMessageDialog(this, "Categoría actualizada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarTabla();
        }
    }

    private void eliminar() {
        if (idCategoriaSeleccionada == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una categoría de la tabla para eliminarla.",
                    "Ningún registro seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar la categoría seleccionada?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (categoriaDatos.eliminar(idCategoriaSeleccionada)) {
                JOptionPane.showMessageDialog(this, "Categoría eliminada correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiar();
                cargarTabla();
            }
        }
    }

        private void limpiar() {
        txtNombreCategoria.setText("");
        idCategoriaSeleccionada = 0;
        tblCategorias.clearSelection();
        habilitarBotonesEdicion(false);
        txtNombreCategoria.requestFocus();
    }

    private boolean validar() {
        String nombre = txtNombreCategoria.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la categoría es obligatorio.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtNombreCategoria.requestFocus();
            return false;
        }
        if (nombre.length() < 3) {
            JOptionPane.showMessageDialog(this, "El nombre de la categoría debe tener al menos 3 caracteres.",
                    "Valor inválido", JOptionPane.WARNING_MESSAGE);
            txtNombreCategoria.requestFocus();
            return false;
        }
        return true;
    }
}
