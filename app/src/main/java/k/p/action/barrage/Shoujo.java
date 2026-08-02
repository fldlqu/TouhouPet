package k.p.action.barrage;

/* JADX INFO: loaded from: classes.dex */
public interface Shoujo {
    int getCurrentHP();

    int getMagic();

    int getMaxHP();

    String getName();

    int getSpeed();

    int getStrength();

    void init();

    void onCauseDamage(Shoujo shoujo, int i);

    void onDamaged(Shoujo shoujo, int i);

    void onRoundEnd(Shoujo shoujo, int i);

    void onRoundStart(Shoujo shoujo, int i);

    void sendMessage(String str);

    void setCurrentHP(int i);

    void setMagic(int i);

    void setSpeed(int i);

    void setStrength(int i);
}
