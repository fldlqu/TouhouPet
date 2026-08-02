package k.p.domain.states;

/* JADX INFO: loaded from: classes.dex */
public class ForageState extends BasePetState {
    private static final long serialVersionUID = -7373767904799806636L;
    private static final String stateDescription = "觅食中...";
    private static final String stateName = "觅食";
    private static final String stateTag = "forage";
    private ForageThread forageThread;

    @Override // k.p.domain.states.PetState
    public int getWeight() {
        return (20 - Math.min(getPet().getRepletionDegree(), getPet().getDrinkDegree())) / 2;
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public int getMaxDuration() {
        return Integer.MAX_VALUE;
    }

    @Override // k.p.domain.states.PetState
    public String getStateDoingDescription() {
        return stateDescription;
    }

    @Override // k.p.domain.states.PetState
    public String getStateTag() {
        return stateTag;
    }

    @Override // k.p.domain.states.PetState
    public String getStateName() {
        return stateName;
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public void onStart() {
        this.forageThread = new ForageThread(this);
        this.forageThread.start();
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public void onEnd() {
        if (this.forageThread != null && this.forageThread.loop) {
            this.forageThread.loop = false;
            try {
                this.forageThread.join(500L);
            } catch (InterruptedException e) {
            } finally {
                getPet().requestNewState();
            }
        }
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public void onPause() {
        if (this.forageThread != null && this.forageThread.loop) {
            this.forageThread.loop = false;
            try {
                this.forageThread.join(500L);
            } catch (InterruptedException e) {
            } finally {
                getPet().requestNewState();
            }
        }
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public void onResume() {
        this.forageThread = new ForageThread(this);
        this.forageThread.start();
    }
}
