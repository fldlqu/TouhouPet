package k.p.animation;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PetAnimation {
    private int currentFrame;
    private int currentLoopIndex;
    private List<PetAnimationInfo> list = new ArrayList();
    private int loop;
    private String name;
    private String[] type;

    public void reset() {
        this.currentFrame = 0;
        this.currentLoopIndex = this.loop;
    }

    public PetAnimationInfo nextFrame() {
        if (this.currentFrame < this.list.size()) {
            PetAnimationInfo info = this.list.get(this.currentFrame);
            PetAnimationInfo info2 = info;
            this.currentFrame++;
            return info2;
        }
        if (this.currentLoopIndex <= 0) {
            return null;
        }
        this.currentLoopIndex--;
        this.currentFrame = 0;
        PetAnimationInfo info3 = this.list.get(this.currentFrame);
        return info3;
    }

    public void addActionInfo(PetAnimationInfo info) {
        this.list.add(info);
    }

    public List<PetAnimationInfo> getList() {
        return this.list;
    }

    public int getLoop() {
        return this.loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    public int getCurrentLoopIndex() {
        return this.currentLoopIndex;
    }

    public void setCurrentLoopIndex(int currentLoopIndex) {
        this.currentLoopIndex = currentLoopIndex;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getType() {
        return this.type;
    }

    public void setType(String[] type) {
        this.type = type;
    }
}
