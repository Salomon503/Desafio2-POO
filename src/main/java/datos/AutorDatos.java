package datos;

import beans.AutorBeans;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import util.Conexion;

/**
 * Capa de acceso a datos para la tabla "autor".
 * Todas las consultas usan PreparedStatement.
 */
public class AutorDatos {

    public boolean insertar(AutorBeans autor) {
        String sql = "INSERT INTO autor (nombre, nacionalidad) VALUES (?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, autor.getNombre());
            ps.setString(2, autor.getNacionalidad());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al registrar el autor:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean actualizar(AutorBeans autor) {
        String sql = "UPDATE autor SET nombre = ?, nacionalidad = ? WHERE id_autor = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, autor.getNombre());
            ps.setString(2, autor.getNacionalidad());
            ps.setInt(3, autor.getIdAutor());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al actualizar el autor:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean eliminar(int idAutor) {
        String sql = "DELETE FROM autor WHERE id_autor = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAutor);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // Captura típica: FK en uso por algún libro
            JOptionPane.showMessageDialog(null,
                    "No se puede eliminar el autor. Es posible que tenga libros asociados.\n"
                    + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public List<AutorBeans> listarTodos() {
        List<AutorBeans> lista = new ArrayList<>();
        String sql = "SELECT id_autor, nombre, nacionalidad FROM autor ORDER BY nombre";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AutorBeans a = new AutorBeans();
                a.setIdAutor(rs.getInt("id_autor"));
                a.setNombre(rs.getString("nombre"));
                a.setNacionalidad(rs.getString("nacionalidad"));
                lista.add(a);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al listar autores:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return lista;
    }

    /** Busca un autor por nombre (sin distinguir mayúsculas). Retorna null si no existe. */
    public AutorBeans buscarPorNombre(String nombre) {
        String sql = "SELECT id_autor, nombre, nacionalidad FROM autor WHERE LOWER(nombre) = LOWER(?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AutorBeans(rs.getInt("id_autor"),
                            rs.getString("nombre"), rs.getString("nacionalidad"));
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al buscar el autor:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    /** Inserta un autor y retorna el id generado (0 si falla). */
    public int insertarYObtenerId(AutorBeans autor) {
        String sql = "INSERT INTO autor (nombre, nacionalidad) VALUES (?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, autor.getNombre());
            ps.setString(2, autor.getNacionalidad());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al registrar el autor:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }
}