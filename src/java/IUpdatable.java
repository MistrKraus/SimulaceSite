import java.io.IOException;

/**
 * Interface pro "aktualizovatelne" soucasti site
 */
public interface IUpdatable {

    /**
     * Aktualizace objektu
     *
     * @param world ridici trida
     */
    void update(World world) throws IOException;

    /**
     * Obnoveni objektu
     *
     * @param world ridici trida
     */
    void restore(World world) throws IOException;
}
