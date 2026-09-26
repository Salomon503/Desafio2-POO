package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Clase encargada exclusivamente de administrar la conexión JDBC
 * hacia la base de datos MySQL "biblioteca_db".
 *
 * Ajustar USUARIO y CONTRASENA según la configuración local de MySQL.
 */
public class Conexion {

    private static final String URL =
            "jdbc:mysql://localhost:3307/biblioteca_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "";

    private static Connection conexion;

    // Constructor privado: no se debe instanciar esta clase (solo métodos estáticos)
    private Conexion() {
    }

    /**
     * Retorna una conexión activa hacia la base de datos.
     * Si no existe o fue cerrada, crea una nueva.
     */
    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "No se encontró el driver JDBC de MySQL.\n"
                    + "Verifique que el archivo mysql-connector-j.jar esté agregado "
                    + "a las Librerías del proyecto en NetBeans.",
                    "Error de driver", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "No fue posible conectar con la base de datos:\n" + e.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
        return conexion;
    }

    /** Cierra la conexión activa, si existe. */
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al cerrar la conexión:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
