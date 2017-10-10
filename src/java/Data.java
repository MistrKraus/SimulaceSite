public class Data {
    /**Mnozstvi dat*/
    private short amount;
    /**Cilovy router*/
    private final short targetRouterId;

    /**
     * Vytvori 'datovy paket' s mnozstvim dat, ktere prenasi a id routeru kam.
     *
     * @param dataAmount mnozstvi prenasenych dat
     * @param routerId id ciloveho routeru
     */
    public Data(short dataAmount, short routerId) {
        amount = dataAmount;
        targetRouterId = routerId;
    }
}
