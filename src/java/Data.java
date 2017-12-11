public class Data implements Comparable<Data> {

    /**Id dat*/
    public final int id;
    /**Mnozstvi prenasenych dat*/
    public int amount;
    /**Puvodni mnozstvi dat*/
    public final int originalAmount;
    /**Router vysilajici data*/
    public final Router sourceRouter;
    /**Cilovy router*/
    public final Router targetRouter;
    /**Pocet datovych vytvorenych datovych packetu*/
    private static int count = 0;

    /**
     * Vytvori 'datovy paket' s mnozstvim dat, ktere prenasi a routerem kam.
     *
     * @param sourceRouter vychozi router
     * @param targetRouter cilovy router
     * @param dataAmount mnozstvi prenasenych dat
     */
    public Data(Router sourceRouter, Router targetRouter, int dataAmount) {
        this (sourceRouter, targetRouter, dataAmount, ++count, dataAmount);
    }

    /**
     * 'Datovy paket' vytvareny po rozdeleni vetsiho datoveho paketu
     *
     * @param sourceRouter vychozi router
     * @param targetRouter cilovy router
     * @param dataAmount mnozstvi prenasenych dat
     * @param id id datoveho paketu
     * @param originalDataAmount mnozstvi dat prenasenych puvodnim datovym paketem
     */
    public Data(Router sourceRouter, Router targetRouter, int dataAmount, int id, int originalDataAmount) {
        this.id = id;
        this.sourceRouter = sourceRouter;
        this.targetRouter = targetRouter;
        this.amount = dataAmount;
        this.originalAmount = originalDataAmount;
    }

    /**
     * Rozdeli se na dva objekty o velikosti zadaneho agumentu a jeho rozdilu s celkovym prenasenym mnozstvim dat
     *
     * @param dataAmount mnozstvi dat, ktere bude mit puvodni datovy packet
     * @return {@code Data} s mnozstvim prenasenych dat o velikosti rozdilu
     */
    public Data splitMe(int dataAmount) {
        if (dataAmount < 0) {
            dataAmount = 0;
        }

        if (dataAmount > amount) {
            dataAmount = amount;
        }

        int newDataAmount = this.amount - dataAmount;
        this.amount = dataAmount;

        return new Data(sourceRouter, targetRouter, newDataAmount, id, originalAmount);
    }

    @Override
    public String toString() {
        return "Data[" + id + "] " + sourceRouter.getId() + " ~ " + targetRouter.getId() + " (" + amount + "b)";
    }

    @Override
    public int compareTo(Data o) {
        return Integer.compare(sourceRouter.getId(), o.sourceRouter.getId());
    }
}
