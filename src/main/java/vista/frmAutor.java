package vista;

import beans.AutorBeans;
import datos.AutorDatos;
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
 * Formulario secundario (JFrame) para el CRUD de Autores.
 * Al cerrarse, refresca el combo de autores del formulario principal.
 */
public class frmAutor extends JFrame {

    private final AutorDatos autorDatos = new AutorDatos();
    private final frmBiblioteca padre;

    private JTextField txtNombre;
    private JTextField txtNacionalidad;
    private JTable tblAutores;
    private DefaultTableModel modeloTabla;
    private JButton btnGuardar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private int idAutorSeleccionado = 0;

    public frmAutor(frmBiblioteca padre) {
        this.padre = padre;
        initComponents();
        cargarTabla();
        habilitarBotonesEdicion(false);
    }

    private void initComponents() {
        setTitle("Gestión de Autores");
        setSize(560, 420);
        setLocationRelativeTo(padre);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (padre != null) {
                    padre.cargarAutoresEnCombo();
                }
            }
        });

        // ---- Panel formulario ----
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del autor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(18);
        panelForm.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Nacionalidad:"), gbc);
        gbc.gridx = 1;
        txtNacionalidad = new JTextField(18);
        panelForm.add(txtNacionalidad, gbc);

        add(panelForm, BorderLayout.NORTH);

        // ---- Tabla ----
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Nacionalidad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblAutores = new JTable(modeloTabla);
        tblAutores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAutores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblAutores.getSelectedRow() != -1) {
                cargarSeleccionEnFormulario();
            }
        });
        add(new JScrollPane(tblAutores), BorderLayout.CENTER);

        // ---- Botones ----
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
        List<AutorBeans> autores = autorDatos.listarTodos();
        for (AutorBeans a : autores) {
            modeloTabla.addRow(new Object[]{a.getIdAutor(), a.getNombre(), a.getNacionalidad()});
        }
    }

    private void cargarSeleccionEnFormulario() {
        int fila = tblAutores.getSelectedRow();
        idAutorSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText(String.valueOf(modeloTabla.getValueAt(fila, 1)));
        txtNacionalidad.setText(String.valueOf(modeloTabla.getValueAt(fila, 2)));
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
        AutorBeans a = new AutorBeans(txtNombre.getText().trim(), txtNacionalidad.getText().trim());
        if (autorDatos.insertar(a)) {
            JOptionPane.showMessageDialog(this, "Autor registrado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarTabla();
        }
    }

    private void editar() {
        if (idAutorSeleccionado == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un autor de la tabla para editarlo.",
                    "Ningún registro seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validar()) {
            return;
        }
        AutorBeans a = new AutorBeans(idAutorSeleccionado, txtNombre.getText().trim(),
                txtNacionalidad.getText().trim());
        if (autorDatos.actualizar(a)) {
            JOptionPane.showMessageDialog(this, "Autor actualizado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            cargarTabla();
        }
    }

    private void eliminar() {
        if (idAutorSeleccionado == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un autor de la tabla para eliminarlo.",
                    "Ningún registro seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar el autor seleccionado?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (autorDatos.eliminar(idAutorSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Autor eliminado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiar();
                cargarTabla();
            }
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtNacionalidad.setText("");
        idAutorSeleccionado = 0;
        tblAutores.clearSelection();
        habilitarBotonesEdicion(false);
        txtNombre.requestFocus();
    }

    private boolean validar() {
        String nombre = txtNombre.getText().trim();
        String nacionalidad = txtNacionalidad.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del autor es obligatorio.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        if (nombre.length() < 3) {
            JOptionPane.showMessageDialog(this, "El nombre debe tener al menos 3 caracteres.",
                    "Valor inválido", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        if (nacionalidad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La nacionalidad es obligatoria.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtNacionalidad.requestFocus();
            return false;
        }
        return true;
    }
}
