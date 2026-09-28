package vista;

import beans.AutorBeans;
import beans.CategoriaBeans;
import beans.LibroBeans;
import datos.AutorDatos;
import datos.CategoriaDatos;
import datos.LibroDatos;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.Year;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Formulario principal del sistema: administración (CRUD) de libros.
 * Permite además filtrar por autor o categoría y abrir los formularios
 * de mantenimiento de autores y categorías.
 */
public class frmBiblioteca extends JFrame {

    // ---- Capas de datos ----
    private final LibroDatos libroDatos = new LibroDatos();
    private final AutorDatos autorDatos = new AutorDatos();
    private final CategoriaDatos categoriaDatos = new CategoriaDatos();

    // ---- Componentes de captura ----
    private JTextField txtTitulo;
    private JTextField txtAnio;
    private JComboBox<AutorBeans> cbAutor;
    private JComboBox<CategoriaBeans> cbCategoria;

    // ---- Filtro ----
    private JComboBox<String> cbTipoFiltro;
    private JComboBox<Object> cbValorFiltro;

    // ---- Tabla ----
    private JTable tblLibros;
    private DefaultTableModel modeloTabla;

    // ---- Botones ----
    private JButton btnGuardar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnFiltrar;
    private JButton btnMostrarTodos;

    // Guarda el id del libro actualmente seleccionado en la tabla (0 = ninguno)
    private int idLibroSeleccionado = 0;

    public frmBiblioteca() {
        initComponents();
        cargarAutoresEnCombo();
        cargarCategoriasEnCombo();
        cargarTabla(libroDatos.listarTodos());
        habilitarBotonesEdicion(false);
    }

    // =========================================================
    // Construcción de la interfaz
    // =========================================================
    private void initComponents() {
        setTitle("Sistema de Biblioteca Digital - Gestión de Libros");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        setJMenuBar(construirMenu());
        add(construirPanelFormulario(), BorderLayout.NORTH);
        add(construirPanelTabla(), BorderLayout.CENTER);
        add(construirPanelBotones(), BorderLayout.SOUTH);
    }

    private JMenuBar construirMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuAdmin = new JMenu("Administrar");

        JMenuItem itemAutores = new JMenuItem("Gestionar Autores");
        itemAutores.addActionListener(e -> {
            frmAutor f = new frmAutor(this);
            f.setVisible(true);
        });

        JMenuItem itemCategorias = new JMenuItem("Gestionar Categorías");
        itemCategorias.addActionListener(e -> {
            frmCategoria f = new frmCategoria(this);
            f.setVisible(true);
        });

        menuAdmin.add(itemAutores);
        menuAdmin.add(itemCategorias);
        menuBar.add(menuAdmin);
        return menuBar;
    }

    private JPanel construirPanelFormulario() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // ---- Panel de datos del libro ----
        JPanel panelDatos = new JPanel(new GridBagLayout());
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos del libro"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelDatos.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1;
        txtTitulo = new JTextField(20);
        panelDatos.add(txtTitulo, gbc);

        gbc.gridx = 2;
        panelDatos.add(new JLabel("Año de publicación:"), gbc);
        gbc.gridx = 3;
        txtAnio = new JTextField(8);
        panelDatos.add(txtAnio, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelDatos.add(new JLabel("Autor:"), gbc);
        gbc.gridx = 1;
        cbAutor = new JComboBox<>();
        cbAutor.setEditable(true); // se puede escribir un autor nuevo o elegir uno existente
        panelDatos.add(cbAutor, gbc);

        gbc.gridx = 2;
        panelDatos.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 3;
        cbCategoria = new JComboBox<>();
        panelDatos.add(cbCategoria, gbc);

        panelPrincipal.add(panelDatos, BorderLayout.NORTH);

        // ---- Panel de filtros ----
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltro.setBorder(BorderFactory.createTitledBorder("Filtrar libros"));

        panelFiltro.add(new JLabel("Filtrar por:"));
        cbTipoFiltro = new JComboBox<>(new String[]{"Autor", "Categoría"});
        cbTipoFiltro.addActionListener(e -> cargarValoresFiltro());
        panelFiltro.add(cbTipoFiltro);

        cbValorFiltro = new JComboBox<>();
        panelFiltro.add(cbValorFiltro);

        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> filtrarLibros());
        panelFiltro.add(btnFiltrar);

        btnMostrarTodos = new JButton("Mostrar todos");
        btnMostrarTodos.addActionListener(e -> cargarTabla(libroDatos.listarTodos()));
        panelFiltro.add(btnMostrarTodos);

        panelPrincipal.add(panelFiltro, BorderLayout.SOUTH);

        return panelPrincipal;
    }

    private JScrollPane construirPanelTabla() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Título", "Año", "Autor", "Categoría"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // La tabla es de solo lectura; se edita mediante el formulario
            }
        };

        tblLibros = new JTable(modeloTabla);
        tblLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLibros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblLibros.getSelectedRow() != -1) {
                cargarLibroSeleccionadoEnFormulario();
            }
        });

        JScrollPane scroll = new JScrollPane(tblLibros);
        scroll.setPreferredSize(new Dimension(900, 300));
        return scroll;
    }

    private JPanel construirPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarLibro());

        btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarLibro());

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setForeground(Color.RED.darker());
        btnEliminar.addActionListener(e -> eliminarLibro());

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        return panelBotones;
    }

    // =========================================================
    // Carga de combos y tabla
    // =========================================================
    public void cargarAutoresEnCombo() {
        List<AutorBeans> autores = autorDatos.listarTodos();
        cbAutor.setModel(new DefaultComboBoxModel<>(autores.toArray(new AutorBeans[0])));
        cbAutor.setSelectedItem(null);
    }

    public void cargarCategoriasEnCombo() {
        List<CategoriaBeans> categorias = categoriaDatos.listarTodos();
        cbCategoria.setModel(new DefaultComboBoxModel<>(categorias.toArray(new CategoriaBeans[0])));
    }

    private void cargarValoresFiltro() {
        cbValorFiltro.removeAllItems();
        String tipo = (String) cbTipoFiltro.getSelectedItem();
        if ("Autor".equals(tipo)) {
            for (AutorBeans a : autorDatos.listarTodos()) {
                cbValorFiltro.addItem(a);
            }
        } else {
            for (CategoriaBeans c : categoriaDatos.listarTodos()) {
                cbValorFiltro.addItem(c);
            }
        }
    }

    private void cargarTabla(List<LibroBeans> libros) {
        modeloTabla.setRowCount(0);
        for (LibroBeans l : libros) {
            modeloTabla.addRow(new Object[]{
                l.getIdLibro(), l.getTitulo(), l.getAnioPublicacion(),
                l.getNombreAutor(), l.getNombreCategoria()
            });
        }
    }

    private void filtrarLibros() {
        String tipo = (String) cbTipoFiltro.getSelectedItem();
        Object valor = cbValorFiltro.getSelectedItem();

        if (valor == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay valores disponibles para filtrar. Registre primero autores/categorías.",
                    "Filtro vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Autor".equals(tipo)) {
            AutorBeans a = (AutorBeans) valor;
            cargarTabla(libroDatos.filtrarPorAutor(a.getIdAutor()));
        } else {
            CategoriaBeans c = (CategoriaBeans) valor;
            cargarTabla(libroDatos.filtrarPorCategoria(c.getIdCategoria()));
        }
    }

    // =========================================================
    // Selección de fila -> formulario
    // =========================================================
    private void cargarLibroSeleccionadoEnFormulario() {
        int fila = tblLibros.getSelectedRow();
        idLibroSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtTitulo.setText(String.valueOf(modeloTabla.getValueAt(fila, 1)));
        txtAnio.setText(String.valueOf(modeloTabla.getValueAt(fila, 2)));

        seleccionarEnCombo(cbAutor, String.valueOf(modeloTabla.getValueAt(fila, 3)));
        seleccionarEnCombo(cbCategoria, String.valueOf(modeloTabla.getValueAt(fila, 4)));

        habilitarBotonesEdicion(true);
    }

    private void seleccionarEnCombo(JComboBox<?> combo, String textoBuscado) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).toString().equals(textoBuscado)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void habilitarBotonesEdicion(boolean habilitar) {
        btnEditar.setEnabled(habilitar);
        btnEliminar.setEnabled(habilitar);
    }

    // =========================================================
    // Operaciones CRUD
    // =========================================================
    private void guardarLibro() {
        if (!validarCampos()) {
            return;
        }

        int idAutor = obtenerOCrearAutor();
        if (idAutor == 0) {
            return; // canceló o hubo un error (ya se mostró el mensaje)
        }

        LibroBeans libro = new LibroBeans();
        libro.setTitulo(txtTitulo.getText().trim());
        libro.setAnioPublicacion(Integer.parseInt(txtAnio.getText().trim()));
        libro.setIdAutor(idAutor);
        libro.setIdCategoria(((CategoriaBeans) cbCategoria.getSelectedItem()).getIdCategoria());

        if (libroDatos.insertar(libro)) {
            JOptionPane.showMessageDialog(this, "Libro registrado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla(libroDatos.listarTodos());
        }
    }

    private void editarLibro() {
        if (idLibroSeleccionado == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar primero un libro de la tabla para editarlo.",
                    "Ningún registro seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos()) {
            return;
        }

        int idAutor = obtenerOCrearAutor();
        if (idAutor == 0) {
            return; // canceló o hubo un error (ya se mostró el mensaje)
        }

        LibroBeans libro = new LibroBeans();
        libro.setIdLibro(idLibroSeleccionado);
        libro.setTitulo(txtTitulo.getText().trim());
        libro.setAnioPublicacion(Integer.parseInt(txtAnio.getText().trim()));
        libro.setIdAutor(idAutor);
        libro.setIdCategoria(((CategoriaBeans) cbCategoria.getSelectedItem()).getIdCategoria());

        if (libroDatos.actualizar(libro)) {
            JOptionPane.showMessageDialog(this, "Libro actualizado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla(libroDatos.listarTodos());
        }
    }

    private void eliminarLibro() {
        if (idLibroSeleccionado == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar primero un libro de la tabla para eliminarlo.",
                    "Ningún registro seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar el libro seleccionado?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (libroDatos.eliminar(idLibroSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Libro eliminado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarTabla(libroDatos.listarTodos());
            }
        }
    }

    private void limpiarFormulario() {
        txtTitulo.setText("");
        txtAnio.setText("");
        cbAutor.setSelectedItem(null);
        if (cbCategoria.getItemCount() > 0) {
            cbCategoria.setSelectedIndex(0);
        }
        idLibroSeleccionado = 0;
        tblLibros.clearSelection();
        habilitarBotonesEdicion(false);
        txtTitulo.requestFocus();
    }

    // =========================================================
    // Autor escrito a mano: buscar en BD o registrar si no existe
    // =========================================================
    private String getTextoAutor() {
        Object item = cbAutor.getEditor().getItem();
        return item == null ? "" : item.toString().trim();
    }

    /**
     * Retorna el id del autor escrito/seleccionado. Si no existe en la BD,
     * pide la nacionalidad y lo registra. Retorna 0 si se cancela o falla.
     */
    private int obtenerOCrearAutor() {
        String nombre = getTextoAutor();

        AutorBeans existente = autorDatos.buscarPorNombre(nombre);
        if (existente != null) {
            return existente.getIdAutor();
        }

        String nacionalidad = JOptionPane.showInputDialog(this,
                "El autor \"" + nombre + "\" no está registrado.\n"
                + "Ingrese su nacionalidad para registrarlo:",
                "Nuevo autor", JOptionPane.QUESTION_MESSAGE);

        if (nacionalidad == null) {
            return 0; // el usuario canceló
        }
        nacionalidad = nacionalidad.trim();
        if (nacionalidad.isEmpty() || !nacionalidad.matches("[\\p{L} .'\\-]+")) {
            JOptionPane.showMessageDialog(this,
                    "La nacionalidad es obligatoria y solo puede contener letras.",
                    "Valor inválido", JOptionPane.WARNING_MESSAGE);
            return 0;
        }

        int nuevoId = autorDatos.insertarYObtenerId(new AutorBeans(nombre, nacionalidad));
        if (nuevoId > 0) {
            cargarAutoresEnCombo();
            seleccionarEnCombo(cbAutor, nombre);
        }
        return nuevoId;
    }

    // =========================================================
    // Validaciones (Capa 8 - mensajes claros al usuario)
    // =========================================================
    private boolean validarCampos() {
        String titulo = txtTitulo.getText().trim();
        String anioTexto = txtAnio.getText().trim();

        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El título del libro es obligatorio.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtTitulo.requestFocus();
            return false;
        }

        if (anioTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El año de publicación es obligatorio.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtAnio.requestFocus();
            return false;
        }

        int anio;
        try {
            anio = Integer.parseInt(anioTexto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "El año de publicación debe ser un número entero (ej. 1998).",
                    "Formato inválido", JOptionPane.WARNING_MESSAGE);
            txtAnio.requestFocus();
            return false;
        }

        if (anio <= 0) {
            JOptionPane.showMessageDialog(this,
                    "El año de publicación debe ser un número mayor a 0.",
                    "Valor inválido", JOptionPane.WARNING_MESSAGE);
            txtAnio.requestFocus();
            return false;
        }

        int anioActual = Year.now().getValue();
        if (anio > anioActual) {
            JOptionPane.showMessageDialog(this,
                    "El año de publicación no puede ser mayor al año actual (" + anioActual + ").",
                    "Fecha inválida", JOptionPane.WARNING_MESSAGE);
            txtAnio.requestFocus();
            return false;
        }

        if (anio < 1450) {
            // Referencia: invención de la imprenta moderna
            JOptionPane.showMessageDialog(this,
                    "El año de publicación no parece válido (mínimo permitido: 1450).",
                    "Valor inválido", JOptionPane.WARNING_MESSAGE);
            txtAnio.requestFocus();
            return false;
        }

        String autor = getTextoAutor();
        if (autor.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe escribir o seleccionar el autor del libro.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            cbAutor.requestFocus();
            return false;
        }
        if (autor.length() < 3) {
            JOptionPane.showMessageDialog(this,
                    "El nombre del autor debe tener al menos 3 caracteres.",
                    "Valor inválido", JOptionPane.WARNING_MESSAGE);
            cbAutor.requestFocus();
            return false;
        }
        if (!autor.matches("[\\p{L} .'\\-]+")) {
            JOptionPane.showMessageDialog(this,
                    "El nombre del autor solo puede contener letras, espacios, puntos, apóstrofes y guiones.",
                    "Formato inválido", JOptionPane.WARNING_MESSAGE);
            cbAutor.requestFocus();
            return false;
        }

        if (cbCategoria.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una categoría. Si no existe ninguna, regístrela primero "
                    + "desde el menú Administrar > Gestionar Categorías.",
                    "Categoría requerida", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
}