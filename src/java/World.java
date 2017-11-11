import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ridici trida
 */
public class World {

    /**Pole vsech routeru*/
    private Router[] routers;
    /**Matice spoju mezi routery*/
    private Link[] links;
    /**TextArea pro logovani udalosti*/
    private TextArea log;

    /**Celkovy pocet spojeni mezi routery*/
    private int linkCount;
    private int routersInRow;
    private int deltaXY;

    private final Duration duration = Duration.millis(100);
    private final KeyFrame oneFrame = new KeyFrame(duration, event -> update());
    private Timeline timeline;
    private final GraphicsContext graphics;

    /**Cesta k vstupnimu souboru s datay*/
    private static final String DATA_INPUT_FILE = "test_input.txt";

    /** Cesta k souboru se simulacnimy daty*/
    private static final String DATA_SIMULATION_FILE = "test_simulace.txt";
    private Router[] routers1;
    private Router[] routers11;

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
    }

    /**
     * Spusti simulaci (nacteni simulacnich dat)
     */
    public void start() {
        timeline.play();

        List<String[]> simulatedData = new ArrayList<>();
        System.out.println();
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_SIMULATION_FILE))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                sCurrentLine.replace(" ", "");
                sCurrentLine.replace("-","-");
                simulatedData.add(sCurrentLine.split("-"));
                System.out.println(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();

            log.appendText(e.getMessage() + "\n");
        }
    }

    /**
     * Pozastavi simulaci
     */
    public void pause() {
        timeline.stop();
    }

    /**
     * Ukonci simulaci
     */
    public void stop() {
        timeline.stop();
    }

    /**
     * Aktualizuje vsechny objekty
     */
    public void update() {


        draw();
    }

    /**
     * Vykresli vsechny objekty
     */
    public void draw() {

        graphics.setFill(Color.rgb(20, 20, 20));
        graphics.fillRect(0, 0, 200, 200);

        for (Link link : links)
            link.draw(graphics, routersInRow);

        for (Router router : routers)
            router.draw(graphics, routersInRow);
    }

    /**
     * Nacte data o podobe site ze souboru
     */
    public void loadData() {
        List<String[]> loadedData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(DATA_INPUT_FILE))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                sCurrentLine.replace(" ", "");
                sCurrentLine.replace("-","-");
                loadedData.add(sCurrentLine.split("-"));
                //System.out.println(sCurrentLine);
            }
            log.appendText("Data succesfully loaded!\n");

            createWeb(loadedData);
        } catch (IOException e) {
            e.printStackTrace();

            log.appendText(e.getMessage() + "\n");
        }
    }

    /**
     * Na zaklade dat vytvori sit - routery a spojeni mezi nimi
     *
     * @param loadedData Nactena vstupni data o podobe site
     */
    private void createWeb(List<String[]> loadedData) {
        linkCount = loadedData.size();
        int maxId = 0;
        short routerId1;
        short routerId2;

        String[][] line = new String[linkCount][];
        /**Zjisteni poctu routeru v siti*/
        for (int i = 0; i < linkCount; i++) {
            line[i] = loadedData.get(i);

            //TODO Jsou routery cislovane od 0 nebo 1?
            routerId1 = (short)(Integer.parseInt(line[i][0]) - 1);
            routerId2 = (short)(Integer.parseInt(line[i][1]) - 1);

            if (maxId < routerId1)
                maxId = routerId1;
            if (maxId < routerId2)
                maxId = routerId2;
        }
        maxId++;

        routers = new Router[maxId];
        //System.out.println(maxId);

        for (int i = 0; i < maxId; i++) {
            routers[i] = new Router(i, "Router" + i);
            log.appendText("Router " + i + " created!\n");
        }
        log.appendText("All routers created succesfully.\n");

        links = new Link[linkCount];

        /**Ulozeni linku do pole*/
        for (int i = 0; i < linkCount; i++) {
            links[i] = new Link(Float.parseFloat(line[i][2]), Float.parseFloat(line[i][3]),
                    (short)(Integer.parseInt(line[i][0]) - 1), (short)(Integer.parseInt(line[i][1]) - 1));
            routers[links[i].getR1Id()].neighbours.add(links[i].getR2Id());
            routers[links[i].getR2Id()].neighbours.add(links[i].getR1Id());
            log.appendText("Link " + links[i].getR1Id() + " ~ " + links[i].getR2Id() + " created!\n");
        }

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

        log.appendText("All links created sucessfully.\n");

        for (Link link : links) {
            routers[link.getR1Id()].addLink();
            routers[link.getR2Id()].addLink();
        }
        log.appendText("Amount of links router is connected with has been recorded to routers.\n");
        log.appendText("Web succesfully created.\n");

        routersInRow = (int)(Math.ceil(Math.sqrt(routers.length)));

        draw();

        new PathsManager(links, routers);

        /* Výpis sousedů routerů
        for (int i = 0; i < maxId; i++) {
            System.out.print("\nRouter " + i + ": ");
            for (int j = 0; j < routers[i].neighbours.size(); j++) {
                System.out.print(routers[i].neighbours.get(j) + " ");
            }
        }
        */
        //new FloydWarshall(links, routers.length);
    }

    public Router[] getRouters() { return this.routers; }

    public Link[] getLinks() {
        return this.links;
    }
}
