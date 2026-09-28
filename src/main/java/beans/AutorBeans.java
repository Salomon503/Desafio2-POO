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


}
