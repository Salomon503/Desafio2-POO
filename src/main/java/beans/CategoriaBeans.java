package beans;
 
/**
 * Bean (POJO) que representa una Categoría literaria.
 * Todos los atributos son privados y se accede a ellos mediante getters/setters.
 */
public class CategoriaBeans {
 
    private int idCategoria;
    private String nombreCategoria;
 
    public CategoriaBeans() {
    }
 
    public CategoriaBeans(int idCategoria, String nombreCategoria) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
    }
 
    public CategoriaBeans(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
 
    public int getIdCategoria() {
        return idCategoria;
    }
 
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }
 
    public String getNombreCategoria() {
        return nombreCategoria;
    }
 
    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
 
    /**
     * Se sobreescribe toString() para que el JComboBox muestre
     * directamente el nombre de la categoría en lugar de la referencia del objeto.
     */
    @Override
    public String toString() {
        return nombreCategoria;
    }
}
