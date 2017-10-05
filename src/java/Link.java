import javafx.scene.canvas.GraphicsContext;

/**
 * Spojeni mezi jednotlivymi routery.
 */
public class Link implements IUpdatable, IDrawable {
    /**Mnozstvi dat, ktere protekly tuto sekundu*/
    private short throughtput;

    /**Maximalni propustnost*/
    private final short maxThroughtput;
    /**Spolehlivost spojeni*/
    private final float reliability;

    public Link(short maxThroughtput, float reliability) {
        this.maxThroughtput = maxThroughtput;
        this.reliability = reliability;
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
