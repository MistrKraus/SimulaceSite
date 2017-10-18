import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

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
        this.throughtput = 1;//maxThroughtput;
        this.reliability = 10;//reliability;
        this.r1Id = r1Id;
        this.r2Id = r2Id;

        this.maxThroughtput = throughtput * reliability;
    }

    @Override
    public void draw(GraphicsContext g, int routersInRow) {
        Affine t = g.getTransform();
        int deltaXY = (int)(g.getCanvas().getHeight() / (routersInRow + 1));
        g.setStroke(Color.YELLOW);
        g.translate(deltaXY, deltaXY / 2);

        g.strokeLine((deltaXY * (r1Id % routersInRow)), (deltaXY * (r1Id / routersInRow)),
            (deltaXY * (r2Id % routersInRow)), (deltaXY * (r2Id / routersInRow)));

        g.setTransform(t);
    }

    @Override
    public void update(World world) {

    }

    public void sendDataDir1(short data, Router r) {

    }

    public short getData1to2() {
        return data1to2;
    }

    public void setData1to2(short data1to2) {
        this.data1to2 = data1to2;
    }

    public short getData2to1() {
        return data2to1;
    }

    public void setData2to1(short data2to1) {
        this.data2to1 = data2to1;
    }

    public short getR1Id() {
        return r1Id;
    }

    public short getR2Id() {
        return r2Id;
    }

    public float getThroughtput() {
        return throughtput;
    }

    public float getReliability() {
        return reliability;
    }

    public float getMaxThroughtput() {
        return maxThroughtput;
    }
}
