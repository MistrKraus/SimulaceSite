import java.util.*;

public class Dijkstra {

    public static void computePath(Router source) {
        source.setMinDistance(0);
        PriorityQueue<Router> routerQueue = new PriorityQueue<>();
        routerQueue.add(source);

        while(!routerQueue.isEmpty()) {
            Router rActual = routerQueue.poll();
            List<Link> paths = rActual.getLinks();

            for (Link link : paths) {
                Router rFind = link.getNeighbour(rActual);
                double throughtput = link.getMaxThroughtput();
                double distanceThroughActual = rActual.getMinDistance() + throughtput;

                if (distanceThroughActual < rFind.getMinDistance()) {
                    routerQueue.remove(rFind);
                    rFind.setMinDistance(distanceThroughActual);
                    rFind.setPrevious(rActual);
                    routerQueue.add(rFind);
                }
            }
        }
    }

    public static List<Router> getShortestPathTo(Router target) {
        List<Router> path = new ArrayList<>();
        for (Router router = target; router != null; router = router.getPrevious()) {
            path.add(router);
        }
        Collections.reverse(path);
        return path;
    }


}
