import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    /**
     * Cislo soucasneho ticku
     */
    private int currentTick;
    /**
     * Buffer s daty o podobě grafu
     */
    private BufferedReader graphData;
    /**
     * Buffer s daty pro simulaci
     */
    private BufferedReader simulationData;
    /**
     * Log
     */
    private final TextArea log;
    /**
     * Prebyvajici data z predchoziho ticku
     */
    private Data excessData;

    /**
     * Mapa routeru
     */
    private Map<Integer, Router> routers = new HashMap<>();
    /**
     * Mapa spoju mezi routery
     */
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
     * Nacte data, ktera se maji odeslat tento tick
     *
     * @return data k odeslani tento tick
     * @throws IOException
     */
    public List<Data> getTickSimulationData() throws IOException {
        List<Data> dataToSend = new ArrayList<>();
        dataToSend.add(excessData);

        String currentLine;
        while ((currentLine = this.simulationData.readLine()) != null) {
//            currentLine = currentLine.replace("\t", "");
//            currentLine = currentLine.replace(" ", "");
//            currentLine = currentLine.replace("-","-");
//
//            String[] currLine = currentLine.split("-");
            String[] currLine = processCurrentLine(currentLine);

            int tick = Integer.parseInt(currLine[0]);
            int sourceRouterId = Integer.parseInt(currLine[1]);
            int targetRouterId = Integer.parseInt(currLine[2]);
            Data data = new Data(routers.get(sourceRouterId), routers.get(targetRouterId),
                    Integer.parseInt(currLine[3]));

            if (tick == currentTick)
                dataToSend.add(data);
            else {
                excessData = data;
                currentTick = tick;
                break;
            }
        }

        //dataToSend.sort(null);

        return dataToSend;
    }

    /**
     * Nacte data o podobe site ze souboru
     * Vytvoří routery a linky mezi nimi
     *
     * @return uspech: nejvetsi index routeru v siti
     *         neuspech: -1
     */
    public int processWebInput() {
        //List<String[]> loadedData = new ArrayList<>();
        int maxIndex = Integer.MIN_VALUE;

        try {
            String currentLine;
            log.appendText("Data succesfully loaded!\n");

            while ((currentLine = graphData.readLine()) != null) {
//                currentLine = currentLine.replace("\t", "");
//                currentLine = currentLine.replace(" ", "");
//                currentLine = currentLine.replace("-","-");
//                //loadedData.add(sCurrentLine.split("-"));
//                //System.out.println(sCurrentLine);
//
//                String[] currLine = currentLine.split("-");
                String[] currLine = processCurrentLine(currentLine);

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

            currentLine = simulationData.readLine();

            String[] currLine = processCurrentLine(currentLine);

            this.currentTick = Integer.parseInt(currLine[0]);
            int r1 = Integer.parseInt(currLine[1]);
            int r2 = Integer.parseInt(currLine[2]);

            this.excessData = new Data(routers.get(r1), routers.get(r2), Integer.parseInt(currLine[3]));

            return maxIndex;
        } catch (IOException e) {
            e.printStackTrace();

            log.appendText(e.getMessage() + "\n");

            return -1;
        }
    }

    /**
     * Rozdeli vstupni retezec
     *
     * @param currentLine nacteny retezec
     * @return pole stringu
     */
    private String[] processCurrentLine(String currentLine) {
        currentLine = currentLine.replace("\t", "");
        currentLine = currentLine.replace(" ", "");
        currentLine = currentLine.replace("-","-");

        return currentLine.split("-");
    }

    public Map<Integer, Router> getRouters() {
        return routers;
    }

    public Map<RouterPair, Link> getLinks() {
        return links;
    }

    public int getCurrentTick() {
        return currentTick;
    }
}
