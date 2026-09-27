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
