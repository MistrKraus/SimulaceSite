import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    /**Cislo soucasneho ticku*/
    private int currentTick;
    /**Buffer s daty o podobě grafu*/
    private final BufferedReader graphData;
    /**Buffer s daty pro simulaci*/
    private final BufferedReader simulationData;
    /**Prebyvajici data z predchoziho ticku*/
    private Data excessData;

    /**Mapa routeru*/
    private final Map<Integer, Router> routers = new HashMap<>();
    /**Mapa spoju mezi routery*/
    private final Map<RouterPair, Link> links = new HashMap<>();

    /**
     * Trida starajici se o nacitani dat
     *
     * @param graphFilePath cesta k souboru s grafem
     * @param simulationFilePath cesta k souboru se smiulačními daty
     */
    public DataManager(String graphFilePath, String simulationFilePath) {
        InputStream stream = DataManager.class.getResourceAsStream(graphFilePath);
        this.graphData = new BufferedReader(new InputStreamReader(stream));

        InputStream streamSimulation = DataManager.class.getResourceAsStream(simulationFilePath);
        this.simulationData = new BufferedReader(new InputStreamReader(streamSimulation));
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
        excessData = null;

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

            if (tick == currentTick) {
                dataToSend.add(data);
            }
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
//            log.appendText("Data succesfully loaded!\n");

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

                if (r1 > maxIndex) {
                    maxIndex = r1;
                }

                if (r2 > maxIndex) {
                    maxIndex = r2;
                }

                RouterPair routerPair = new RouterPair(routers.get(r1), routers.get(r2));

                if (links.get(routerPair) == null) {
                    links.put(routerPair, new Link(maxThroughtput, reliability, routerPair));

                    routers.get(r1).addLink(links.get(routerPair));
                    routers.get(r2).addLink(links.get(routerPair));
                } else {
                    links.get(routerPair).addNextLink(new Link(maxThroughtput, reliability, routerPair));
                }

//                RouterPair ma pretizenou metodu equals - proto je poradi vlozeni indexu routeru irelevantni
//                if (r1 < r2) {
//                    links.put(new RouterPair(r1, r2), new Link(maxThroughtput, reliability, r1, r2))
//                }
//                else {
//                    links.put(new RouterPair(r2, r1), new Link(maxThroughtput, reliability, r2, r1));
//                  }
            }
//            log.appendText("Web succesfully created:\n" +
//                    " - " + routers.size() + " nodes\n" +
//                    " - " + links.size() + " links\n");

            currentLine = simulationData.readLine();

            String[] currLine = processCurrentLine(currentLine);

            this.currentTick = Integer.parseInt(currLine[0]);
            int r1 = Integer.parseInt(currLine[1]);
            int r2 = Integer.parseInt(currLine[2]);

            this.excessData = new Data(routers.get(r1), routers.get(r2), Integer.parseInt(currLine[3]));

            return maxIndex;
        } catch (IOException e) {
            e.printStackTrace();

            System.out.println(e.getMessage() + "\n");
//            log.appendText(e.getMessage() + "\n");

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
        String tmp = currentLine;
        tmp = tmp.replace("\t", "");
        tmp = tmp.replace(" ", "");
        tmp = tmp.replace("-","-");

        return tmp.split("-");
    }

    /**
     * Vrati Mapu routeru
     *
     * @return Mapa routeru
     */
    public Map<Integer, Router> getRouters() {
        return routers;
    }

    /**
     * Vrati Mapu linku
     *
     * @return Mapa linku
     */
    public Map<RouterPair, Link> getLinks() {
        return links;
    }

    /**
     * Vrati cislo aktualniho ticku
     *
     * @return cislo aktualniho ticku
     */
    public int getCurrentTick() {
        return currentTick;
    }
}