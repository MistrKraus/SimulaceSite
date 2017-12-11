import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

/**
 * Ridici trida
 */
public class World {

    /**Cislo ticku*/
    private int tickNum = 0;
    /**Pocet routeru v radku pri vykreslovani*/
    private int routersInRow;
    /**Objekt s prvky site*/
    private Web web;
    /**Spravce vstupnich dat*/
    private DataManager dataManager;
    /**Objekt pro loggovani udalosti*/
    private Log log;
    /**Label pro zorbrazeni aktualniho ticku*/
    private Label tickNumberLbl;
    /**Label pro zobrazeni vyuziti pameti routeru aktualnim ticku*/
    private Label memUsageLbl;
    /**Label pro zobrazeni vyuziti maximalniho prutoku linku*/
    private Label trafficLbl;

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
     */
    public World(GraphicsContext g, Label tickNumberLbl, Label memUsageLbl, Label trafficLbl) {
        graphics = g;
        this.tickNumberLbl = tickNumberLbl;
        this.memUsageLbl = memUsageLbl;
        this.trafficLbl = trafficLbl;

        timeline = new Timeline(oneFrame);
        timeline.setCycleCount(Animation.INDEFINITE);

        this.dataManager = new DataManager(DATA_INPUT_FILE, DATA_SIMULATION_FILE);

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
//            logTA.appendText(e.getMessage() + "\n");
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
    public void stop() throws IOException {
        isRunning = false;

        // TODO Dokoncit ukonceni simulace

        timeline.stop();

        dataManager = new DataManager(DATA_INPUT_FILE, DATA_SIMULATION_FILE);

        log.saveLog();
        //logTA.appendText("Simulation succesfully ended!\n");
    }

    /**
     * Aktualizuje vsechny objekty
     */
    public void update() throws IOException, InterruptedException {
        this.log.setTickNum(++tickNum);
        System.out.println(dataManager.getCurrentTick());
        tickNumberLbl.setText("Tick " + tickNum);

        web.update(this);
        //log.update(this);

        if (isRunning) {
            log.update(this);

            web.restore(this);
            log.restore(this);
        }

        memUsageLbl.setText(String.format("Routers Memory Usage\t%.2f%%", log.getMemUsage()));
        trafficLbl.setText(String.format("Links Traffic\t\t\t%.2f%%", log.getTraffic()));

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


    public void logData(TextArea log, /*boolean tickNum,*/ boolean sentData, boolean dataPath,
                   boolean dataDest, boolean memUsage, boolean traffic) throws IOException {
        if (web == null) {
            createWeb();
        }

        this.log = new Log(log, web.getMAX_MEMORY_USAGE(), web.getMAX_TRAFFIC(), //tickNum,
                sentData, dataPath, dataDest, memUsage, traffic);
        this.log.addText("Web succesfully created:\n" +
                " - " + web.getRouters().size() + " nodes\n" +
                " - " + web.getLinks().size() + " links\n");

//        this.log.update(this);
//        this.log.restore(this);
    }

    /**
     * Na zaklade dat vytvori sit - routery a spojeni mezi nimi
     */
    public void createWeb() {
        int maxIndex = dataManager.processWebInput();

        if (maxIndex == -1) {
            System.out.println("Creating web was not succesful");
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
//        logTA.appendText("All routers created succesfully.\n");

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
//            logTA.appendText("Link " + links[i].getR1Id() + " ~ " + links[i].getR2Id() + " created!\n");
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
//            logTA.appendText("Link " + routerId1 + " ~ " + routerId2 + " created!\n");
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

    public Log getLog() {
        return log;
    }

//    public TextArea getLogTA() {
//        return logTA;
//    }

    //private List<Data> loadDataTick

//    public Map<Integer, Router> getRouters() { return web.getRouters(); }
//
//    public Map<RouterPair, Link> getLinks() {
//        return web.getLinks();
//    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public List<Data> getDataToSend() throws IOException {
        return dataManager.getTickSimulationData();
    }

    public void setRunning() {
        isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
