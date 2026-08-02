package k.p.listener;

/* JADX INFO: loaded from: classes.dex */
public class PetPropertyChangeEvent {
    public static final int DRINKDEGREE = 1;
    public static final int ENERGY = 2;
    public static final int EXP = 3;
    public static final int REPLETIONDEGREE = 0;
    private int changeValue;
    private int property;

    public PetPropertyChangeEvent(int property, int changeValue) {
        this.property = property;
        this.changeValue = changeValue;
    }

    public int getProperty() {
        return this.property;
    }

    public int getChangeValue() {
        return this.changeValue;
    }
}
