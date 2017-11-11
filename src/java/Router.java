import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import java.util.LinkedList;
import java.util.List;

/**
 * Routery v sidi ridici komunikaci.
 */
public class Router implements IUpdatable, IDrawable {

    /**Data ulozena v routeru*/
    private short data = 0;
    /**Pocet linku pripojenych k routeru*/
    private short linkCount = 0;
    /**Router pracuje*/
    private boolean up = true;
    /** Nazev routeru*/
    private String name;
    /**ID routeru*/
    private final int id;
    /**Pamet routeru - maximalni mnozstvi dat, ktere dokaze uchovavat*/
    private static final short MEMORY = 100;
    /** Sousedi daného routeru (kdyžtak přepsat na objekt Router, pokud nebude stačit Short)*/
    List<Short> neighbours = new LinkedList<>();


    public Router(int id, String name) {
        this.id = id;
        this.name = name;
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

    public void addLink() {
        linkCount++;
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

    public short getLinkCount() {
        return linkCount;
    }

    public static byte getMEMORY() {
        return MEMORY;
    }
}
