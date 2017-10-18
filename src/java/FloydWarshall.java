import java.util.Stack;

//TODO Dodelat FloydWarshalla
public class FloydWarshall {
    /**Matice cest ~ sousednoti*/
    private float[][] links;
    /**Matice nejkratsich vzdalenosti*/
    private float[][] shortestDistace;
    /**Matice nejdraktsich mezicest*/
    private int[][] btw;

    /**
     * Nalezne nejrychlejsi cesty
     *
     * @param links matice {@code Link}
     */
    public FloydWarshall(Link[][] links) {
        int length = links.length;
        this.links = new float[length][length];

        for (int i = 0; i < length; i++) {
            for (int j = i; j < length; j++) {
                if (links[i][j] == null) {
                    this.links[i][j] = 1;
                    this.links[j][i] = 1;
                    continue;
                }

                this.links[i][j] = links[i][j].getMaxThroughtput();
                this.links[j][i] = this.links[i][j];
            }
        }

        findPaths(this.links);
    }

    public void findPaths(float[][] links) {
        int lenght = links.length;

        this.shortestDistace = new float[lenght][lenght];
        this.btw = new int[lenght][lenght];

        for (int i = 0; i < lenght; i++) {
            for (int j = i; j < lenght; j++) {
                shortestDistace[i][j] = links[i][j];
                shortestDistace[j][i] = links[i][j];
                btw[i][j] = Integer.MIN_VALUE;
                btw[j][i] = Integer.MIN_VALUE;

            }
        }

        System.out.println("----------------------------");

        for (int i = 0; i < lenght; i++) {
            for (int j = 0; j < lenght; j++) {
                for (int k = 0; k < lenght; k++) {
                    if (shortestDistace[j][i] + shortestDistace[i][k] > shortestDistace[j][k]) {
                        shortestDistace[j][k] = shortestDistace[j][i] + shortestDistace[i][k];
                        btw[j][k] = i;
                    }
                }
            }
        }

        for (int i = 0; i < lenght; i++) {
            for (int j = 0; j < lenght; j++) {
                System.out.print(shortestDistace[i][j] + "  ");
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