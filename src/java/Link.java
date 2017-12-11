import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import java.util.List;

/**
 * Spojeni mezi jednotlivymi routery.
 */
public class Link implements IWebComp, Comparable<Link> {

    /**Id barvy, ktera se vyuzije na vykresleni*/
    private short colorId = 0;
    /**Mnozstvi dat, ktere mohou protect tuto sekundu jednim smerem*/
    private int data1to2;
    /**Mnozstvi dat, ktere mohou protect tuto sekundu druhym smerem*/
    private int data2to1;
    /**Nasledujici Link spojujici stejne uzly*/
    private Link nextLink;
    /**Predchazejici Link spojujici stejnej uzly*/
    private Link previousLink;

    /**ID routeru, mezi kterymi link je*/
    private final RouterPair ROUTER_PAIR;
    /**Maximalni propustnost*/
    private final float THROUGHTPUT;
    /**Spolehlivost spojeni - procento z maximalni propustnosti dat, ktere se odesle bezeztraty*/
    private final float RELIABILITY;
    /**Maximalni bezeztratova propustnost*/
    private final float MAX_THROUGHTPUT;
    /**Priblizna maximalni bezestratova propustnost zaokrouhlena dolu*/
    private final int CCA_MAX_THROUGHTPUT;

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
        this.THROUGHTPUT = maxThroughtput;
        //this.RELIABILITY = RELIABILITY;
//        this.THROUGHTPUT = 1.0f;
        this.RELIABILITY = reliability;
//        this.r1Id = r1Id;
//        this.r2Id = r2Id;
        this.ROUTER_PAIR = routerPair;
        this.nextLink = null;
        this.previousLink = null;

        this.MAX_THROUGHTPUT = this.THROUGHTPUT * this.RELIABILITY;
        this.CCA_MAX_THROUGHTPUT = (int)Math.floor(this.MAX_THROUGHTPUT);
        this.data1to2 = CCA_MAX_THROUGHTPUT;
        this.data2to1 = CCA_MAX_THROUGHTPUT;
    }

    @Override
    public void draw(GraphicsContext g, int routersInRow) {
        Affine t = g.getTransform();
        int deltaXY = (int)(g.getCanvas().getHeight() / (routersInRow + 1));

        switch (colorId) {
            case 0:
                g.setStroke(Color.rgb(255, 255, 0));
                break;
                //return;
            case 1:
                g.setStroke(Color.rgb(0, 145, 255));
                break;
            case 2:
                g.setStroke(Color.RED);
                break;
        }
        g.translate(deltaXY, deltaXY / 2);

        g.strokeLine((deltaXY * (ROUTER_PAIR.r1.getId() % routersInRow)), (deltaXY * (ROUTER_PAIR.r1.getId() / routersInRow)),
            (deltaXY * (ROUTER_PAIR.r2.getId() % routersInRow)), (deltaXY * (ROUTER_PAIR.r2.getId() / routersInRow)));

        g.setTransform(t);
    }

    @Override
    public void update(World world) {
        this.data1to2 = CCA_MAX_THROUGHTPUT;
        this.data2to1 = CCA_MAX_THROUGHTPUT;
    }

    @Override
    public void restore(World world) {
        if (data1to2 == CCA_MAX_THROUGHTPUT && data2to1 == CCA_MAX_THROUGHTPUT) {
            colorId = 0;
        } else {
            if (data1to2 == 0 || data2to1 == 0) {
                colorId = 2;
            } else {
                colorId = 1;
            }
        }
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

    public int sendData(List<Router> path, int idOnPath, Data data) {
        Router routerFrom = path.get(idOnPath - 1);
        if (routerFrom.getId() < ROUTER_PAIR.r1.getId()) {
            int newThroughtput = data1to2 - data.amount;
            if (newThroughtput >= 0) {
                data1to2 = newThroughtput;
            } else {
                if (nextLink != null) {
                    return nextLink.sendData(path, idOnPath, data);
                }

                routerFrom.saveData(data);
                return -1;
            }
        } else {
            int newThroughtput = data2to1 - data.amount;
            if (newThroughtput >= 0) {
                data2to1 = newThroughtput;
            } else {
                if (nextLink != null) {
                    return nextLink.sendData(path, idOnPath, data);
                }

                routerFrom.saveData(data);
                return -1;
            }
        }

        return path.get(idOnPath).sendData(path, ++idOnPath, data);
    }

    public int getDirCapacity(int targetRouterId) {
        if (targetRouterId < ROUTER_PAIR.r1.getId()) {
            if (data1to2 == 0 && nextLink != null) {
                    return nextLink.getDirCapacity(targetRouterId);
            } else {
                return data1to2;
            }
        }

        if (data2to1 == 0 && nextLink != null) {
            return nextLink.getDirCapacity(targetRouterId);
        }

        return data2to1;
    }

    public Link getNextLink() {
        return nextLink;
    }

    public Link getPreviousLink() {
        return previousLink;
    }

    public int getData1to2() {
        return data1to2;
    }

    public void setData1to2(int data1to2) {
        this.data1to2 = data1to2;
    }

    public int getData2to1() {
        return data2to1;
    }

    public void setData2to1(int data2to1) {
        this.data2to1 = data2to1;
    }

    public int getR1Id() {
        return ROUTER_PAIR.r1.getId();
    }

    public int getR2Id() {
        return ROUTER_PAIR.r2.getId();
    }

    public Router getNeighbour(Router router) {
        if (router.equals(ROUTER_PAIR.r1))
            return ROUTER_PAIR.r2;

        return ROUTER_PAIR.r1;
    }

    public RouterPair getROUTER_PAIR() {
        return ROUTER_PAIR;
    }

    public float getTHROUGHTPUT() {
        return THROUGHTPUT;
    }

    public float getRELIABILITY() {
        return RELIABILITY;
    }

    public float getMAX_THROUGHTPUT() {
        return MAX_THROUGHTPUT;
    }

    public int getCCA_MAX_THROUGHTPUT() { return CCA_MAX_THROUGHTPUT; }

    @Override
    public int compareTo(Link link) {
        return Integer.compare(link.getCCA_MAX_THROUGHTPUT(), CCA_MAX_THROUGHTPUT);
    }

    @Override
    public String toString() {
        return "Link [" + this.getR1Id() + " ~ " + this.getR2Id() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (CCA_MAX_THROUGHTPUT != link.CCA_MAX_THROUGHTPUT) return false;

        //return ((r1Id == link.r1Id || r1Id == link.r2Id) && (r2Id == link.r2Id || r2Id == link.r1Id));
        return ROUTER_PAIR.equals(ROUTER_PAIR);
    }

    @Override
    public int hashCode() {
        //int result = (int) data1to2 + (int) data2to1;
        int result = ROUTER_PAIR.r1.getId() + ROUTER_PAIR.r2.getId();
        result = 31 * result + (THROUGHTPUT != +0.0f ? Float.floatToIntBits(THROUGHTPUT) : 0);
        result = 31 * result + (RELIABILITY != +0.0f ? Float.floatToIntBits(RELIABILITY) : 0);
        result = 31 * result + CCA_MAX_THROUGHTPUT;
        return result;
    }

    public int getNeighbourId(int id) {
        //System.out.print(id);
        if (id == ROUTER_PAIR.r1.getId()) {
            //System.out.println(" " + ROUTER_PAIR.r2.getId());
            return ROUTER_PAIR.r2.getId();
        }

        //System.out.println(" " + ROUTER_PAIR.r1.getId());
        return ROUTER_PAIR.r1.getId();
    }
}
