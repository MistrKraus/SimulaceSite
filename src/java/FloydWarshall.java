import java.util.Stack;

//TODO Dodelat FloydWarshalla
public class FloydWarshall {
    /**Matice cest ~ sousednoti*/
    private float[][] links;
    /**Matice nejkratsich vzdalenosti*/
    private float[][] shortestDistace;
    /**Matice nejdraktsich mezicest*/
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
        //int length = routerCount;
        int x,y;
        float throughtput;
        this.links = new float[routerCount][routerCount];

        for (int i = 0; i < links.length; i++) {
            x = links[i].getR1Id();
            y = links[i].getR2Id();
            throughtput = links[i].getMaxThroughtput();

            this.links[x][y] = throughtput;
            this.links[y][x] = throughtput;
        }

        for (int i = 0; i < routerCount; i++) {
            for (int j = i; j < routerCount; j++) {
                if (i == j)
                    continue;

                if (this.links[i][j] == 0.0f) {
                    this.links[i][j] = 100.0f;
                    this.links[j][i] = 100.0f;
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

        findPaths(this.links);
    }

    /**
     *
     *
     * @param links
     */
    public void findPaths(float[][] links) {
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
                        btw[i][j] = btw[i][k]; /** zjistili jsme, ze pres nahradni bod k je to vyhodnejsi, tudiz nastavime bod k jako mezibod*/
                    }
                }
            }
        }

        for (int i = 0; i < lenght; i++) {
            for (int j = 0; j < lenght; j++) {
                if (shortestDistace[i][j] != 0) /** vypise pouze vzdalenosti tam, kde existuji linky*/
                    System.out.print(shortestDistace[i][j] + " - Routery: " + (i+1) + ", " + (j+1) + "  \n"); //vypisovani nejakych picovin
            }
            System.out.println();
        }
    }

    //TODO Upravit vystupni format cesty (zmenit ze stacku)
    public Stack getPath(int r1, int r2) {
        if (btw[r1][r2] == Integer.MIN_VALUE)
            return new Stack();

        Stack stack = new Stack();



        return stack;
    }
}