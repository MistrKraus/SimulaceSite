import javafx.scene.canvas.GraphicsContext;

/**
 * Spojeni mezi jednotlivymi routery.
 */
public class Link implements IUpdatable, IDrawable {
    /**Mnozstvi dat, ktere protekly tuto sekundu jednim smerem*/
    private short data1to2;
    /**Mnozstvi dat, ktere protekly tuto sekundu druhym smerem*/
    private short data2to1;

    /**Id routeru 1*/
    private final short r1Id;
    /**Id routeru 2*/
    private final short r2Id;
    /**Maximalni propustnost*/
    private final float throughtput;
    /**Spolehlivost spojeni - procento z maximalni propustnosti dat, ktere se odesle bezeztraty*/
    private final float reliability;
    /**Maximalni bezeztratova propustnost*/
    private final float maxThroughtput;

    /**
     * Spojeni mezi jednotlivymi routery
     *
     * @param maxThroughtput maximalni propustnost dat
     * @param reliability procento z max. propustnosti dat, ktere se odesle bezeztraty
     * @param r1Id id routeru 1
     * @param r2Id id routeru 2
     */
    public Link(float maxThroughtput, float reliability, short r1Id, short r2Id) {
        this.throughtput = maxThroughtput;
        this.reliability = reliability;
        this.r1Id = r1Id;
        this.r2Id = r2Id;

        this.maxThroughtput = throughtput * reliability;
    }

    @Override
    public void draw(GraphicsContext g) {

    }

    @Override
    public void update(World world) {

    }

    public void sendDataDir1(short data, Router r) {

    }
}
