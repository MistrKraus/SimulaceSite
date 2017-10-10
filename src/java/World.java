import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
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

    private Router[] routers;
    private Link[][] links;

    private final Duration duration = Duration.millis(50);
    private final KeyFrame oneFrame = new KeyFrame(duration, event -> update());
    private Timeline timeline;
    private final GraphicsContext graphics;

    /**Cesta k vstupnimu souboru s datay*/
    private static final String DATA_INPUT_FILE = "text_input.txt";

    /**
     * Konstruktor
     *
     * @param g graficky kontext, do ktereho se simulace vykresluje
     */
    public World(GraphicsContext g) {
        graphics = g;

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

    }

    /**
     * Vykresli vsechny objekty
     */
    public void draw() {

    }

    public void loadData() {
        List<String[]> loadedData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(DATA_INPUT_FILE))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                loadedData.add(sCurrentLine.split("-"));
            }

            int linkAmnout = loadedData.size();
            int maxId = 0;

            links = new Link[linkAmnout][linkAmnout];

            String[] line;
            for (int i = 0; i < linkAmnout; i++) {
                line = loadedData.get(i);

                short routerId1 = Short.parseShort(line[0]);
                short routerId2 = Short.parseShort(line[1]);

                Link link = new Link(Float.parseFloat(line[2]), Float.parseFloat(line[3]),
                        routerId1, routerId2);

                links[routerId1][routerId2] = link;
                links[routerId2][routerId1] = link;

                if (maxId < routerId1)
                    maxId = routerId1;

                if (maxId < routerId2)
                    maxId = routerId2;
            }
            routers = new Router[maxId];

            for (int i = 0; i < maxId; i++)
                routers[i] = new Router(i);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
