import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

/**
 * Spojeni mezi jednotlivymi routery.
 */
public class Link implements IUpdatable, IDrawable, Comparable<Link> {
    /**Mnozstvi dat, ktere protekly tuto sekundu jednim smerem*/
    private short data1to2;
    /**Mnozstvi dat, ktere protekly tuto sekundu druhym smerem*/
    private short data2to1;
    /**Nasledujici Link spojujici stejne uzly*/
    private Link nextLink;
    /**Predchazejici Link spojujici stejnej uzly*/
    private Link previousLink;

//    /**Id routeru 1*/
//    private final int r1Id;
//    /**Id routeru 2*/
//    private final int r2Id;
    /**ID routeru, mezi kterymi link je*/
    private final RouterPair routerPair;
    /**Maximalni propustnost*/
    private final float throughtput;
    /**Spolehlivost spojeni - procento z maximalni propustnosti dat, ktere se odesle bezeztraty*/
    private final float reliability;
    /**Maximalni bezeztratova propustnost*/
    private final float maxThroughtput;
    /**Priblizna maximalni bezestratova propustnost*/
    private final int ccaMaxThroughtput;

    /**
     * Spojeni mezi jednotlivymi routery
     *
     * @param maxThroughtput maximalni propustnost dat
     * @param reliability procento z max. propustnosti dat, ktere se odesle bezeztraty
//     * @param r1Id id routeru 1
//     * @param r2Id id routeru 2
     * @param routerPair id routeru mezi, kterymi je vytvareny link
     */
    public Link(float maxThroughtput, float reliability, RouterPair routerPair) {
        //TODO odstranit jednicky
        this.throughtput = maxThroughtput;
        //this.reliability = reliability;
//        this.throughtput = 1.0f;
        this.reliability = 1.0f;
//        this.r1Id = r1Id;
//        this.r2Id = r2Id;
        this.routerPair = routerPair;
        this.nextLink = null;
        this.previousLink = null;

        this.maxThroughtput = throughtput * this.reliability;
        this.ccaMaxThroughtput = Math.round(this.maxThroughtput);
    }

    @Override
    public void draw(GraphicsContext g, int routersInRow) {
        Affine t = g.getTransform();
        int deltaXY = (int)(g.getCanvas().getHeight() / (routersInRow + 1));
        g.setStroke(Color.YELLOW);
        g.translate(deltaXY, deltaXY / 2);

        g.strokeLine((deltaXY * (routerPair.r1.getId() % routersInRow)), (deltaXY * (routerPair.r1.getId() / routersInRow)),
            (deltaXY * (routerPair.r2.getId() % routersInRow)), (deltaXY * (routerPair.r2.getId() / routersInRow)));

        g.setTransform(t);
    }

    @Override
    public void update(World world) {

    }

    /**
     * Pokud maji dva routery mezi sebou vice linku, ulozi se do spojoveho seznamu
     *
     * @param link Link mezi stejnymi routery
     */
    public void addNextLink(Link link) {
        if (this.nextLink == null) {
            this.nextLink = link;
            this.nextLink.previousLink = this;
        } else {
            this.nextLink.addNextLink(link);
        }
    }

    public void sendDataDir1(short data, Router r) {

    }

    public Link getNextLink() {
        return nextLink;
    }

    public Link getPreviousLink() {
        return previousLink;
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

    public int getR1Id() {
        return routerPair.r1.getId();
    }

    public int getR2Id() {
        return routerPair.r2.getId();
    }

    public Router getNeighbour(Router router) {
        if (router.equals(routerPair.r1))
            return routerPair.r2;

        return routerPair.r1;
    }

    public RouterPair getRouterPair() {
        return routerPair;
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

    public int getCcaMaxThroughtput() { return ccaMaxThroughtput; }

    @Override
    public int compareTo(Link link) {
        return Integer.compare(link.getCcaMaxThroughtput(), ccaMaxThroughtput);
    }

    @Override
    public String toString() {
        return this.getR1Id() + " ~ " + this.getR2Id();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (ccaMaxThroughtput != link.ccaMaxThroughtput) return false;

        //return ((r1Id == link.r1Id || r1Id == link.r2Id) && (r2Id == link.r2Id || r2Id == link.r1Id));
        return routerPair.equals(routerPair);
    }

    @Override
    public int hashCode() {
        //int result = (int) data1to2 + (int) data2to1;
        int result = routerPair.r1.getId() + routerPair.r2.getId();
        result = 31 * result + (throughtput != +0.0f ? Float.floatToIntBits(throughtput) : 0);
        result = 31 * result + (reliability != +0.0f ? Float.floatToIntBits(reliability) : 0);
        result = 31 * result + (maxThroughtput != +0.0f ? Float.floatToIntBits(maxThroughtput) : 0);
        result = 31 * result + ccaMaxThroughtput;
        return result;
    }
}
