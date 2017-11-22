public class RouterPair {
    public final int r1;
    public final int r2;

    public RouterPair(int r1, int r2) {
        this.r1 = r1;
        this.r2 = r2;
    }

    public int getR1() {
        return r1;
    }

    public int getR2() {
        return r2;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (RouterPair.class.isAssignableFrom(obj.getClass()))
            return false;

        RouterPair routerPair = (RouterPair) obj;

        return (routerPair.r1 == r1 && routerPair.r2 == r2) || (routerPair.r2 == r1 && routerPair.r1 == r2);
    }

    @Override
    public int hashCode() {
        return 31 * (r1 + r2);
    }
}
