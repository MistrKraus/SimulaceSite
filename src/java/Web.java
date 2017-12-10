import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Web implements IDrawable, IUpdatable {

    /**Zda jsou v routerech ulozena nejaka data*/
    private boolean dataInRouters = false;
//    /**Mnozstvi packetu v routerech*/
//    private int dataInRouters = 0;
    /**Mapa routeru*/
    private Map<Integer, Router> routers = new HashMap<>();
    /**Mapa spoju mezi routery*/
    private Map<RouterPair, Link> links = new HashMap<>();

    /**Maximalni mnozstvi dat v routerech*/
    private final long MAX_MEMORY_USAGE;
    /**Maximalni vytizeni linku*/
    private final long MAX_TRAFFIC;

    public Web (Map<Integer, Router> routers, Map<RouterPair, Link> links) {
        this.routers = routers;
        this.links = links;

        long temp = 0;
        for (Router router : routers.values()) {
            temp += router.getMemoryLeft();
        }
        MAX_MEMORY_USAGE = temp;

        temp = 0;
        for (Link link : links.values()) {
            temp += link.getCCA_MAX_THROUGHTPUT();
        }
        MAX_TRAFFIC = temp + temp;

        //System.out.println(routers.size());
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

        //System.out.println(" ++ " + dataInRouters);
        if (dataToSend != null && dataToSend.size() > 0 && dataToSend.get(0) != null) {
            //dataInRouters = 0;

            for (Data data : dataToSend) {
                data.sourceRouter.carryData(data);
                if (data.sourceRouter.getId() == Dijkstra.getSource()) {
                    data.sourceRouter.update(world);
                    data.sourceRouter.restore(world);
                }
            }
        } else {
            //if (dataInRouters == 0) {
            if (!dataInRouters) {
                //dataInRouters = 0;
                world.stop();
                return;
            }
        }

        for (Router router : routers.values()) {
            router.update(world);
        }

        //dataInRouters = 0;
    }

    @Override
    public void restore(World world) {
        dataInRouters = false;
        long temp = 0;
        for (Router router : routers.values()) {
            temp += router.getMemoryLeft();
            router.restore(world);
            //System.out.println(router.toString() + " " + router.getMemoryLeft());
        }
        world.getLog().setMemUsage(temp);
        dataInRouters = (MAX_MEMORY_USAGE - temp != 0);
//            dataInRouters

        temp = 0;
        for (Link link : links.values()) {
            temp += link.getCCA_MAX_THROUGHTPUT() - link.getData1to2();
            temp += link.getCCA_MAX_THROUGHTPUT() - link.getData2to1();
            link.restore(world);
        }
        world.getLog().setTraffic(temp);

        world.getLog().showSummaryReport();
    }

//    public void addDataInRouter() {
//        dataInRouters++;
//    }

    public Map<Integer, Router> getRouters() {
        return routers;
    }

    public Map<RouterPair, Link> getLinks() {
        return links;
    }

    public long getMAX_MEMORY_USAGE() {
        return MAX_MEMORY_USAGE;
    }

    public long getMAX_TRAFFIC() {
        return MAX_TRAFFIC;
    }
}
