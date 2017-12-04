public class RouterPair {
    public final Router r1;
    public final Router r2;

    public RouterPair(Router r1, Router r2) {
//        if (r1.getId() < r2.getId()) {
//            this.r1 = r1;
//            this.r2 = r2;
//            return;
//        }

        this.r1 = r1;
        this.r2 = r2;
    }

    @Override
    public String toString() {
        return "RouterPair{" + r1 + " ~ " + r2 + '}';
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
        return 31 * (r1.getId() + r2.getId());
    }
}
