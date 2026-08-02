package k.p.domain.states;

import java.io.Serializable;
import k.p.domain.BasePet;

/* JADX INFO: compiled from: ForageState.java */
/* JADX INFO: loaded from: classes.dex */
class ForageThread extends Thread implements Serializable {
    private static final long serialVersionUID = 3026982885253592204L;
    private ForageState forageState;
    protected boolean loop = true;
    private BasePet pet;

    public ForageThread(ForageState forageState) {
        this.forageState = forageState;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        this.pet = this.forageState.getPet();
        while (this.loop) {
            if (this.pet.getRepletionDegree() > 50 && this.pet.getDrinkDegree() > 50) {
                this.pet.requestNewState();
                this.loop = false;
                return;
            } else {
                try {
                    Thread.sleep(120000L);
                } catch (InterruptedException e) {
                }
                this.pet.changeEnergy(-1);
                if (this.pet.getRepletionDegree() > this.pet.getDrinkDegree()) {
                    this.pet.changeDrinkDegree(1);
                } else {
                    this.pet.changeRepletionDegree(1);
                }
            }
        }
    }
}
