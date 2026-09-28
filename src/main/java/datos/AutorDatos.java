package datos;

import beans.AutorBeans;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}