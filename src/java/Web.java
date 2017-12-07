import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
        for (Link link : links.values()) {
            link.draw(g, routersInRow);
        }

        for (Router router : routers.values()) {
            router.draw(g, routersInRow);
        }
    }

    @Override
    public void update(World world) throws IOException {
        List<Data> dataToSend = world.getDataToSend();

        if (dataToSend != null && dataToSend.size() > 0) {
            for (Data data : dataToSend) {
                data.sourceRouter.carryData(data);
                if (data.sourceRouter.getId() == Dijkstra.getSource()) {
                    data.sourceRouter.update(world);
                    data.sourceRouter.restore(world);
                }
            }
        }

        for (Router router : routers.values()) {
            router.update(world);
        }
    }

    @Override
    public void restore(World world) {
        for (Router router : routers.values()) {
            router.restore(world);
        }

        for (Link link : links.values()) {
            link.restore(world);
        }
    }

    public Map<Integer, Router> getRouters() {
        return routers;
    }

    public Map<RouterPair, Link> getLinks() {
        return links;
    }
}
