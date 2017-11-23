import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import java.util.HashMap;
import java.util.Map;

/**
 * Routery v sidi ridici komunikaci.
 */
public class Router implements IUpdatable, IDrawable {

    /**Data ulozena v routeru*/
    private short data = 0;
    /**Router pracuje*/
    private boolean up = true;
    /**Je router default gateway*/
    private boolean defGW = false;

    /**ID routeru*/
    private final int id;
    /**Pamet routeru - maximalni mnozstvi dat, ktere dokaze uchovavat*/
    private static final short MEMORY = 100;

    /** Mapa - Sousedi daného routeru*/
    private Map<Integer, Link> neighbours = new HashMap<>();
    /** Nazev routeru*/
    private final String name;
    //** List - Sousedi daného routeru (kdyžtak přepsat na objekt Router, pokud nebude stačit Short)*/
    //List<Short> neighbour = new LinkedList<>();

    public Router(int id) {
        this.id = id;
        this.name = "Router" + id;
    }

    @Override
    public void draw(GraphicsContext g, int routersInRow) {
        Affine t = g.getTransform();
        int deltaXY = (int)(g.getCanvas().getHeight() / (routersInRow + 1));

        g.setFill(Color.WHITE);

        g.translate(deltaXY, deltaXY / 2);
        g.fillOval((deltaXY * (id % routersInRow)) - 5,
                deltaXY * (id / routersInRow) - 5, 10, 10);


        //g.fillOval(-5,-5,10, 10);

        g.setTransform(t);
    }

    @Override
    public void update(World world) {


    }

    public void setData(Link link, Data data) {

    }

    public void addNeighbour(Link link) {
        if (link.getR1Id() == this.id)
            neighbours.put(link.getR2Id(), link);
        else
            neighbours.put(link.getR1Id(), link);
    }

    public boolean isUp() {
        return up;
    }

    public String getName() {
        return name;
    }

    public short getData() {
        return data;
    }

    public int getId() {
        return id;
    }

    public static byte getMEMORY() {
        return MEMORY;
    }

    @Override
    public String toString() {
        return "Router{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", data=" + data +
                ", neighbours=" + neighbours +
                '}';
    }
}
