package k.p.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import k.p.domain.states.BasePetState;
import k.p.domain.states.DeadState;
import k.p.item.BaseItem;
import k.p.item.drink.Rinsing;
import k.p.item.food.Apple;
import k.p.listener.PetPropertyChangeEvent;
import k.p.location.Location;
import k.p.main.MainService;
import k.p.services.ListenerService;
import k.p.services.LocationService;
import k.p.services.StateService;
import k.p.utils.ExpUtil;
import local.kcn.utils.LogUtil;

/* JADX INFO: loaded from: classes.dex */
public abstract class BasePet implements Serializable {
    public static final transient int MAX_DRINKDEGREE = 100;
    public static final transient int MAX_ENERGY = 100;
    public static final transient int MAX_LEVEL = 999;
    public static final transient int MAX_REPLETIONDEGREE = 100;
    private static final long serialVersionUID = 2;
    private int achievement;
    private int addExpTime;
    private int changeEnergyTime;
    private int cumulativeAddExpTime;
    private int cumulativeChangeEnergyTime;
    private int cumulativeDecreaseDrinkDegreeTime;
    private int cumulativeDecreaseRepletionDegreeTime;
    private int cumulativeMinute;
    protected int currentExp;
    protected transient Location currentLocation;
    protected String currentLocationName;
    protected BasePetState currentState;
    private int decreaseDrinkDegreeTime;
    private int decreaseRepletionDegreeTime;
    private int defaultAddExpTime;
    private int defaultChangeEnergyTime;
    private int defaultDecreaseDDTime;
    private int defaultDecreaseRDTime;
    protected long lifeTime;
    private int magic;
    protected int minuteCumulativeTime;
    protected String name;
    private int point;
    private int power;
    private int speed;
    private int strength;
    protected String typeName;
    private int changeEnergyValue = -1;
    private int changeRepletionDegreeValue = -1;
    private int changeDrinkDegreeValue = -1;
    protected int level = 1;
    protected int nextLevelExp = ExpUtil.getLevelExp(this.level);
    protected int repletionDegree = 100;
    protected int drinkDegree = 100;
    protected int energy = 100;
    private Map<String, Serializable> petSetting = new HashMap();
    private List<BaseItem> allItems = new ArrayList();

    protected abstract void levelUp(int i);

    public BasePet() {
        this.allItems.add(new Rinsing());
        this.allItems.add(new Rinsing());
        this.allItems.add(new Rinsing());
        this.allItems.add(new Apple());
        this.allItems.add(new Apple());
        this.allItems.add(new Apple());
        this.allItems.add(new Apple());
        this.allItems.add(new Apple());
        this.defaultDecreaseRDTime = 720000;
        this.defaultDecreaseDDTime = 480000;
        this.defaultChangeEnergyTime = 600000;
        this.defaultAddExpTime = 600000;
        this.decreaseRepletionDegreeTime = this.defaultDecreaseRDTime;
        this.decreaseDrinkDegreeTime = this.defaultDecreaseDDTime;
        this.changeEnergyTime = this.defaultChangeEnergyTime;
        this.addExpTime = this.defaultAddExpTime;
        this.cumulativeDecreaseRepletionDegreeTime = this.decreaseRepletionDegreeTime;
        this.cumulativeDecreaseDrinkDegreeTime = this.decreaseDrinkDegreeTime;
        this.cumulativeChangeEnergyTime = this.changeEnergyTime;
        this.cumulativeAddExpTime = this.addExpTime;
        this.currentState = StateService.ACTIVE;
        this.currentLocation = LocationService.HOME;
        this.currentLocationName = "家";
    }

    public void onLoad() {
        this.currentLocation = LocationService.findLocationByName(this.currentLocationName);
        if (this.currentLocation == null) {
            this.currentLocation = LocationService.HOME;
            LogUtil.log("load location fail,null location");
        } else {
            LogUtil.log("load location success");
        }
    }

    public void onSave() {
        this.currentLocationName = this.currentLocation.getName();
    }

    public Serializable getPetSetting(String tag) {
        return this.petSetting.get(tag);
    }

    public void setPetSetting(String tag, Serializable setting) {
        this.petSetting.put(tag, setting);
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BaseItem> getAllItems() {
        return this.allItems;
    }

    public int getLevel() {
        return this.level;
    }

    public int getCurrentExp() {
        return this.currentExp;
    }

    public int getNextLevelExp() {
        return this.nextLevelExp;
    }

    public int getRepletionDegree() {
        return this.repletionDegree;
    }

    public int getDrinkDegree() {
        return this.drinkDegree;
    }

    public int getEnergy() {
        return this.energy;
    }

    public long getLifeTime() {
        return this.lifeTime;
    }

    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public int getStrength() {
        return this.strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMagic() {
        return this.magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public int getPoint() {
        return this.point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getAchievement() {
        return this.achievement;
    }

    public void setAchievement(int achievement) {
        this.achievement = achievement;
    }

    public void changePoint(int point) {
        this.point += point;
    }

    public void changePower(int power) {
        this.power += power;
    }

    public int getDefaultDecreaseRDTime() {
        return this.defaultDecreaseRDTime;
    }

    public void setDefaultDecreaseRDTime(int defaultDecreaseRDTime) {
        this.defaultDecreaseRDTime = defaultDecreaseRDTime;
    }

    public int getDefaultDecreaseDDTime() {
        return this.defaultDecreaseDDTime;
    }

    public void setDefaultDecreaseDDTime(int defaultDecreaseDDTime) {
        this.defaultDecreaseDDTime = defaultDecreaseDDTime;
    }

    public int getDefaultChangeEnergyTime() {
        return this.defaultChangeEnergyTime;
    }

    public void setDefaultChangeEnergyTime(int defaultChangeEnergyTime) {
        this.defaultChangeEnergyTime = defaultChangeEnergyTime;
    }

    public int getDefaultAddExpTime() {
        return this.defaultAddExpTime;
    }

    public void setDecreaseRepletionDegreeTime(int decreaseRepletionDegreeTime) {
        this.decreaseRepletionDegreeTime = decreaseRepletionDegreeTime;
        if (this.cumulativeDecreaseRepletionDegreeTime > decreaseRepletionDegreeTime) {
            this.cumulativeDecreaseRepletionDegreeTime = decreaseRepletionDegreeTime;
        }
    }

    public void setDecreaseDrinkDegreeTime(int decreaseDrinkDegreeTime) {
        this.decreaseDrinkDegreeTime = decreaseDrinkDegreeTime;
        if (this.cumulativeDecreaseDrinkDegreeTime > decreaseDrinkDegreeTime) {
            this.cumulativeDecreaseDrinkDegreeTime = decreaseDrinkDegreeTime;
        }
    }

    public void setChangeEnergyTime(int changeEnergyTime) {
        this.changeEnergyTime = changeEnergyTime;
        if (this.cumulativeChangeEnergyTime > changeEnergyTime) {
            this.cumulativeChangeEnergyTime = changeEnergyTime;
        }
    }

    public void setChangeEnergyValue(int changeEnergyValue) {
        this.changeEnergyValue = changeEnergyValue;
    }

    public void setChangeRepletionDegreeValue(int changeRepletionDegreeValue) {
        this.changeRepletionDegreeValue = changeRepletionDegreeValue;
    }

    public void setChangeDrinkDegreeValue(int changeDrinkDegreeValue) {
        this.changeDrinkDegreeValue = changeDrinkDegreeValue;
    }

    public void setDefaultAddExpTime(int defaultAddExpTime) {
        this.defaultAddExpTime = defaultAddExpTime;
    }

    public void addExp(int exp) {
        if (this.level < 999) {
            ListenerService.notifyListener(new PetPropertyChangeEvent(3, exp));
            this.currentExp += exp;
            while (this.currentExp >= this.nextLevelExp) {
                this.currentExp -= this.nextLevelExp;
                int i = this.level + 1;
                this.level = i;
                levelUp(i);
                this.nextLevelExp = ExpUtil.getLevelExp(this.level);
            }
        }
    }

    public void changeRepletionDegree(int i) {
        ListenerService.notifyListener(new PetPropertyChangeEvent(0, i));
        this.repletionDegree += i;
        if (this.repletionDegree > 100) {
            this.repletionDegree = 100;
        }
        if (this.repletionDegree < 0) {
            this.repletionDegree = 0;
            requestChangeState(StateService.DEAD, MAX_LEVEL);
        }
    }

    public void changeDrinkDegree(int i) {
        ListenerService.notifyListener(new PetPropertyChangeEvent(1, i));
        this.drinkDegree += i;
        if (this.drinkDegree > 100) {
            this.drinkDegree = 100;
        } else if (this.drinkDegree < 0) {
            this.drinkDegree = 0;
            requestChangeState(StateService.DEAD, MAX_LEVEL);
        }
    }

    public void changeEnergy(int i) {
        ListenerService.notifyListener(new PetPropertyChangeEvent(2, i));
        this.energy += i;
        if (this.energy > 100) {
            this.energy = 100;
        } else if (this.energy < 0) {
            this.energy = 0;
            requestChangeState(StateService.DEAD, MAX_LEVEL);
        }
    }

    public void addLifeTime(int time) {
        if (!(this.currentState instanceof DeadState)) {
            this.lifeTime += (long) time;
            this.minuteCumulativeTime += time;
            if (this.minuteCumulativeTime > 60000) {
                this.minuteCumulativeTime -= 60000;
                perMinuteAction();
            }
            this.cumulativeDecreaseRepletionDegreeTime -= time;
            this.cumulativeDecreaseDrinkDegreeTime -= time;
            this.cumulativeChangeEnergyTime -= time;
            this.cumulativeAddExpTime -= time;
            if (this.cumulativeDecreaseRepletionDegreeTime <= 0) {
                this.cumulativeDecreaseRepletionDegreeTime = this.decreaseRepletionDegreeTime;
                changeRepletionDegree(this.changeRepletionDegreeValue);
            }
            if (this.cumulativeDecreaseDrinkDegreeTime <= 0) {
                this.cumulativeDecreaseDrinkDegreeTime = this.decreaseDrinkDegreeTime;
                changeDrinkDegree(this.changeDrinkDegreeValue);
            }
            if (this.cumulativeChangeEnergyTime <= 0) {
                this.cumulativeChangeEnergyTime = this.changeEnergyTime;
                changeEnergy(this.changeEnergyValue);
                if (this.changeEnergyValue > 0 && getPetSetting("HasPillow") != null && new Random().nextInt(100) < 10) {
                    changeEnergy(1);
                }
            }
            if (this.cumulativeAddExpTime <= 0) {
                this.cumulativeAddExpTime = this.addExpTime;
                addExp(1);
            }
            if (this.currentState.addCurrentDuration(time)) {
                requestNewState();
            }
        }
    }

    protected void perMinuteAction() {
        this.cumulativeMinute++;
        if (this.cumulativeMinute > 10) {
            this.cumulativeMinute -= 10;
            perTenMinuteAction();
        }
        if (this.energy < 20 && this.currentLocation == LocationService.HOME) {
            requestChangeState(StateService.SLEEP, 20 - this.energy);
        } else if (this.energy > 30) {
            if (this.repletionDegree < 20 || this.drinkDegree < 20) {
                requestChangeState(StateService.FORAGE, 20 - Math.min(this.repletionDegree, this.drinkDegree));
            }
        }
    }

    public void requestNewState() {
        if (this.currentState instanceof DeadState) {
            startState(StateService.DEAD);
        } else if (this.energy < 20 && this.currentLocation == LocationService.HOME) {
            startState(StateService.SLEEP);
        } else {
            startState(StateService.ACTIVE);
        }
    }

    private void startState(BasePetState state) {
        this.currentState = state;
        this.currentState.onStart();
    }

    protected void perTenMinuteAction() {
    }

    public BasePetState getCurrentState() {
        return this.currentState;
    }

    public Location getCurrentLocation() {
        return this.currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
        this.currentLocationName = currentLocation.getName();
    }

    public String getCurrentLocationName() {
        return this.currentLocationName;
    }

    public boolean requestChangeState(BasePetState state, int requestWeight) {
        if (requestWeight <= this.currentState.getWeight()) {
            return false;
        }
        this.currentState.onInterrupt();
        this.currentState.onEnd();
        startState(state);
        MainService.updateNotification();
        return true;
    }

    public boolean requestChangeState(BasePetState state) {
        return requestChangeState(state, state.getWeight());
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }

    public String getLifeTimeByChinese() {
        return String.valueOf(((this.lifeTime / 1000) / 60) / 60) + "小时";
    }
}
