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
}
