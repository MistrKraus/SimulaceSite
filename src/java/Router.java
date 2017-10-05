import javafx.scene.canvas.GraphicsContext;

/**
 * Routery v sidi ridici komunikaci.
 */
public class Router implements IUpdatable, IDrawable {

    /**Data ulozena v routeru*/
    private byte data = 0;

    /**Pamet routeru - maximalni mnozstvi dat, ktere dokaze uchovavat*/
    private static final byte MEMORY = 100;

    public Router() {

    }

    @Override
    public void draw(GraphicsContext g) {

    }
/*
    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }
*/
    @Override
    public void update(World world) {

    }
}
