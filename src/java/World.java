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
    private Link[][] links;
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
     * Spusti simulaci
     */
    public void start() {
        timeline.play();
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

        for (int i = 0; i < linkCount; i++) {
            for (int j = i; j < linkCount; j++) {
                if (links[i][j] != null) {
                    links[i][j].draw(graphics, routersInRow);
                }
            }
        }

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
                loadedData.add(sCurrentLine.split("-"));
                System.out.println(sCurrentLine);
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

        this.links = new Link[linkCount][linkCount];
        String[] line;

        for (int i = 0; i < linkCount; i++) {
            line = loadedData.get(i);

            //TODO Jsou routery cislovane od 0 nebo 1? ~ odstranit minusy o 4 radky niz
            routerId1 = (short)(Integer.parseInt(line[0]) - 1);
            routerId2 = (short)(Integer.parseInt(line[1]) - 1);

            Link link = new Link(Float.parseFloat(line[2]), Float.parseFloat(line[3]),
                    (short)(routerId1), (short)(routerId2));

            links[routerId1][routerId2] = link;
            links[routerId2][routerId1] = link;

            if (maxId < routerId1)
                maxId = routerId1;

            if (maxId < routerId2)
                maxId = routerId2;

            log.appendText("Link " + routerId1 + " ~ " + routerId2 + " created!\n");
        }
        maxId++;

        log.appendText("All links created sucessfully.\n");

        routers = new Router[maxId];
        //System.out.println(maxId);

        for (int i = 0; i < maxId; i++) {
            routers[i] = new Router(i);
            log.appendText("Router " + i + " created!\n");
        }
        log.appendText("All routers created succesfully.\n");

        routersInRow = (int)(Math.floor(Math.sqrt(routers.length)));
    }
}
