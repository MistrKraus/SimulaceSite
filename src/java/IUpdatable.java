/**
 * Interface pro "aktualizovatelne" soucasti site
 */
public interface IUpdatable {

    /**
     * Aktualizace objektu
     *
     * @param world ridici trida
     */
    void update(World world);

    /**
     * Obnoveni objektu
     *
     * @param world ridici trida
     */
    void restore(World world);
}
