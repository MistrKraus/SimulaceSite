import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Web implements IDrawable, IUpdatable {

    /**Mnozstvi dat v routerech*/
    private int dataInRouters = 0;
    /**Mapa routeru*/
    private Map<Integer, Router> routers = new HashMap<>();
    /**Mapa spoju mezi routery*/
    private Map<RouterPair, Link> links = new HashMap<>();

    public Web (Map<Integer, Router> routers, Map<RouterPair, Link> links) {
        this.routers = routers;
        this.links = links;

        //System.out.println(routers.get(7).getLinks().get(7));
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

        if (dataToSend != null && dataToSend.size() > 0 && dataToSend.get(0) != null) {
            for (Data data : dataToSend) {
                data.sourceRouter.carryData(data);
                if (data.sourceRouter.getId() == Dijkstra.getSource()) {
                    data.sourceRouter.update(world);
                    data.sourceRouter.restore(world);
                }
            }
        } else {
            if (dataInRouters == 0) {
                world.stop();
                return;
            }
        }

        for (Router router : routers.values()) {
            router.update(world);
        }

        //TODO uprava (in/de)krementace!!!
        dataInRouters = 0;
    }

    @Override
    public void restore(World world) {
        for (Router router : routers.values()) {
            router.restore(world);
            //System.out.println(router.toString() + " " + router.getMemoryLeft());
        }

        for (Link link : links.values()) {
            link.restore(world);
        }
    }

    public void addDataInRouter() {
        dataInRouters++;
    }

    public Map<Integer, Router> getRouters() {
        return routers;
    }

    public Map<RouterPair, Link> getLinks() {
        return links;
    }
}
