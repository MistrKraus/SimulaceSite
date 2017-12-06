import javafx.scene.canvas.GraphicsContext;

import java.util.HashMap;
import java.util.Map;

public class Web implements IDrawable, IUpdatable {

    /**Mapa routeru*/
    private Map<Integer, Router> routers = new HashMap<>();
    /**Mapa spoju mezi routery*/
    private Map<RouterPair, Link> links = new HashMap<>();

    public Web (Map<Integer, Router> routers, Map<RouterPair, Link> links) {
        this.routers = routers;
        this.links = links;
    }

    @Override
    public void draw(GraphicsContext g, int routersInRow) {
        for (Map.Entry<RouterPair, Link> o : links.entrySet())
            o.getValue().draw(g, routersInRow);
    }

    @Override
    public void update(World world) {

    }

    @Override
    public void restore(World world) {

    }

    public Map<Integer, Router> getRouters() {
        return routers;
    }

    public Map<RouterPair, Link> getLinks() {
        return links;
    }
}
