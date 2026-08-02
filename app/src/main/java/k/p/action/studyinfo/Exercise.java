package k.p.action.studyinfo;

import java.util.Random;
import k.p.action.StudyAction;
import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Exercise extends StudyAction.BaseStudyInfo {
    private static final long serialVersionUID = -2190327041412242670L;

    @Override // k.p.action.StudyAction.StudyInfo
    public void onDone(BasePet pet) {
        pet.changeEnergy(-1);
        if (pet.getPower() >= 50) {
            pet.changePower(-50);
            int r = new Random().nextInt(10);
            if (r < 30) {
                pet.setStrength(pet.getStrength() + 5);
                return;
            }
            if (r < 60) {
                pet.setSpeed(pet.getSpeed() + 5);
            } else {
                if (r < 90) {
                    pet.setMagic(pet.getMagic() + 5);
                    return;
                }
                pet.setStrength(pet.getStrength() + 3);
                pet.setSpeed(pet.getSpeed() + 3);
                pet.setMagic(pet.getMagic() + 3);
            }
        }
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getName() {
        return "修炼";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public int getMaxDuration() {
        return 3600000;
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getStartMessage() {
        return "消耗时间 : 60分钟\r\n\r\n消耗精力 : 1\r\n消耗P点 : 50\r\n就是P点换属性啦...";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getDoneMessage() {
        return "修炼完成\r\n属性得到了提升!";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public boolean canDone(BasePet pet) {
        return pet.getPower() >= 50;
    }
}
