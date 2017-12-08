import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Ridici trida
 */
public class World {

    /**Objekt s prvky site*/
    private Web web;
    /**Spravce vstupnich dat*/
    private DataManager dataManager;
    /**TextArea pro logovani udalosti*/
    private TextArea log;
    /**Pocet routeru v radku pri vykreslovani*/
    private int routersInRow;
//    /**Celkovy pocet spojeni mezi routery*/
//    private int linkCount;
//    /**Pocet routeru v radku pri vykreslovani*/
//    private int routersInRow;
//    private int deltaXY;
    /**Boolean bezi-li simulace*/
    private boolean isRunning = false;

    private final Duration duration = Duration.millis(100);
    private final KeyFrame oneFrame = new KeyFrame(duration, event -> {
        try {
            update();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });
    private Timeline timeline;
    private final GraphicsContext graphics;

    /**Cesta k vstupnimu souboru s daty*/
    private static final String DATA_INPUT_FILE = "test_input.txt";//"src/resources/PT-graf_site.txt";
    /** Cesta k souboru se simulacnimy daty*/
    private static final String DATA_SIMULATION_FILE = "test_simulace.txt";

    /**
     * Konstruktor
     *
     * @param g graficky kontext, do ktereho se simulace vykresluje
     * @param log TextArea pro logovani udalosti
     */
    public World(GraphicsContext g, TextArea log) {
        graphics = g;
        this.log = log;

        timeline = new Timeline(oneFrame);
        timeline.setCycleCount(Animation.INDEFINITE);

        this.dataManager = new DataManager(DATA_INPUT_FILE, DATA_SIMULATION_FILE, log);
    }

    /**
     * Spusti simulaci (nacteni simulacnich dat)
     */
    public void start() throws IOException, InterruptedException {
        if (web == null)
            createWeb();

        isRunning = true;

        update();

        timeline.play();

//        List<String[]> simulatedData = new ArrayList<>();
//        System.out.println();
//        try (BufferedReader br = new BufferedReader(new FileReader(DATA_SIMULATION_FILE))) {
//            String sCurrentLine;
//            while ((sCurrentLine = br.readLine()) != null) {
//                sCurrentLine = sCurrentLine.replace(" ", "");
//                sCurrentLine = sCurrentLine.replace("-","-");
//                simulatedData.add(sCurrentLine.split("-"));
//                System.out.println(sCurrentLine);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//
//            log.appendText(e.getMessage() + "\n");
//        }
    }

    /**
     * Pozastavi simulaci
     */
    public void pause() {
        isRunning = false;

        timeline.stop();
    }

    /**
     * Ukonci simulaci
     */
    public void stop() {
        isRunning = false;

        // TODO Dokoncit ukonceni simulace

        timeline.stop();

        dataManager = new DataManager(DATA_INPUT_FILE, DATA_SIMULATION_FILE, log);

        log.appendText("Simulation succesfully ended!\n");
    }

    /**
     * Aktualizuje vsechny objekty
     */
    public void update() throws IOException, InterruptedException {
        System.out.println(dataManager.getCurrentTick());

        web.update(this);
        web.restore(this);
        draw();
    }

    /**
     * Vykresli vsechny objekty
     */
    public void draw() {
        graphics.setFill(Color.rgb(20, 20, 20));
        graphics.fillRect(0, 0, graphics.getCanvas().getWidth(), graphics.getCanvas().getHeight());

        web.draw(graphics, routersInRow);

        //int i = 0;
//        for (Map.Entry<Integer, Router> o : routers.entrySet()) {
//            o.getValue().draw(graphics, routersInRow);
//            System.out.println(o.getValue().toString());
//        }

//        it = routers.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            ((Router)pair.getValue()).draw(graphics, routersInRow);
//            it.remove(); // avoids a ConcurrentModificationException
//            System.out.println(routers.size());
//        }
    }

    /**
     * Na zaklade dat vytvori sit - routery a spojeni mezi nimi
     */
    public void createWeb() {
        int maxIndex = dataManager.processWebInput();

        if (maxIndex == -1) {
            log.appendText("Creating web was not succesful");
            return;
        }
        routersInRow = (int)(Math.ceil(Math.sqrt(maxIndex)));

        this.web = new Web(dataManager.getRouters(), dataManager.getLinks());

//        int routerId1;
//        int routerId2;
//
//        String[][] line = new String[linkCount][];
//
//        /**Zjisteni poctu routeru v siti*/
//        for (int i = 0; i < linkCount; i++) {
//            line[i] = loadedData.get(i);
//
//            //TODO Jsou routery cislovane od 0 nebo 1?
//            routerId1 = Integer.parseInt(line[i][0]) - 1;
//            routerId2 = Integer.parseInt(line[i][1]) - 1;
//
//            routers.put(routerId1, new Router(routerId1));
//            routers.put(routerId2, new Router(routerId2));
//        }
//        log.appendText("All routers created succesfully.\n");

//        links = new Link[linkCount];
//
//        /**Ulozeni linku do pole*/
//        for (int i = 0; i < linkCount; i++) {
//            links[i] = new Link(Float.parseFloat(line[i][2]), Float.parseFloat(line[i][3]),
//                    (short)(Integer.parseInt(line[i][0]) - 1), (short)(Integer.parseInt(line[i][1]) - 1));
//
//            routers.get(links[i].getR1Id()).neighbours.put(links[i].getR2Id(), links[i]);
//            Link tmp = new Link(links[i].getTHROUGHTPUT(), links[i].getRELIABILITY(), links[i].getR2Id(), links[i].getR1Id());
//            routers.get(links[i].getR2Id()).neighbours.put(links[i].getR1Id(), tmp);
//            log.appendText("Link " + links[i].getR1Id() + " ~ " + links[i].getR2Id() + " created!\n");
//        }

        // ulozeni sousedu do routeru
//        for (Map.Entry<RouterPair, Link> o : links.entrySet()) {
//            Link link = o.getValue();
//
//            routers.get(link.getR1Id()).addLink(link.getR2Id(), link);
//            routers.get(link.getR2Id()).addLink(link.getR1Id(), link);
//        }

//        this.links = new Link[linkCount][linkCount];
//        String[] line;
//
//        for (int i = 0; i < linkCount; i++) {
//            line = loadedData.get(i);
//
//            //TODO Jsou routery cislovane od 0 nebo 1?
//            routerId1 = (short)(Integer.parseInt(line[0]) - 1);
//            routerId2 = (short)(Integer.parseInt(line[1]) - 1);
//
//            Link link = new Link(Float.parseFloat(line[2]), Float.parseFloat(line[3]),
//                    routerId1, routerId2);
//
//            links[routerId1][routerId2] = link;
//            links[routerId2][routerId1] = link;
//
//            if (maxId < routerId1)
//                maxId = routerId1;
//
//            if (maxId < routerId2)
//                maxId = routerId2;
//
//            log.appendText("Link " + routerId1 + " ~ " + routerId2 + " created!\n");
//        }
//        maxId++;

        draw();

        //new PathsManager(links, routers);

//        Vypsání sousedů routerů/počet hran vedoucích z routerů
//        for (int i = 0; i < maxId; i++) {
//            System.out.println("\nRouter " + i + ": " + routers.get(i).neighbours.values());
//            System.out.println(i + ".Router : " + routers.get(i).getLinkCount());
//        }

        //new FloydWarshall(links, routers.length);
    }

    public Web getWeb() {
        return web;
    }

    public TextArea getLog() {
        return log;
    }

    //private List<Data> loadDataTick

    public Map<Integer, Router> getRouters() { return web.getRouters(); }

    public Map<RouterPair, Link> getLinks() {
        return web.getLinks();
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public List<Data> getDataToSend() throws IOException {
        return dataManager.getTickSimulationData();
    }

    public boolean isRunning() {
        return isRunning;
    }
}
