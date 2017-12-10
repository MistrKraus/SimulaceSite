import javafx.scene.control.TextArea;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Log implements IUpdatable {

    private final boolean saveTickNum;
    private final boolean saveSentData;
    private final boolean saveDataPath;
    private final boolean saveDataDest;
    private final boolean saveMemUsage;
    private final boolean saveTraffic;

    private int tickNum;
    private List<Data> sentData = new ArrayList<>();
    private Map<Integer, List<Integer>> dataPath = new HashMap<>();
    private Map<Integer, Integer> dataDest = new HashMap<>();
    private Map<Integer, String> additionalDataInfo = new HashMap<>();
    private long memUsage;
    private long traffic;

    private final long MAX_MEM_USAGE;
    private final long MAX_TRAFFIC;

    private final TextArea log;
    private BufferedWriter bufferedWriter;

    public Log(TextArea log, long maxMemUsage, long maxTraffic, boolean tickNum,
               boolean sentData, boolean dataPath, boolean dataDest, boolean memUsage, boolean traffic) throws IOException {
        this.saveTickNum = tickNum;
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
        if (saveTickNum) {
            this.tickNum = tickNum;
        }
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
        tickNum = -1;
        sentData.clear();
        dataPath.clear();
        dataDest.clear();
        memUsage = -1;
        traffic = -1;
    }

    @Override
    public void restore(World world) {
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

    private String getReport() {
        StringBuilder tickReport = new StringBuilder();

        if (saveTickNum) {
            tickReport.append("Tick " + tickNum + "\n");
        }

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

        tickReport.append("\n");

        return tickReport.toString();
    }

    public void showSummaryReport() {
        if (saveMemUsage) {
            log.appendText(" Routers Memory Usage " + Double.toString(round(getMemUsage(), 2)) + "%\n");
            System.out.println(" Routers Memory Usage " + Double.toString(round(getMemUsage(), 2)) + "%\n");
        }

        if (saveTraffic) {
            log.appendText(" Link Traffic " + Double.toString(round(getTraffic(), 2)) + "%\n");
            System.out.println(" Link Traffic " + Double.toString(round(getTraffic(), 2)) + "%\n");
        }

        log.appendText("\n");
        System.out.println();
    }

    public void saveLog() throws IOException {
        bufferedWriter.write("Simulation ended");
        bufferedWriter.close();

        log.appendText("Simulation ended");
    }

    public double getMemUsage() {
        return ((double)(MAX_MEM_USAGE - memUsage) / MAX_MEM_USAGE) * 100;
    }

    public double getTraffic() {
//        System.out.println("----------");
//        System.out.println(traffic);
//        System.out.println(MAX_TRAFFIC);
//        System.out.println("----------");
        return ((double)traffic / MAX_TRAFFIC) * 100;
    }

    private static double round(double value, int places) {
        if (places < 0) {
            return -1;
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
