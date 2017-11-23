import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ridici trida
 */
public class World {

    /**Mapa routeru*/
    private Map<Integer, Router> routers = new HashMap<>();
    /**Mapa spoju mezi routery*/
    private Map<RouterPair, Link> links = new HashMap<>();
    /**TextArea pro logovani udalosti*/
    private TextArea log;
    /**Celkovy pocet spojeni mezi routery*/
    private int linkCount;
    /**Pocet routeru v radku pri vykreslovani*/
    private int routersInRow;
    private int deltaXY;
    /**Buffer s daty pro simulaci*/
    BufferedReader simulationData;
    /**Boolean bezi-li simulace*/
    private boolean isRunning = false;

//    private final Duration duration = Duration.millis(100);
//    private final KeyFrame oneFrame = new KeyFrame(duration, event -> update());
//    private Timeline timeline;
    private final GraphicsContext graphics;

    /**Cesta k vstupnimu souboru s daty*/
    private static final String DATA_INPUT_FILE = "test_input.txt";
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

//        timeline = new Timeline(oneFrame);
//        timeline.setCycleCount(Animation.INDEFINITE);

        try {
            this.simulationData = new BufferedReader(new FileReader(DATA_SIMULATION_FILE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.appendText("Loading data Erorr:\n" + e.getMessage());
            try {
                log.appendText("App will be closed automatically in 5 seconds...");
                Thread.sleep(5000);
                Platform.exit();
            } catch (Exception e1) {
                log.appendText("App will be closed automatically.");
                e1.printStackTrace();
                Platform.exit();
            }
        }
    }

    /**
     * Spusti simulaci (nacteni simulacnich dat)
     */
    public void start() throws IOException {
        isRunning = true;

        update();

//        timeline.play();
//
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

        //timeline.stop();
    }

    /**
     * Ukonci simulaci
     */
    public void stop() {
        isRunning = false;

        // TODO Dokoncit ukonceni simulace

        //timeline.stop();
    }

    /**
     * Aktualizuje vsechny objekty
     */
    public void update() throws IOException {
        String inputLine = simulationData.readLine();

        while (isRunning && inputLine != null) {


            draw();
            inputLine = simulationData.readLine();
        }
    }

    /**
     * Vykresli vsechny objekty
     */
    public void draw() {

        graphics.setFill(Color.rgb(20, 20, 20));
        graphics.fillRect(0, 0, 200, 200);

        for (Map.Entry<RouterPair, Link> o : links.entrySet())
            o.getValue().draw(graphics, routersInRow);

        int i = 0;
        for (Map.Entry<Integer, Router> o : routers.entrySet()) {
            o.getValue().draw(graphics, routersInRow);
            System.out.println(o.getValue().toString());
        }

//        it = routers.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            ((Router)pair.getValue()).draw(graphics, routersInRow);
//            it.remove(); // avoids a ConcurrentModificationException
//            System.out.println(routers.size());
//        }
    }

    /**
     * Nacte data o podobe site ze souboru
     * Vytvoří routery a linky mezi nimi
     */
    public void processInput() {
        List<String[]> loadedData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(DATA_INPUT_FILE))) {
            String sCurrentLine;
            log.appendText("Data succesfully loaded!\n");

            while ((sCurrentLine = br.readLine()) != null) {
                sCurrentLine = sCurrentLine.replace(" ", "");
                sCurrentLine = sCurrentLine.replace("-","-");
                //loadedData.add(sCurrentLine.split("-"));
                //System.out.println(sCurrentLine);

                String[] currLine = sCurrentLine.split("-");

                // TODO pokud je indexovani od 0 odstranit "-1"
                int r1 = Integer.parseInt(currLine[0]) - 1;
                int r2 = Integer.parseInt(currLine[1]) - 1;
                float maxThroughtput = Float.parseFloat(currLine[2]) - 1;
                float reliability = Float.parseFloat(currLine[3]);

//                routers.put(r1, new Router(r1));
//                routers.put(r2, new Router(r1));
//                System.out.println(r1 + "\n" + r2);

                RouterPair routerPair = new RouterPair(r1, r2);

                if (routers.get(r1) == null)
                    routers.put(r1, new Router(r1));

                if (routers.get(r2) == null)
                    routers.put(r2, new Router(r2));

                if (links.get(routerPair) == null) {
                    links.put(routerPair, new Link(maxThroughtput, reliability, routerPair));

                    routers.get(r1).addNeighbour(links.get(routerPair));
                    routers.get(r2).addNeighbour(links.get(routerPair));
                } else
                    links.get(routerPair).addNextLink(new Link(maxThroughtput, reliability, routerPair));

//                RouterPair ma pretizenou metodu equals - proto je poradi vlozeni indexu routeru irelevantni
//                if (r1 < r2)
//                    links.put(new RouterPair(r1, r2), new Link(maxThroughtput, reliability, r1, r2));
//                else
//                    links.put(new RouterPair(r2, r1), new Link(maxThroughtput, reliability, r2, r1));
            }
            log.appendText("Web succesfully created:\n" +
                    " - " + routers.size() + " nodes\n" +
                    " - " + links.size() + " links\n");

            createWeb();
        } catch (IOException e) {
            e.printStackTrace();

            log.appendText(e.getMessage() + "\n");
        }
    }

    /**
     * Na zaklade dat vytvori sit - routery a spojeni mezi nimi
     */
    private void createWeb() {
        linkCount = links.size();
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
//            Link tmp = new Link(links[i].getThroughtput(), links[i].getReliability(), links[i].getR2Id(), links[i].getR1Id());
//            routers.get(links[i].getR2Id()).neighbours.put(links[i].getR1Id(), tmp);
//            log.appendText("Link " + links[i].getR1Id() + " ~ " + links[i].getR2Id() + " created!\n");
//        }

        // ulozeni sousedu do routeru
//        for (Map.Entry<RouterPair, Link> o : links.entrySet()) {
//            Link link = o.getValue();
//
//            routers.get(link.getR1Id()).addNeighbour(link.getR2Id(), link);
//            routers.get(link.getR2Id()).addNeighbour(link.getR1Id(), link);
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

        routersInRow = (int)(Math.ceil(Math.sqrt(routers.size())));

        draw();

        //new PathsManager(links, routers);

//        Vypsání sousedů routerů/počet hran vedoucích z routerů
//        for (int i = 0; i < maxId; i++) {
//            System.out.println("\nRouter " + i + ": " + routers.get(i).neighbours.values());
//            System.out.println(i + ".Router : " + routers.get(i).getLinkCount());
//        }

        //new FloydWarshall(links, routers.length);
    }

    public Map<Integer, Router> getRouters() { return this.routers; }

    public Map<RouterPair, Link> getLinks() {
        return this.links;
    }
}
