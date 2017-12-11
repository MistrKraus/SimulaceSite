import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;

/**
 * Interface pro sit a jeji soucasti
 */
public interface IWebComp {

    /**
     * Na graficky kontext vykresli svou instanci
     *
     * @param g
     */
    void draw(GraphicsContext g, int routersInRow);

    /**
     * Aktualizace objektu
     *
     * @param world ridici trida
     */
    void update(World world) throws IOException;

    /**
     * Obnoveni objektu
     *
     * @param world ridici trida
     */
    void restore(World world) throws IOException;
}
