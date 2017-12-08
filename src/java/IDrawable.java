import javafx.scene.canvas.GraphicsContext;

/**
 * Interface pro kreslitelne soucasti site
 */
public interface IDrawable {

    /**
     * Na graficky kontext vykresli svou instanci
     *
     * @param g
     */
    void draw(GraphicsContext g, int routersInRow);
}
