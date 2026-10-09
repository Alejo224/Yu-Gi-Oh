import java.awt.*;

public class Card {
    private String nombre;
    private int atk, def;
    private Image imagen;
    private int id;


    // Getters and setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public Image getImagen() {
        return imagen;
    }

    public void setImagen(Image imagen) {
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    @Override
    public String toString() {
        return "Card{" +
                "nombre='" + nombre + '\'' +
                ", atk=" + atk +
                ", def=" + def +
                ", imagen=" + imagen +
                ", id=" + id +
                '}';
    }
}
