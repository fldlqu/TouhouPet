package k.p.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import k.p.domain.BasePet;
import k.p.item.BaseItem;
import k.p.services.DialogService;
import k.p.services.ItemService;
import k.p.services.PetService;
import local.kcn.utils.LogUtil;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseLocation implements Location {
    protected String description;
    protected String name;
    protected int totalWeight;
    protected List<Location> nearbyList = new ArrayList();
    protected List<LocationAction> locationActionList = new ArrayList();
    private Random random = new Random();
    protected boolean visible = true;

    public interface LocationAction {
        String getDoneMessage();

        int getWeight();

        void onTrigger(BasePet basePet, Location location);
    }

    public BaseLocation(String name) {
        this.name = name;
    }

    @Override // k.p.location.Location
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override // k.p.location.Location
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override // k.p.location.Location
    public List<Location> getNearbyList() {
        return this.nearbyList;
    }

    @Override // k.p.location.Location
    public boolean isVisible() {
        return this.visible;
    }

    @Override // k.p.location.Location
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override // k.p.location.Location
    public void onEnter() {
        PetService.pet.setCurrentLocation(this);
    }

    @Override // k.p.location.Location
    public void onLeave() {
    }

    @Override // k.p.location.Location
    public void onStartSearch() {
    }

    @Override // k.p.location.Location
    public void onFinishSearch() {
        if (this.totalWeight > 0) {
            int currentWeight = 0;
            int sz = this.locationActionList.size();
            int r = this.random.nextInt(this.totalWeight);
            LogUtil.log("r = " + r);
            for (int currentIndex = 0; currentIndex < sz; currentIndex++) {
                LocationAction action = this.locationActionList.get(currentIndex);
                if (r >= currentWeight && r < action.getWeight() + currentWeight) {
                    DialogService.alert("探索", action.getDoneMessage());
                    action.onTrigger(PetService.pet, this);
                    return;
                }
                currentWeight += action.getWeight();
            }
        }
    }

    public void registerLocationAction(LocationAction action) {
        if (action != null) {
            this.locationActionList.add(action);
        }
        this.totalWeight += action.getWeight();
    }

    public class SearchItemInfo {
        private Class<? extends BaseItem> clazz;
        private int weight;

        public SearchItemInfo(Class<? extends BaseItem> clazz, int weight) {
            this.clazz = clazz;
            this.weight = weight;
        }

        public Class<? extends BaseItem> getClazz() {
            return this.clazz;
        }

        public int getWeight() {
            return this.weight;
        }
    }

    public class FindNothingAction implements LocationAction {
        private String message;
        private int weight;

        public FindNothingAction(String message, int weight) {
            this.weight = weight;
            this.message = message;
        }

        @Override // k.p.location.BaseLocation.LocationAction
        public int getWeight() {
            return this.weight;
        }

        @Override // k.p.location.BaseLocation.LocationAction
        public String getDoneMessage() {
            return this.message;
        }

        @Override // k.p.location.BaseLocation.LocationAction
        public void onTrigger(BasePet pet, Location location) {
        }
    }

    public class FindItemAction implements LocationAction {
        private Class<? extends BaseItem> clazz;
        private String itemName;
        private int weight;

        public FindItemAction(String itemName, Class<? extends BaseItem> clazz, int weight) {
            this.clazz = clazz;
            this.weight = weight;
            this.itemName = itemName;
        }

        @Override // k.p.location.BaseLocation.LocationAction
        public int getWeight() {
            return this.weight;
        }

        public Class<? extends BaseItem> getClazz() {
            return this.clazz;
        }

        @Override // k.p.location.BaseLocation.LocationAction
        public String getDoneMessage() {
            return "探索结束,找到" + this.itemName;
        }

        @Override // k.p.location.BaseLocation.LocationAction
        public void onTrigger(BasePet pet, Location location) {
            BaseItem item = null;
            try {
                item = getClazz().newInstance();
            } catch (Exception e) {
            }
            if (item != null) {
                ItemService.addItem(item);
            }
        }
    }
}
