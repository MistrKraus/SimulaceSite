import java.util.Arrays;

public class PathsManager implements IUpdatable {
    /**Matice cest ~ sousednoti*/
    private Link[][] links;
    /**Pole s nejlepsimi cestami pro různé objemi(Y) dat:
     * */
    private FloydWarshall[] paths = new FloydWarshall[5];

    public PathsManager(Link[] links, Router[] routers) {
        int routerCount = routers.length;
        this.links = new Link[routerCount][routerCount];
        //Link[][] tempLinks = new Link[routerCount][routerCount];

        for (Link link : links) {
            this.links[link.getR1Id()][link.getR2Id()] = link;
            this.links[link.getR2Id()][link.getR1Id()] = link;

//            tempLinks[link.getR1Id()][link.getR2Id()] = link;
//            tempLinks[link.getR1Id()][link.getR2Id()] = link;
        }

        paths[0] = new FloydWarshall(links, routerCount);
        links = removeBestLinks(links, routers, false);
        paths[1] = new FloydWarshall(links, routerCount);
        links = removeBestLinks(links, routers, true);
        paths[2] = new FloydWarshall(links, routerCount);
        links = removeBestLinks(links, routers, true);
        paths[3] = new FloydWarshall(links, routerCount);
        links = removeBestLinks(links, routers, true);
        paths[4] = new FloydWarshall(links, routerCount);
    }

    /**
     * Odstraneni nejlepe ohodnocenych linku, pokud se jedna o jedine spojeni s routerem, odstranen neni
     *
     * @param links pole linku
     * @param sortedArray je vkladane pole serazene
     * @return vzestupne setridene pole zadanych linku bez nejlepe hodnocenych cest
     */
    private Link[] removeBestLinks(Link[] links, Router[] routers, boolean sortedArray) {
        if (!sortedArray)
            Arrays.sort(links);

        int newLinksLenght = links.length;
        int i = newLinksLenght - 1;
        int bestThroughtput = links[i].getCcaMaxThroughtput();
        // dokud ma link nejlepe hodnocenou propustnost
        // && oba routery propojene timto linkem, jsou se siti propojeny i jinym linkem
        while (links[i].getCcaMaxThroughtput() == bestThroughtput) {
            if ((routers[links[i].getR1Id()]).getLinkCount() > 1 &&
                routers[links[i].getR2Id()].getLinkCount() > 1)
                newLinksLenght--;
            i--;
        }
        newLinksLenght++;

        Link[] newLinks = new Link[newLinksLenght];
        System.arraycopy(links, 0, newLinks, 0, newLinksLenght);

        return newLinks;
    }

    @Override
    public void update(World world) {

    }
}
