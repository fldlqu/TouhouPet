package k.p.domain;

/* JADX INFO: loaded from: classes.dex */
public class Satori extends BasePet {
    private static final long serialVersionUID = 1975771795098734793L;

    public Satori() {
        this.typeName = "Satori";
        this.name = "Satori";
    }

    @Override // k.p.domain.BasePet
    protected void levelUp(int currentLevel) {
        setStrength(getStrength() + 1);
        setSpeed(getSpeed() + 1);
        setMagic(getMagic() + 1);
    }
}
