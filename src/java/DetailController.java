import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class DetailController implements Initializable {
    /**ListView se vsemi routery*/
    public ListView allRoutersLW;
    /**ListView s routery s ulozenymi daty*/
    public ListView carryingRoutersLW;
    /**ListView se vsemi linky*/
    public ListView allLinksLW;

    /**TextArea, kde se vypisuji detaily o zvolenem routeru*/
    public TextArea detailRouterTA;
    /**TextArea, kde se vypisuji detaily o zvolenem linku*/
    public TextArea detailLinkTA;

    /**List se vsemi routery*/
    private final ObservableList<Router> routers = FXCollections.observableArrayList();
    /**List s routety s ulozenymi daty*/
    private final ObservableList<Router> routersWithData = FXCollections.observableArrayList();
    /**List se vsemi linky*/
    private final ObservableList<Link> links = FXCollections.observableArrayList();

    /**
     * Trida ridici okno s detaily o siti
     *
     * @param routers Mapa vsech routeru v siti
     * @param links Mapa vsech linku v siti
     */
    public DetailController(Map<Integer,Router> routers, Map<RouterPair,Link> links) {
        this.routers.addAll(routers.values());
        this.links.addAll(links.values());

        //refresh();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allRoutersLW.setItems(routers);
        carryingRoutersLW.setItems(routersWithData);
        allLinksLW.setItems(links);
    }

    /**Obnovi list s routery s ulozenymi daty*/
    @FXML
    public void refresh() {
        routersWithData.clear();
        for (Router router : routers) {
            if (router.getData().size() > 0) {
                routersWithData.add(router);
            }
        }
        detailRouterTA.clear();
        detailLinkTA.clear();
    }

    /**
     * Necha vypsat detaily o routeru
     */
    public void routerDetailAll(MouseEvent mouseEvent) {
        showRouterDetail((Router) allRoutersLW.getSelectionModel().getSelectedItem());
    }

    /**
     * Necha vypsat detaily o routeru s daty
     */
    public void routerDetailData(MouseEvent mouseEvent) {
        showRouterDetail((Router) carryingRoutersLW.getSelectionModel().getSelectedItem());
    }

    /**
     * Vypise detaily o routeru
     *
     * @param router Router, o kterem se vypisi detaiily
     */
    private void showRouterDetail(Router router) {
        detailRouterTA.clear();
        detailRouterTA.appendText(router.toString() + "\n Data to send:\n");
        for (Data data : router.getData()) {
            detailRouterTA.appendText(" - " + data.toString() + "\n");
        }
        detailRouterTA.appendText(" Memory left: " + router.getMemoryLeft() + "\n");
        detailRouterTA.appendText(" Neighbors:\n");
        for (Link link : router.getLinks().values()) {
            detailRouterTA.appendText(" - " + link.getNeighbourId(router.getId()) + "\n");
        }
    }

    /**
     * Vypise detaily o linku
     */
    public void linkDetail(MouseEvent mouseEvent) {
        Link link = (Link) allLinksLW.getSelectionModel().getSelectedItem();

        detailLinkTA.clear();
        detailLinkTA.appendText(link.toString() + "\n Maximal throughtput: " + link.getCCA_MAX_THROUGHTPUT() + "\n");
        detailLinkTA.appendText(String.format(" Reliability: %.3f", link.getRELIABILITY()));
        detailLinkTA.appendText("\n Capacity left for: \n - direction 1: " + Integer.toString(link.getDirCapacity(link.getR1Id())));
        detailLinkTA.appendText("\n - direction 2: " + Integer.toString(link.getDirCapacity(link.getR2Id())));

    }
}
