package cheron_ezzaamari_soysal.model;

/*public final class Id {

    private static class Holder {
        private static final AtomicInteger id = new AtomicInteger();
    }

    public static int getId() {
        return Holder.id.get();
    }

    public static int increment() {
        return Holder.id.incrementAndGet();
    }



    private static final Id instance = new Id();

    private Id() {
    }

    public static final Id getInstance(){
        return instance;
    }
}*/

public class Id {


    private static int number;

    /**
     *
     */
    private static Id id;

    public int getNumber() {
        return number;

    }

    public static Id getId() {
        if (id == null)  id = new Id();
        number++;
        return id;
    }
}
