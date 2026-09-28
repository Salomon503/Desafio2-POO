package beans;

/**
 * Bean (POJO) que representa un Autor.
 * Todos los atributos son privados y se accede a ellos mediante getters/setters.
 */
public class AutorBeans {

    private int idAutor;
    private String nombre;
    private String nacionalidad;

    public AutorBeans() {
    }

    public AutorBeans(int idAutor, String nombre, String nacionalidad) {
        this.idAutor = idAutor;
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
    }

    public AutorBeans(String nombre, String nacionalidad) {
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
    }

    public int getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(int idAutor) {
        this.idAutor = idAutor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    /**
     * Se sobreescribe toString() para que el JComboBox muestre
     * directamente el nombre del autor en lugar de la referencia del objeto.
     */
    @Override
    public String toString() {
        return nombre;
    }
}

