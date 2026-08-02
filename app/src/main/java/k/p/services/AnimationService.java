package k.p.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import k.p.animation.PetAnimation;
import k.p.animation.PetAnimationLoader;
import k.p.exceptions.LoadXMLFailException;
import k.p.main.MainService;
import k.p.utils.EnvironmentUtil;
import local.kcn.utils.LogUtil;

/* JADX INFO: loaded from: classes.dex */
public class AnimationService {
    private static List<PetAnimation> animationList;
    public static int petHeight;
    public static int petWidth;

    public static void init(MainService mainService) {
        animationList = new ArrayList();
        loadAction();
    }

    private static void loadAction() {
        try {
            new PetAnimationLoader(String.valueOf(EnvironmentUtil.getMainPath()) + "/pet/animations/satori.xml").load();
        } catch (LoadXMLFailException e) {
            LogUtil.log("XML加载失败");
            LogUtil.log("Line:" + e.getLineNumber());
            LogUtil.log("Caused:" + e.getMessage());
        }
    }

    public static void registerAnimation(PetAnimation animation) {
        animationList.add(animation);
    }

    public static PetAnimation getAnimationByName(String animationName) {
        for (PetAnimation animation : animationList) {
            if (animationName.equals(animation.getName())) {
                return animation;
            }
        }
        return null;
    }

    public static PetAnimation getRandomAnimationByType(String typeName) {
        List<PetAnimation> tmpList = new ArrayList<>();
        for (PetAnimation animation : animationList) {
            boolean hasType = false;
            String[] type = animation.getType();
            int length = type.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                String type2 = type[i];
                if (!typeName.equals(type2)) {
                    i++;
                } else {
                    hasType = true;
                    break;
                }
            }
            if (hasType) {
                tmpList.add(animation);
            }
        }
        int sz = tmpList.size();
        if (sz > 0) {
            return tmpList.get(new Random().nextInt(sz));
        }
        if ("ACTIVE".equals(typeName)) {
            return null;
        }
        return getRandomAnimationByType("ACTIVE");
    }

    public static void requestChangeAnimation(PetAnimation animation) {
        if (animation != null) {
            animation.reset();
            ViewService.petView.changeAnimation(animation);
        }
    }

    public static void release() {
        animationList = null;
    }
}
