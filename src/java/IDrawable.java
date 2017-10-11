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

    /**
     * X-ove souradnice instance
     *
     * @return x-ove souradnice
     */
    //double getX();

    /**
     * Y-ove souradnice instance
     *
     * @return y-ove souradnice
     */
    //double getY();
}
