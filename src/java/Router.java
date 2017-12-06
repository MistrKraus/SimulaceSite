import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import java.util.ArrayList;
import java.util.List;

/**
 * Routery v sidi ridici komunikaci.
 */
public class Router implements IUpdatable, IDrawable, Comparable<Router> {

    /**Data ulozena v routeru*/
    private short data = 0;
    /**ID subnetu ve kterem se router nachazi*/
    private short subnetID;
    /**Minimalni vzdalenost do hrany*/
    private double minDistance = Double.POSITIVE_INFINITY;
    /**Router pracuje*/
    private boolean up = true;
    /**Je router default gateway*/
    private boolean defGW = false;
    /**Predchazejici router*/
    private Router previous;
    /** List - Sousedi daného routeru*/
    private List<Link> links = new ArrayList<>();
    /** Nazev routeru*/
    private final String name;

    /**ID routeru*/
    private final int id;
    /**Pamet routeru - maximalni mnozstvi dat, ktere dokaze uchovavat*/
    private static final short MEMORY = 100;

    //** List - Sousedi daného routeru (kdyžtak přepsat na objekt Router, pokud nebude stačit Short)*/
    //List<Short> neighbour = new LinkedList<>();

    public Router(int id) {
        this.id = id;
        this.name = "Router" + id;
        this.previous = null;
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

    @Override
    public void restore(World world) {

    }

    public void setData(Link link, Data data) {

    }

    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    public void setPrevious(Router previous) {
        this.previous = previous;
    }

    public void addLink(Link link) {
        links.add(link);
//        if (link.getR1Id() == this.id)
//            links.put(link.getR2Id(), link);
//        else
//            links.put(link.getR1Id(), link);
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

    public double getMinDistance() {
        return minDistance;
    }

    public Router getPrevious() {
        return previous;
    }

    public List<Link> getLinks() {
        return links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Router router = (Router) o;

        if (id != router.id) return false;
        return links != null ? links.equals(router.links) : router.links == null;
    }

    @Override
    public int hashCode() {
        int result = links != null ? links.hashCode() : 0;
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "Router[" + id + "]";
    }

    @Override
    public int compareTo(Router o) {
        return Double.compare(minDistance, o.getMinDistance());
    }
}
