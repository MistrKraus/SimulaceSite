import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataManager {
    /**Buffer s daty o podobě grafu*/
    private BufferedReader graphData;
    /**Buffer s daty pro simulaci*/
    private BufferedReader simulationData;
    /**Log*/
    private final TextArea log;

    /**Mapa routeru*/
    private Map<Integer, Router> routers = new HashMap<>();
    /**Mapa spoju mezi routery*/
    private Map<RouterPair, Link> links = new HashMap<>();

    public DataManager(String graphFilePath, String simulationFilePath, TextArea log) {
        this.log = log;

        try {
            this.graphData = new BufferedReader(new FileReader(graphFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.appendText("Loading data Erorr:\n" + e.getMessage());
            try {
                log.appendText("App will be closed automatically in 5 seconds...");
                Thread.sleep(5000);
                Platform.exit();
            } catch (Exception e1) {
                log.appendText("App will be closed automatically.");
                e1.printStackTrace();
                Platform.exit();
            }
        }

        try {
            this.simulationData = new BufferedReader(new FileReader(simulationFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.appendText("Loading data Erorr:\n" + e.getMessage());
            try {
                log.appendText("App will be closed automatically in 5 seconds...");
                Thread.sleep(5000);
                Platform.exit();
            } catch (Exception e1) {
                log.appendText("App will be closed automatically.");
                e1.printStackTrace();
                Platform.exit();
            }
        }
    }

    /**
     * Nacte data o podobe site ze souboru
     * Vytvoří routery a linky mezi nimi
     */
    public int processWebInput() {
        //List<String[]> loadedData = new ArrayList<>();
        int maxIndex = Integer.MIN_VALUE;

        try {
            String sCurrentLine;
            log.appendText("Data succesfully loaded!\n");

            while ((sCurrentLine = graphData.readLine()) != null) {
                sCurrentLine = sCurrentLine.replace("\t", "");
                sCurrentLine = sCurrentLine.replace(" ", "");
                sCurrentLine = sCurrentLine.replace("-","-");
                //loadedData.add(sCurrentLine.split("-"));
                //System.out.println(sCurrentLine);

                String[] currLine = sCurrentLine.split("-");

                int r1 = Integer.parseInt(currLine[0]) - 1;
                int r2 = Integer.parseInt(currLine[1]) - 1;
                float maxThroughtput = Float.parseFloat(currLine[2]) - 1;
                float reliability = Float.parseFloat(currLine[3]);

//                routers.put(r1, new Router(r1));
//                routers.put(r2, new Router(r1));
//                System.out.println(r1 + "\n" + r2);
                if (routers.get(r1) == null) {
                    routers.put(r1, new Router(r1));
                }

                if (routers.get(r2) == null) {
                    routers.put(r2, new Router(r2));
                }

                if (r1 > maxIndex)
                    maxIndex = r1;

                if (r2 > maxIndex)
                    maxIndex = r2;

                RouterPair routerPair = new RouterPair(routers.get(r1), routers.get(r2));

                if (links.get(routerPair) == null) {
                    links.put(routerPair, new Link(maxThroughtput, reliability, routerPair));

                    routers.get(r1).addLink(links.get(routerPair));
                    routers.get(r2).addLink(links.get(routerPair));
                } else
                    links.get(routerPair).addNextLink(new Link(maxThroughtput, reliability, routerPair));

//                RouterPair ma pretizenou metodu equals - proto je poradi vlozeni indexu routeru irelevantni
//                if (r1 < r2)
//                    links.put(new RouterPair(r1, r2), new Link(maxThroughtput, reliability, r1, r2));
//                else
//                    links.put(new RouterPair(r2, r1), new Link(maxThroughtput, reliability, r2, r1));
            }
            log.appendText("Web succesfully created:\n" +
                    " - " + routers.size() + " nodes\n" +
                    " - " + links.size() + " links\n");

            return maxIndex;
        } catch (IOException e) {
            e.printStackTrace();

            log.appendText(e.getMessage() + "\n");

            return -1;
        }
    }

    public Map<Integer, Router> getRouters() {
        return routers;
    }

    public Map<RouterPair, Link> getLinks() {
        return links;
    }
}
