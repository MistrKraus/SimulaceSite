import java.util.*;

public class Dijkstra {

    private static int s = -1;

    /**
     * Vypocita cesty z routeru
     *
     * @param source index routeru, ze ktereho vypocte cesty do ostatnich routeru (index -1 nelze)
     */
    public static void computePath(Router source) {
        source.setMinDistance(0);
        PriorityQueue<Router> routerQueue = new PriorityQueue<>();
        routerQueue.add(source);

        while(!routerQueue.isEmpty()) {
            Router rActual = routerQueue.poll();
            Collection<Link> paths = rActual.getLinks().values();

            for (Link link : paths) {
                Router rFind = link.getNeighbour(rActual);
                double throughtput = link.getMAX_THROUGHTPUT();
                double distanceThroughActual = rActual.getMinDistance() + throughtput;

                if (distanceThroughActual < rFind.getMinDistance()) {
                    routerQueue.remove(rFind);
                    rFind.setMinDistance(distanceThroughActual);
                    rFind.setPrevious(rActual);
                    routerQueue.add(rFind);
                }
            }
        }

        s = source.getId();
    }

//    public static List<Router> getShortestPathTo(Router target) {
//        List<Router> path = new ArrayList<>();
//        for (Router router = target; router != null; router = router.getPrevious())
//            path.add(router);
//
//        Collections.reverse(path);
//        return path;
//    }

    public static List<Router> getShortestPathTo(Router target) {
        List<Router> path = new ArrayList<>();
        for (Router router = target; router != null; router = router.getPrevious())
            path.add(router);

        Collections.reverse(path);
        System.out.println(path);
        return path;
    }

    public static int getSource() {
        return s;
    }
}
