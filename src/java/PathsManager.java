public class PathsManager implements IUpdatable {
    /**Matice cest ~ sousednoti*/
    private Link[][] links;
    /**Pole s nejlepsimi cestami pro různé objemi dat:
     * */
    private FloydWarshall[] paths = new FloydWarshall[5];

    public PathsManager(Link[] links, int routerCount) {
        this.links = new Link[routerCount][routerCount];
        //Link[][] tempLinks = new Link[routerCount][routerCount];

        for (Link link : links) {
            this.links[link.getR1Id()][link.getR2Id()] = link;
            this.links[link.getR2Id()][link.getR1Id()] = link;

//            tempLinks[link.getR1Id()][link.getR2Id()] = link;
//            tempLinks[link.getR1Id()][link.getR2Id()] = link;
        }

//        paths[0] = new FloydWarshall(links, routerCount);
//        links = removeBestLinks(links);
//        paths[1] = new FloydWarshall(links, routerCount);
//        links = removeBestLinks(links);
//        paths[2] = new FloydWarshall(links, routerCount);
//        links = removeBestLinks(links);
//        paths[3] = new FloydWarshall(links, routerCount);
//        links = removeBestLinks(links);
//        paths[4] = new FloydWarshall(links, routerCount);
    }

//    private Link[] removeBestLinks(Link[] links) {
//
//    }

    @Override
    public void update(World world) {

    }
}
