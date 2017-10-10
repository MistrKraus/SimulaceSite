import javafx.scene.canvas.GraphicsContext;

/**
 * Routery v sidi ridici komunikaci.
 */
public class Router implements IUpdatable, IDrawable {

    /**Data ulozena v routeru*/
    private short data = 0;
    /**Router pracuje*/
    private boolean up = true;

    /**ID routeru*/
    private final int id;
    /**Pamet routeru - maximalni mnozstvi dat, ktere dokaze uchovavat*/
    private static final byte MEMORY = 100;

    public Router(int id) {
        this.id = id;
    }

    @Override
    public void draw(GraphicsContext g) {

    }

    @Override
    public void update(World world) {

    }

    public void setData(Link link, Data data) {

    }
}
