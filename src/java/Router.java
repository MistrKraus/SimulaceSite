import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import java.util.*;

/**
 * Routery v sidi ridici komunikaci.
 */
public class Router implements IWebComp, Comparable<Router> {

    /**Id barvy, ktera se vyuzije na vykresleni*/
    private short colorId = 0;
    /**Minimalni vzdalenost do hrany*/
    private double minDistance = Double.POSITIVE_INFINITY;
    /**Zbyvajici pamet*/
    private int memoryLeft;
    /**Predchazejici router*/
    private Router previous;
    /** List - Sousedi daného routeru*/
    private final Map<Integer, Link> links = new HashMap<>();
    /**Data k odeslani*/
    private final List<Data> dataToSend = new ArrayList<>();
    /**Data ulozena v routeru*/
    private final List<Data> dataToSave = new ArrayList<>();
    /**Prijate nekompletni pakety dat*/
    private final List<Data> recievedData = new ArrayList<>();
    /**Data ke smazani*/
    private final List<Data> dataToRemove = new ArrayList<>();
    /** Nazev routeru*/
    private final String name;

    /**ID routeru*/
    private final int id;
    /**Pamet routeru - maximalni mnozstvi dat, ktere dokaze uchovavat*/
    private static final int MEMORY = 100000000;

    /**
     * Odesila, prechovava a prijima data v siti
     *
     * @param id id routeru
     */
    public Router(int id) {
        this.id = id;
        this.name = "Router" + id;
        this.memoryLeft = MEMORY;
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
            default:
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
        if (dataToSend.size() == 0) {
            return;
        }


        if (id != Dijkstra.getSource()) {
            Dijkstra.computePath(this, world);
        }

        int x;
        for (Data data : dataToSend) {
            world.getLog().addSentData(data);
//            System.out.println(data.toString());
            x = prepareAndSendData(data, world.getLog());
            switch (x) {
                case -1:
                    world.getLog().addAdditionalDataInfo(data.id, "Chyba pri odesilani dat.");
//                    System.out.println("Chyba pri odesilani dat.");
                    break;
                case -2:
                    world.getLog().addAdditionalDataInfo(data.id, "Cast dat prijata.");
//                    System.out.println("Cast dat prijata.");
                    break;
                case -3:
                    world.getLog().addAdditionalDataInfo(data.id, "Data v nespravnem routeru - odeslána do routeru, ktery je odeslal.");
                    data.sourceRouter.carryData(data);
                    dataToRemove.add(data);
//                    System.out.println("Data v nespravnem routeru - odeslána do routeru, ktery je odeslal.");
                    break;
                case -4:
                    world.getLog().addAdditionalDataInfo(data.id, "Cesta k cilovemu routeru neexistuje - data odstranena.");
                    dataToRemove.add(data);
//                    System.out.println("Cesta k cilovemu routeru neexistuje - data odstranena.");
                    break;
                default:
                    world.getLog().addDataDest(data.id, x);
                    if (data.targetRouter.getId() == x) {
                        world.getLog().addAdditionalDataInfo(data.id,"Data dorucena.");
//                        System.out.println("Data dorucena.");
                    } else {
                        world.getLog().addAdditionalDataInfo(data.id,"Data docasne ulozena v Routeru" + x);
//                        System.out.println("Data docasne ulozena v Routeru" + x);
                    }
            }
        }
    }

    @Override
    public void restore(World world) {
        dataToSend.removeAll(dataToRemove);
        recievedData.removeAll(dataToRemove);
        dataToRemove.clear();

//        System.out.println("----- " + dataToSave.size());

        dataToSend.addAll(dataToSave);
        dataToSave.clear();

        memoryLeft = MEMORY;

        for (Data data : dataToSend) {
//            world.getWeb().addDataInRouter();
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

    /**
     * Ulozi data k odeslani
     *
     * @param data data, o ktera se router postara
     */
    public void carryData(Data data) {
        dataToSend.add(data);
    }

    /**
     * Ulozi data do pameti routeru
     *
     * @param data data k ulozeni do pameti
     */
    public void saveData(Data data) {
        dataToSave.add(data);

        this.memoryLeft -= data.amount;
    }

    /**
     * Pripravi a odesle data
     * Ziska cestu, kudy budou data odesilana
     *
     * @param data data k odeslani
     * @return id routeru, kde data na konci ticku zustala
     *          -1 pro neuspech
     */
    private int prepareAndSendData(Data data, Log log) {
        if (data.amount == 0) {
            return data.targetRouter.id;
        }

        int idOnPath = -3;
        List<Router> path = Dijkstra.getShortestPathTo(data.targetRouter);
        log.addDataPath(data.sourceRouter.id, path);
        for (int i = 0; i < path.size(); i++) {
            if (id == path.get(i).id) {
                idOnPath = i;
                break;
            }
        }

        if (path.get(0).id == data.targetRouter.id) {
            dataToRemove.add(data);
            return -4;
        }

        return idOnPath != -3 ? sendData(path, ++idOnPath, data) : -3;
    }

    /**
     * Odesle data do nasledujiciho routeru v ceste
     * Pokud nema link dostatecnou propustnost nebo router pamet pro pripade ulozeni - jsou data rozdelena a odeslano jejich maximalni mnozstvi
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

//        boolean splited = false;
        Link link = links.get(path.get(idOnPath).id);

        int linkCapacity = link.getDirCapacity(id);

        if (linkCapacity < data.amount) {
            saveData(data.splitMe(linkCapacity));
//            splited = true;
        }

        if (idOnPath + 1 < path.size() && path.get(idOnPath + 1).getMemoryLeft() < linkCapacity) {
//            splited = true;
            saveData(data.splitMe(path.get(idOnPath + 1).getMemoryLeft()));
        }

        dataToRemove.add(data);

        return link.sendData(path, idOnPath, data);

//        Integer destinationId = link.sendData(path, idOnPath, data);
//        return destinationId != null ? destinationId.intValue() : id;

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

    /**
     * Nastavi minimalni vzdalenost - Dijkstra
     *
     * @param minDistance minimalni vzdalenost
     */
    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    /**
     * Natavi predchozi router - Dijkstra
     *
     * @param previous predchozi router
     */
    public void setPrevious(Router previous) {
        this.previous = previous;
    }

    /**
     * Ulozi link vedouci k routeru
     *
     * @param link link vedouci k routeru
     */
    public void addLink(Link link) {
        links.put(link.getNeighbourId(id), link);
    }

    /**
     * Vrati nazev routeru
     *
     * @return nazev routeru
     */
    public String getName() {
        return name;
    }

    /**
     * Vrazi List dat k odeslani
     *
     * @return List dat k odeslani
     */
    public List<Data> getData() {
        return dataToSend;
    }

    /**
     * Vrati mnozstvi volne pameti routeru
     *
     * @return mnozstvi vole pameti routeru
     */
    public int getMemoryLeft() {
        return memoryLeft;
    }

    /**
     * Vrati id routeru
     *
     * @return id routeru
     */
    public int getId() {
        return id;
    }

    /**
     * Vrati pamet routeru
     *
     * @return pamet routeru
     */
    public static int getMEMORY() {
        return MEMORY;
    }

    /**
     * Vrati minimalni vzdalenost - Dijkstra
     *
     * @return minimalni vzdalenost
     */
    public double getMinDistance() {
        return minDistance;
    }

    /**
     * Vrati predchozi router - Dijkstra
     *
     * @return prechozi router
     */
    public Router getPrevious() {
        return previous;
    }

    /**
     * Vrati mapu linku vedoucich k routeru
     *
     * @return Mapa linku vechoucich k routeru
     */
    public Map<Integer, Link> getLinks() {
        return links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Router router = (Router) o;

        if (id != router.id) {
            return false;
        }
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