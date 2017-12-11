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
     * Ridici trida
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

    /**
     * Vytvori log, nastavi, jaka data se budou ukladat
     *
     * @param log TextArea kam se po kazdem kroku vypise info o siti
     * @param sentData Logovat odeslana data
     * @param dataPath Logovat cesty odeslanych dat
     * @param dataDest Logovat id routeru, kam se data ulozila
     * @param memUsage Logovat vyuziti pameti routeru
     * @param traffic Logovat vytizenost site
     * @throws IOException
     */
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

        draw();
    }

    /**
     * Vrati instanci {@code Web}
     *
     * @return instance {@code Web}
     */
    public Web getWeb() {
        return web;
    }

    /**
     * Vrati {@code Log}
     *
     * @return {@code Log}
     */
    public Log getLog() {
        return log;
    }

    /**
     * Vrati instanci {@code DataManager}
     *
     * @return instance {@code DataManager}
     */
    public DataManager getDataManager() {
        return dataManager;
    }

    /**
     * Vrati list dat k odeslani v tomto ticku
     *
     * @return list dat k odeslani
     * @throws IOException chyba pri cteni souboru
     */
    public List<Data> getDataToSend() throws IOException {
        return dataManager.getTickSimulationData();
    }

    /**
     * Nastavi informaci o behu simulace na pravdivou
     */
    public void setRunning() {
        isRunning = true;
    }

    /**
     * Vrati zda simulace bezi
     *
     * @return zda simulace bezi
     */
    public boolean isRunning() {
        return isRunning;
    }
}
