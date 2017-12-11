import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Log implements IWebComp {

    /**Zda se maji loggovat odeslana data*/
    private final boolean saveSentData;
    /**Zda se maji loggovat cesty odeslanych dat*/
    private final boolean saveDataPath;
    /**Zda se maji loggovat routery, kam se data ulozila*/
    private final boolean saveDataDest;
    /**Zda se ma loggovat vyuziti pameti v routerech*/
    private final boolean saveMemUsage;
    /**Zda se ma loggovat vyuziti kapacity linku*/
    private final boolean saveTraffic;

    /**Cislo ticku*/
    private int tickNum;
    /**List odeslanych dat tento tick*/
    private List<Data> sentData = new ArrayList<>();
    /**Mapa cest odeslanych dat*/
    private Map<Integer, List<Integer>> dataPath = new HashMap<>();
    /**Mapa id routeru, kde se data ulozila na konci ticku*/
    private Map<Integer, Integer> dataDest = new HashMap<>();
    /**Dodatecne informace k odeslanym datum*/
    private Map<Integer, String> additionalDataInfo = new HashMap<>();
    /**Vyuziti mapeti routeru*/
    private long memUsage;
    /**Vytizenost linku*/
    private long traffic;

    /**Maximalni vyutiti pameti routeru*/
    private final long MAX_MEM_USAGE;
    /**Maximalni vytizenost linku*/
    private final long MAX_TRAFFIC;

    /**TextArea kam se vypisuji logovane informace*/
    private final TextArea log;
    /**BufferedWriter zapisujici logovane informace do souboru*/
    private BufferedWriter bufferedWriter;

    /**
     * Trida vypisujici stav site
     *
     * @param log TextArea pro vypis
     * @param maxMemUsage Pamet vsech routeru
     * @param maxTraffic Bezeztratova propustnosti vsech linku
     * @param sentData Logovat odeslana data
     * @param dataPath Logovat cesty odeslanych dat
     * @param dataDest Logovat id routeru, kam se data ulozila
     * @param memUsage Logovat vyuziti pameti routeru
     * @param traffic Logovat vytizenost site
     * @throws IOException Zapisovani do souboru selhalo
     */
    public Log(TextArea log, long maxMemUsage, long maxTraffic, //boolean tickNum,
               boolean sentData, boolean dataPath, boolean dataDest, boolean memUsage, boolean traffic) throws IOException {
//        this.saveTickNum = tickNum;
        this.saveSentData = sentData;
        this.saveDataPath = dataPath;
        this.saveDataDest = dataDest;
        this.saveMemUsage = memUsage;
        this.saveTraffic = traffic;

        this.log = log;
        this.MAX_MEM_USAGE = maxMemUsage;
        this.MAX_TRAFFIC = maxTraffic;

        this.bufferedWriter = new BufferedWriter(new FileWriter("log.txt"));
        this.bufferedWriter.write("Data succesfully loaded!\n");
    }

    public void addText(String text) throws IOException {
        bufferedWriter.write(text + "\n");
        log.appendText(text + "\n");
    }

    public void addAdditionalDataInfo(int dataId, String info) {
        additionalDataInfo.put(dataId, info);
    }

    public void setTickNum(int tickNum) {
        this.tickNum = tickNum;
    }

    public void addSentData(Data data) {
        if (saveSentData) {
            this.sentData.add(data);
        }
    }

    public void addDataPath(int sourceRouterId, List<Router> path) {
        if (!saveDataPath)
            return;

        List<Integer> idPath = new ArrayList<>();
        for (Router router : path) {
            idPath.add(router.getId());
        }

        this.dataPath.put(sourceRouterId, idPath);
    }

    public void addDataDest(int dataId, int destinId) {
        if (saveDataDest) {
            this.dataDest.put(dataId, destinId);
        }
    }

    public void setMemUsage(long memUsage) {
        this.memUsage = memUsage;
    }

    public void setTraffic(long traffic) {
        this.traffic = traffic;
    }

    @Override
    public void update(World world) throws IOException {
        String report = getReport();

        log.appendText(report);
        try {
            bufferedWriter.write(report);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        sentData.clear();
        dataPath.clear();
        dataDest.clear();
        additionalDataInfo.clear();
    }

    @Override
    public void restore(World world) throws IOException {
        world.getLog().showSummaryReport();
    }

    @Override
    public void draw(GraphicsContext g, int routersInRow) {

    }

    /**
     * Vrati info o stavu site
     *
     * @return informace o stavu site
     */
    private String getReport() {
        StringBuilder tickReport = new StringBuilder();

//        if (saveTickNum)
        tickReport.append("Tick " + tickNum + "\n");

        if (saveSentData || saveDataPath || saveDataDest) {
            tickReport.append(" Sent data\n");

            for (Data data : sentData) {
                if (saveSentData) {
                    tickReport.append("\t" + data.toString() + "\n");

                    if (saveDataPath) {
                        tickReport.append("\t\tVia routers ");
                        List<Integer> path = dataPath.get(data.sourceRouter.getId());
                        if (path != null) {
                            for (int i = 0; i < path.size() - 1; i++) {
                                tickReport.append(path.get(i) + " ~ ");
                            }

                            tickReport.append(path.get(path.size() - 1) + "\n");
                        }
                    }

                    if (saveDataDest) {
                        tickReport.append("\t\tData saved into router " + dataDest.get(data.id) + "\n");
                        tickReport.append("\t\t " + additionalDataInfo.get(data.id) + "\n");
                    }
                } else {
                    if (saveDataPath) {
                        tickReport.append("\tData[" + data.id + "] Via routers ");
                        for (int i = 0; i < dataPath.size() - 1; i++) {
                            tickReport.append(dataPath.get(data.id).get(i) + " ~ ");
                        }

                        tickReport.append(dataPath.get(data.id).get(dataPath.size()) + "\n");

                        if (saveDataDest) {
                            if (dataDest.get(data.id) != null) {
                                tickReport.append("\t\tData saved into router " + dataDest.get(data.id) + "\n");
                                tickReport.append("\t\t" + additionalDataInfo.get(data.id) + "\n");
                            }
                        }
                    } else {
                        if (dataDest.get(data.id) != null) {
                            tickReport.append("\tData[" + data.id + "] saved into router " + dataDest.get(data.id) + "\n");
                            tickReport.append("\t\t" + additionalDataInfo.get(data.id) + "\n");
                        }
                    }
                }
            }
        }

        //tickReport.append("\n");

        return tickReport.toString();
    }

    /**
     * Vypise a ulozi procentualni vytizenost linku a vyuziti pameti routeru
     */
    public void showSummaryReport() throws IOException {
        if (saveMemUsage) {
            log.appendText(String.format(" Routers Memory Usage %.2f%%\n", getMemUsage()));
            bufferedWriter.write(String.format(" Routers Memory Usage %.2f%%\n", getMemUsage()));
//            System.out.println(" Routers Memory Usage " + Double.toString(round(getMemUsage(), 2)) + "%\n");
        }

        if (saveTraffic) {
            log.appendText(String.format(" Link Traffic %.2f%%\n", getTraffic()));
            bufferedWriter.write(String.format(" Link Traffic %.2f%%\n", getTraffic()));
//            System.out.println(" Link Traffic " + Double.toString(round(getTraffic(), 2)) + "%\n");
        }

        log.appendText("\n");
        bufferedWriter.write("\n");
    }

    /**
     * Uzavre a ulozi soubor s logem
     *
     * @throws IOException chyba pri zapisovani do souboru
     */
    public void saveLog() throws IOException {
        bufferedWriter.write("Simulation ended");
        bufferedWriter.close();

        log.appendText("Simulation ended");
    }

    /**
     * Vrati procentualni vyuziti pameti routeru
     *
     * @return procentualni vyuziti pameti routeru
     */
    public double getMemUsage() {
        return ((double)(MAX_MEM_USAGE - memUsage) / MAX_MEM_USAGE) * 100;
    }

    /**
     * Vrati procentualni vyuziti propustnosti site
     *
     * @return procentualni vyuziti propustnosti site
     */
    public double getTraffic() {
        return ((double)traffic / MAX_TRAFFIC) * 100;
    }

//    private static double round(double value, int places) {
//        if (places < 0) {
//            return -1;
//        }
//
//        BigDecimal bd = new BigDecimal(value);
//        bd = bd.setScale(places, RoundingMode.HALF_UP);
//        return bd.doubleValue();
//    }
}
