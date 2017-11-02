import java.util.ArrayList;
import java.util.List;

//TODO Dodelat FloydWarshalla
public class FloydWarshall {
    /**Matice cest ~ sousednoti*/
    private float[][] links;
    /**Matice nejkratsich vzdalenosti*/
    private float[][] shortestDistace;
    /**Matice nejkraktsich mezicest*/
    private int[][] btw;
    /**Pocet routeru v siti*/
    private int routerCount;

//    /**
//     * Nalezne nejrychlejsi cesty
//     *
//     * @param links matice {@code Link}
//     * @param routerCount pocet routeru v siti
//     */
//    public FloydWarshall(Link[][] links, int routerCount) {
//        int length = links.length;
//        this.links = new float[length][length];
//
//        for (int i = 0; i < length; i++) {
//            for (int j = i; j < length; j++) {
//                if (links[i][j] == null) {
//                    this.links[i][j] = 0;
//                    this.links[j][i] = 0;
//                    continue;
//                }
//
//                this.links[i][j] = links[i][j].getMaxThroughtput();
//                this.links[j][i] = this.links[i][j];
//            }
//        }
//
//        findPaths(this.links);
//    }

    /**
     * Nalezne nejrychlejsi cesty
     *
     * @param links matice {@code Link}
     * @param routerCount pocet routeru v siti
     */
    public FloydWarshall(Link[] links, int routerCount) {
        int length = links.length;
        int x,y;
        float throughtput;
        float bestThroughtput = 0.0f;
        this.links = new float[routerCount][routerCount];

        // nejlepsi propust + 1
        for (Link link : links)
            if (bestThroughtput < link.getMaxThroughtput())
                bestThroughtput = link.getMaxThroughtput();
        bestThroughtput++;

        for (int i = 0; i < length; i++) {
            x = links[i].getR1Id();
            y = links[i].getR2Id();

            // ohodnoceni cesty - cim vetsi propustnost ma, tim mensi hodnota -> lepsi ohodnoceni WTF
            throughtput = bestThroughtput - links[i].getMaxThroughtput();

            this.links[x][y] = throughtput;
            this.links[y][x] = throughtput;
        }

        for (int i = 0; i < routerCount; i++) {
            for (int j = i; j < routerCount; j++) {
                if (i == j)
                    continue;

                if (this.links[i][j] == 0.0f) {
                    this.links[i][j] = Float.POSITIVE_INFINITY;
                    this.links[j][i] = Float.POSITIVE_INFINITY;
                }
            }
        }

//        for (int i = 0; i < length; i++) {
//            for (int j = 0; j < length; j++) {
//                if (this.links[i][j] == null) {
//                    this.links[i][j] = ;
//                }
//            }
//        }

        findPaths();
    }

    /**
     * Nalezne nejlepsi cesty
     */
    private void findPaths() {
        int lenght = links.length;
        this.shortestDistace = new float[lenght][lenght];
        this.btw = new int[lenght][lenght];

        for (int i = 0; i < lenght; i++) {
            for (int j = i; j < lenght; j++) {
                shortestDistace[i][j] = links[i][j];
                shortestDistace[j][i] = links[i][j];

                if (i == j) {
                    btw[i][j] = 0;
                    continue;
                }

                btw[i][j] = -1;
                btw[j][i] = -1;
            }
        }

//        // Naplneni pole btw -1
//        for (int i = 0; i < lenght; i++) {
//            for (int j = i; j < lenght; j++) {
//                if (i != j) {
//                    //btw[i][j] = j + 1; /** meziskok mezi dvìma body nastavíme vlastnì na poslední bod, tudíž u i-j bude poslední bod j*/
//
//                }
//            }
//        }

        System.out.println("----------------------------");

        for (int k = 0; k < lenght; k++) {
            for (int i = 0; i < lenght; i++) {
                for (int j = 0; j < lenght; j++) {
                    if ((shortestDistace[i][k] + shortestDistace[k][j]) < shortestDistace[i][j]) {
                        shortestDistace[i][j] = shortestDistace[i][k] + shortestDistace[k][j];
                        btw[i][j] = k; /** zjistili jsme, ze pres nahradni bod k je to vyhodnejsi, tudiz nastavime bod k jako mezibod*/
                    }
                }
            }
        }

        for (int i = 0; i < lenght; i++) {
            for (int j = 0; j < lenght; j++) {
                //if (shortestDistace[i][j] != 0) /** vypise pouze vzdalenosti tam, kde existuji linky*/
                    System.out.print(shortestDistace[i][j] + " - Routery: " + (i+1) + ", " + (j+1) + "  \n"); //vypisovani nejakych picovin
            }
            System.out.println();
        }

        System.out.println("Cesta mezi routery 2 a 4:");
        List<Integer> path = getPath(1, 3);
        for (Integer i : path) {
            System.out.print((i + 1) + " ");
        }
        System.out.println();
    }

    /**Vrati list integeru s nejlepsi cestou mezi zadanymi routery*/
    public List<Integer> getPath(int r1, int r2) {
        List<Integer> path = new ArrayList<>();

        path.add(r1);
        searchPointToPath(r1, r2, path);

        return path;
    }

    /**Zkompletuje nejlepsi cestu mezi zadanymi routery a vrati ji jako list integeru*/
    private List<Integer> searchPointToPath(int r1, int r2, List<Integer> path) {
        if (btw[r1][r2] == -1)
            path.add(r2);
        else {
            path = searchPointToPath(r1, btw[r1][r2], path);
            path = searchPointToPath(btw[r1][r2], r2, path);
        }

        return path;
    }
}