package datos;

import beans.LibroBeans;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import util.Conexion;

/**
 * Capa de acceso a datos para la tabla "libro".
 * Incluye JOIN con autor y categoria para poder mostrar sus nombres
 * directamente en la JTable.
 */
public class LibroDatos {

    private static final String SELECT_BASE =
            "SELECT l.id_libro, l.titulo, l.anio_publicacion, l.id_autor, l.id_categoria, "
          + "       a.nombre AS nombre_autor, c.nombre_categoria "
          + "FROM libro l "
          + "INNER JOIN autor a ON l.id_autor = a.id_autor "
          + "INNER JOIN categoria c ON l.id_categoria = c.id_categoria ";

    public boolean insertar(LibroBeans libro) {
        String sql = "INSERT INTO libro (titulo, anio_publicacion, id_autor, id_categoria) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setInt(2, libro.getAnioPublicacion());
            ps.setInt(3, libro.getIdAutor());
            ps.setInt(4, libro.getIdCategoria());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al registrar el libro:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean actualizar(LibroBeans libro) {
        String sql = "UPDATE libro SET titulo = ?, anio_publicacion = ?, id_autor = ?, "
                   + "id_categoria = ? WHERE id_libro = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setInt(2, libro.getAnioPublicacion());
            ps.setInt(3, libro.getIdAutor());
            ps.setInt(4, libro.getIdCategoria());
            ps.setInt(5, libro.getIdLibro());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al actualizar el libro:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean eliminar(int idLibro) {
        String sql = "DELETE FROM libro WHERE id_libro = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idLibro);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al eliminar el libro:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Método privado de apoyo: ejecuta la consulta SELECT indicada,
     * asignando el parámetro (id_autor o id_categoria) cuando corresponde.
     */
    private List<LibroBeans> ejecutarConsulta(String sql, String tipoFiltro, Integer valorFiltro) {
        List<LibroBeans> lista = new ArrayList<>();

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (tipoFiltro != null) {
                ps.setInt(1, valorFiltro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LibroBeans l = new LibroBeans();
                    l.setIdLibro(rs.getInt("id_libro"));
                    l.setTitulo(rs.getString("titulo"));
                    l.setAnioPublicacion(rs.getInt("anio_publicacion"));
                    l.setIdAutor(rs.getInt("id_autor"));
                    l.setIdCategoria(rs.getInt("id_categoria"));
                    l.setNombreAutor(rs.getString("nombre_autor"));
                    l.setNombreCategoria(rs.getString("nombre_categoria"));
                    lista.add(l);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al consultar libros:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return lista;
    }
}
