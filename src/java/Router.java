import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import java.util.*;

/**
 * Routery v sidi ridici komunikaci.
 */
public class Router implements IUpdatable, IDrawable, Comparable<Router> {

    /**Id barvy, ktera se vyuzije na vykresleni*/
    private short colorId = 0;
    /**Minimalni vzdalenost do hrany*/
    private double minDistance = Double.POSITIVE_INFINITY;
    /**Router pracuje*/
    private boolean up = true;
    /**Zbyvajici pamet*/
    private int memoryLeft = MEMORY;
//    /**Je router default gateway*/
//    private boolean defGW = false;
    /**Predchazejici router*/
    private Router previous;
    /** List - Sousedi daného routeru*/
    private Map<Integer, Link> links = new HashMap<>();
    /**Data k odeslani*/
    private List<Data> dataToSend = new ArrayList<>();
    /**Data ulozena v routeru*/
    private List<Data> dataToSave = new ArrayList<>();
    /**Prijate nekompletni pakety dat*/
    private List<Data> recievedData = new ArrayList<>();
    /**Data ke smazani*/
    private List<Data> dataToRemove = new ArrayList<>();
    /** Nazev routeru*/
    private final String name;

    /**ID routeru*/
    private final int id;
    /**Pamet routeru - maximalni mnozstvi dat, ktere dokaze uchovavat*/
    private static final int MEMORY = 100000000;

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

        switch (colorId) {
            case 0:
                g.setFill(Color.WHITE);
                break;
                //return;
            case 1:
                g.setFill(Color.rgb(0, 145, 255));
                break;
            case 2:
                g.setFill(Color.RED);
                break;
        }

        g.translate(deltaXY, deltaXY / 2);
        g.fillOval((deltaXY * (id % routersInRow)) - 1,
                deltaXY * (id / routersInRow) - 1, 2, 2);

        //g.fillOval(-5,-5,10, 10);

        g.setTransform(t);
    }

    @Override
    public void update(World world) {
        if (dataToSend.size() == 0)
            return;

        if (id != Dijkstra.getSource()) {
            world.getRouters().values().forEach(r -> {
                r.setPrevious(null);
                r.setMinDistance(Double.POSITIVE_INFINITY);
            });
            Dijkstra.computePath(this);
        }

        int x;
        for (Data data : dataToSend) {
            System.out.println(data.toString());
            x = prepareAndSendData(data);
            switch (x) {
                case -1:
                    System.out.println("Chyba pri odesilani dat.");
                    break;
                case -2:
                    System.out.println("Cast dat prijata.");
                    break;
                default:
                    if (data.targetRouter.getId() == x) {
                        System.out.println("Data dorucena.");
                    } else {
                        System.out.println("Data docasne ulozena v Routeru" + x);
                    }
            }
        }
    }

    @Override
    public void restore(World world) {
        dataToSend.removeAll(dataToRemove);
        recievedData.removeAll(dataToRemove);
        dataToRemove.clear();

        dataToSend.addAll(dataToSave);

        memoryLeft = MEMORY;

        for (Data data : dataToSend) {
            world.getWeb().addDataInRouter();
            memoryLeft -= data.amount;
        }

        if (memoryLeft == MEMORY) {
            colorId = 0;
            return;
        }

        if (memoryLeft > 0) {
            colorId = 1;
            return;
        }

        colorId = 2;
    }

    public void carryData(Data data) {
        dataToSend.add(data);
    }

    public void saveData(Data data) {
        dataToSave.add(data);

        this.memoryLeft -= data.amount;
    }

    /**
     * Pripravi a odesle data
     *
     * @param data data k odeslani
     * @return id routeru, kde data na konci ticku zustala
     *          -1 pro neuspech
     */
    private int prepareAndSendData(Data data) {
        if (data.amount == 0)
            return data.targetRouter.id;

        int idOnPath = 0;
        List<Router> path = Dijkstra.getShortestPathTo(data.targetRouter);

        return sendData(path, ++idOnPath, data);
//        boolean dataSentSuccesfuly = true;
//        for (Router router : path) {
//            if (sendDataVia(router, data) == -1) {
//                dataSentSuccesfuly = false;
//                break;
//            }
//        }



        //links.get(data.targetRouter.id).prepareAndSendData(data);

        //return data.targetRouter.getId();
    }

    /**
     * Odesle data
     *
     * @param path list routeru, pres ktere se data maji poslat
     * @param data data k odeslani
     * @return id routeru, kde data na konci ticku zustala
     *          -1 pro neuspech
     *          -2 data prijata, ceka se na celkova data
     */
    public int sendData(List<Router> path, int idOnPath, Data data) {
        if (id == data.targetRouter.id) {
            return recieveData(data);
        }

        Link link = links.get(path.get(idOnPath).id);

        int linkCapacity = link.getDirCapacity(id);

        if (linkCapacity < data.amount) {
            saveData(data.splitMe(linkCapacity));
        }

        if (idOnPath + 1 < path.size() && path.get(idOnPath + 1).getMemoryLeft() < linkCapacity) {
            saveData(data.splitMe(path.get(idOnPath + 1).getMemoryLeft()));
        }

        dataToRemove.add(data);

        return link.sendData(path, idOnPath, data);

        //return data.targetRouter.getId();
    }

    /**
     * Prijme dato, zjisti zda je cele, pokud
     *
     * @param data prijate dato
     * @return id routeru: cele dato prijato
     *          -2: prijata cast data
     */
    private int recieveData(Data data) {
        if (data.amount == data.originalAmount) {
            dataToRemove.add(data);
            return id;
        }

        this.recievedData.add(data);

        int amount = 0;
        for (Data data1 : recievedData) {
            if (data1.id == data.id) {
                amount += data1.amount;
            }
        }

        if (amount == data.originalAmount) {
            dataToRemove.add(data);
            return id;
        }

        return -2;
    }

    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    public void setPrevious(Router previous) {
        this.previous = previous;
    }

    public void addLink(Link link) {
        links.put(link.getNeighbourId(id), link);
    }

    public boolean isUp() {
        return up;
    }

    public String getName() {
        return name;
    }

    public List<Data> getData() {
        return dataToSend;
    }

    public int getMemoryLeft() {
        return memoryLeft;
    }

    public int getId() {
        return id;
    }

    public static int getMEMORY() {
        return MEMORY;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public Router getPrevious() {
        return previous;
    }

    public Map<Integer, Link> getLinks() {
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
