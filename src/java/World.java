import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

/**
 * Ridici trida
 */
public class World {

    private final Duration duration = Duration.millis(50);
    private final KeyFrame oneFrame = new KeyFrame(duration, event -> update());
    private Timeline timeline;
    private final GraphicsContext graphics;

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
}
