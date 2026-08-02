package k.p.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import k.p.action.barrage.Flandre;
import k.p.action.barrage.Mokou;
import k.p.action.studyinfo.AroundLakeRunning;
import k.p.action.studyinfo.AttendClass;
import k.p.action.workinfo.CleanCourtyardForYoumu;
import k.p.action.workinfo.PartnerTraining;
import k.p.action.workinfo.TestMedicine;
import k.p.domain.BasePet;
import k.p.domain.Flag;
import k.p.item.drink.BlackTea;
import k.p.item.drink.GreenTea;
import k.p.item.drink.LakeWater;
import k.p.item.drink.Liquor;
import k.p.item.drink.Rinsing;
import k.p.item.drink.ThermalSpring;
import k.p.item.food.BambooShoot;
import k.p.item.food.Cake;
import k.p.item.food.Fish;
import k.p.item.food.HotSpringEgg;
import k.p.item.food.Meat;
import k.p.item.food.Mushroom;
import k.p.item.food.RiceBall;
import k.p.item.food.SteamedBun;
import k.p.item.otheritem.Pillow;
import k.p.item.specialfood.Ghost;
import k.p.location.BaseLocation;
import k.p.location.Location;
import k.p.services.DialogService;
import k.p.view.sliderview.BarrageSliderItemList;

/* JADX INFO: loaded from: classes.dex */
public class LocationService {
    public static Location HOME;
    public static List<Location> locationList;
    public static Map<String, Location> locationMap;

    public static void init() {
        locationMap = new HashMap();
        locationList = new ArrayList();
        HOME = new BaseLocation("家") { // from class: k.p.services.LocationService.1
            @Override // k.p.location.BaseLocation, k.p.location.Location
            public void onEnter() {
                super.onEnter();
                ViewService.sliderView.returnToMainList();
            }

            @Override // k.p.location.Location
            public void init() {
            }
        };
        Location jxq = new BaseLocation("间歇泉") { // from class: k.p.services.LocationService.2
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindItemAction("温泉蛋", HotSpringEgg.class, 20));
                registerLocationAction(new BaseLocation.FindItemAction("温泉水", ThermalSpring.class, 70));
                registerLocationAction(new BaseLocation.FindNothingAction("啥都没找到", 10));
            }
        };
        Location ymjj = new BaseLocation("幽冥结界") { // from class: k.p.services.LocationService.3
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindNothingAction("这里有好多幽灵,要不要捉一只来尝尝呢...", 30));
                registerLocationAction(new BaseLocation.FindNothingAction("这里真的什么都没有...", 60));
                registerLocationAction(new BaseLocation.FindItemAction("幽灵", Ghost.class, 10));
            }
        };
        Location byl = new BaseLocation("白玉楼") { // from class: k.p.services.LocationService.4
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindNothingAction("果然妄图在白玉楼找吃的是不现实的...", 90));
                registerLocationAction(new BaseLocation.LocationAction() { // from class: k.p.services.LocationService.4.1
                    @Override // k.p.location.BaseLocation.LocationAction
                    public void onTrigger(BasePet pet, Location location) {
                        if (pet.getPetSetting("CanCleanCourtyardForYoumu") == null) {
                            pet.setAchievement(pet.getAchievement() + 1);
                            pet.setPetSetting("CanCleanCourtyardForYoumu", new Flag());
                            DialogService.alert("探索", "妖梦每天都要清理这么大的庭院啊...\r\n有空来帮帮她好了!\r\n\r\n新的工作可选 : 清理庭院");
                            ViewService.sliderView.addWork(new CleanCourtyardForYoumu());
                        }
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public int getWeight() {
                        return 10;
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public String getDoneMessage() {
                        return "果然妄图在白玉楼找吃的是不现实的...";
                    }
                });
            }
        };
        Location rjzl = new BaseLocation("人间之里") { // from class: k.p.services.LocationService.5
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindItemAction("清水", Rinsing.class, 30));
                registerLocationAction(new BaseLocation.FindItemAction("饭团", RiceBall.class, 40));
                registerLocationAction(new BaseLocation.FindNothingAction("这里貌似什么都没有...", 20));
                registerLocationAction(new BaseLocation.LocationAction() { // from class: k.p.services.LocationService.5.1
                    @Override // k.p.location.BaseLocation.LocationAction
                    public void onTrigger(BasePet pet, Location location) {
                        if (pet.getPetSetting("CanAttendClass") == null) {
                            pet.setAchievement(pet.getAchievement() + 1);
                            pet.setPetSetting("CanAttendClass", new Flag());
                            DialogService.alert("探索", "慧音好像在这里教算术...\r\n有空来听听吧\r\n\r\n新的学习可选 : 上课");
                            ViewService.sliderView.addStudy(new AttendClass());
                        }
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public int getWeight() {
                        return 10;
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public String getDoneMessage() {
                        return "这里貌似什么都没有...";
                    }
                });
            }
        };
        Location blss = new BaseLocation("博丽神社") { // from class: k.p.services.LocationService.6
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindItemAction("清茶", GreenTea.class, 30));
                registerLocationAction(new BaseLocation.FindNothingAction("这里貌似什么都没有...", 30));
                registerLocationAction(new BaseLocation.FindNothingAction("几天不见,灵梦你又瘦了不少...", 30));
                registerLocationAction(new BaseLocation.LocationAction() { // from class: k.p.services.LocationService.6.1
                    @Override // k.p.location.BaseLocation.LocationAction
                    public void onTrigger(BasePet pet, Location location) {
                        ItemService.addItem(new Meat());
                        ItemService.addItem(new Liquor());
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public int getWeight() {
                        return 10;
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public String getDoneMessage() {
                        return "哇!西瓜又在这里开宴会了\r\n快进去蹭吃蹭喝...\r\n\r\n获得:\r\n酒 x 1 , 肉 x 1";
                    }
                });
            }
        };
        Location xlt = new BaseLocation("香霖堂") { // from class: k.p.services.LocationService.7
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindItemAction("饭团", RiceBall.class, 30));
                registerLocationAction(new BaseLocation.FindItemAction("蛋糕", Cake.class, 15));
                registerLocationAction(new BaseLocation.FindItemAction("包子", SteamedBun.class, 25));
                registerLocationAction(new BaseLocation.FindNothingAction("这里吃的很多,但是乡长盯得很紧啊...", 20));
                registerLocationAction(new BaseLocation.LocationAction() { // from class: k.p.services.LocationService.7.1
                    @Override // k.p.location.BaseLocation.LocationAction
                    public void onTrigger(BasePet pet, Location location) {
                        if (pet.getPetSetting("HasPillow") == null) {
                            if (pet.getPoint() >= 100) {
                                DialogService.confirm("森近 霖之助", "最近新到货一个枕头哦!\r\n有效治疗各种颈椎病!\r\n只要100点一个!", "购买", "取消", new DialogService.CallBack() { // from class: k.p.services.LocationService.7.1.1
                                    @Override // k.p.services.DialogService.CallBack
                                    public void onReturn(boolean retVal) {
                                        if (retVal) {
                                            PetService.pet.changePoint(-100);
                                            ItemService.addItem(new Pillow());
                                        }
                                    }
                                });
                            } else {
                                DialogService.alert("森近 霖之助", "最近新到货一个枕头哦!\r\n有效治疗各种颈椎病!\r\n只要100点一个!", "取消", (DialogService.CallBack) null);
                            }
                        }
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public int getWeight() {
                        return 10;
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public String getDoneMessage() {
                        return "这里吃的很多,但是乡长盯得很紧啊...";
                    }
                });
            }
        };
        Location wzh = new BaseLocation("雾之湖") { // from class: k.p.services.LocationService.8
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindItemAction("鱼", Fish.class, 20));
                registerLocationAction(new BaseLocation.FindItemAction("湖水", LakeWater.class, 40));
                registerLocationAction(new BaseLocation.FindNothingAction("这里貌似什么都没有...", 30));
                registerLocationAction(new BaseLocation.LocationAction() { // from class: k.p.services.LocationService.8.1
                    @Override // k.p.location.BaseLocation.LocationAction
                    public void onTrigger(BasePet pet, Location location) {
                        if (pet.getPetSetting("CanAroundLakeRunning") == null) {
                            pet.setAchievement(pet.getAchievement() + 1);
                            pet.setPetSetting("CanAroundLakeRunning", new Flag());
                            DialogService.alert("探索", "这里风景不错呀\r\n以后可以来这里跑步!\r\n\r\n新的学习可选 : 绕湖跑步");
                            ViewService.sliderView.addStudy(new AroundLakeRunning());
                        }
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public int getWeight() {
                        return 10;
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public String getDoneMessage() {
                        return "这里貌似什么都没有...";
                    }
                });
            }
        };
        Location hmg = new BaseLocation("红魔馆") { // from class: k.p.services.LocationService.9
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindItemAction("红茶", BlackTea.class, 30));
                registerLocationAction(new BaseLocation.FindItemAction("蛋糕", Cake.class, 30));
                registerLocationAction(new BaseLocation.FindNothingAction("16:闲杂人等不要擅自闯入!", 30));
                registerLocationAction(new BaseLocation.FindNothingAction("貌似迷路了...", 30));
                registerLocationAction(new BaseLocation.LocationAction() { // from class: k.p.services.LocationService.9.1
                    @Override // k.p.location.BaseLocation.LocationAction
                    public void onTrigger(BasePet pet, Location location) {
                        if (pet.getPetSetting("CanBeatFlandre") == null) {
                            pet.setAchievement(pet.getAchievement() + 1);
                            pet.setPetSetting("CanBeatFlandre", new Flag());
                            DialogService.alert("探索", "咦?这里有个通往地下室的暗门...\r\n\r\n新的弹幕对象可选 : 芙兰朵露");
                            ViewService.sliderView.addBarrage(new BarrageSliderItemList.BarrageInfo(Flandre.class, "芙兰"));
                        }
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public int getWeight() {
                        return 10;
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public String getDoneMessage() {
                        return "貌似迷路了...";
                    }
                });
            }
        };
        Location mfsl = new BaseLocation("魔法森林") { // from class: k.p.services.LocationService.10
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindItemAction("蘑菇", Mushroom.class, 60));
                registerLocationAction(new BaseLocation.FindNothingAction("貌似迷路了...", 30));
                registerLocationAction(new BaseLocation.LocationAction() { // from class: k.p.services.LocationService.10.1
                    @Override // k.p.location.BaseLocation.LocationAction
                    public void onTrigger(BasePet pet, Location location) {
                        if (pet.getPetSetting("CanPartnerTraining") == null) {
                            pet.setAchievement(pet.getAchievement() + 1);
                            pet.setPetSetting("CanPartnerTraining", new Flag());
                            DialogService.alert("探索", "魔理沙正在找人玩弹幕,陪她玩玩说不定有蘑菇吃!\r\n\r\n新的工作可选 : 陪练");
                            ViewService.sliderView.addWork(new PartnerTraining());
                        }
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public int getWeight() {
                        return 10;
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public String getDoneMessage() {
                        return "这里貌似什么都没有...";
                    }
                });
            }
        };
        Location ymzd = new BaseLocation("夜盲之道") { // from class: k.p.services.LocationService.11
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindNothingAction("这里貌似什么都没有...", 30));
            }
        };
        Location yytzl = new BaseLocation("竹林") { // from class: k.p.services.LocationService.12
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindItemAction("竹笋", BambooShoot.class, 30));
                registerLocationAction(new BaseLocation.FindNothingAction("貌似迷路了...", 60));
                registerLocationAction(new BaseLocation.LocationAction() { // from class: k.p.services.LocationService.12.1
                    @Override // k.p.location.BaseLocation.LocationAction
                    public void onTrigger(BasePet pet, Location location) {
                        if (pet.getPetSetting("CanBeatMokou") == null) {
                            pet.setAchievement(pet.getAchievement() + 1);
                            pet.setPetSetting("CanBeatMokou", new Flag());
                            DialogService.alert("探索", "发现妹红一只!\r\n\r\n新的弹幕对象可选 : 藤原 妹红");
                            ViewService.sliderView.addBarrage(new BarrageSliderItemList.BarrageInfo(Mokou.class, "妹红"));
                        }
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public int getWeight() {
                        return 10;
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public String getDoneMessage() {
                        return "貌似迷路了...";
                    }
                });
            }
        };
        Location yyt = new BaseLocation("永远亭") { // from class: k.p.services.LocationService.13
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindNothingAction("NEET : \"兔子你怎么又ADD了一个机器人!蛋红去接怪.拉远点!\r\n远程不要A了!点了这只机器人!\"", 5));
                registerLocationAction(new BaseLocation.FindNothingAction("NEET : \"黑水!黑水!驱散!驱散!兔子你又踩黑水了!\"", 5));
                registerLocationAction(new BaseLocation.FindNothingAction("NEET : \"点燃!点燃!兔子你的点燃!\"", 5));
                registerLocationAction(new BaseLocation.FindNothingAction("NEET : \"宝珠!宝珠!打宝珠啊兔子!\"", 5));
                registerLocationAction(new BaseLocation.FindNothingAction("NEET : \"莫慌,我已支配BKB在手,一会开雾打了roshan,中推\"", 5));
                registerLocationAction(new BaseLocation.FindNothingAction("NEET : \"我们野区高地那里有眼,一会帝你去排了\"", 5));
                registerLocationAction(new BaseLocation.FindNothingAction("NEET : \"兔子你怎么又被秒了!能出个绿杖不!\"", 5));
                registerLocationAction(new BaseLocation.FindNothingAction("NEET : \"兔子你怎么又被抓了!40分钟就一双鞋是什么情况!\"", 5));
                registerLocationAction(new BaseLocation.FindNothingAction("NEET : \"哦也!终于FC彩虹小马了!\"", 5));
                registerLocationAction(new BaseLocation.FindNothingAction("这里貌似什么都没有...", 20));
                registerLocationAction(new BaseLocation.LocationAction() { // from class: k.p.services.LocationService.13.1
                    @Override // k.p.location.BaseLocation.LocationAction
                    public void onTrigger(BasePet pet, Location location) {
                        if (pet.getPetSetting("CanTestMedicine") == null) {
                            pet.setAchievement(pet.getAchievement() + 1);
                            pet.setPetSetting("CanTestMedicine", new Flag());
                            DialogService.alert("探索", "师匠做的新药都可以当饭吃了...\r\n饿得不行来吃点药吧...\r\n\r\n新的工作可选 : 试药");
                            ViewService.sliderView.addWork(new TestMedicine());
                        }
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public int getWeight() {
                        return 10;
                    }

                    @Override // k.p.location.BaseLocation.LocationAction
                    public String getDoneMessage() {
                        return "这里貌似什么都没有...";
                    }
                });
            }
        };
        Location ygzs = new BaseLocation("妖怪之山") { // from class: k.p.services.LocationService.14
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindNothingAction("守矢神社什么都没有,暂时还不能让你上去哦", 30));
            }
        };
        Location ssss = new BaseLocation("守矢神社") { // from class: k.p.services.LocationService.15
            @Override // k.p.location.Location
            public void init() {
                registerLocationAction(new BaseLocation.FindNothingAction("这里貌似什么都没有...", 30));
            }
        };
        registerLocation(HOME);
        registerLocation(jxq);
        registerLocation(ymjj);
        registerLocation(byl);
        registerLocation(rjzl);
        registerLocation(blss);
        registerLocation(xlt);
        registerLocation(wzh);
        registerLocation(hmg);
        registerLocation(mfsl);
        registerLocation(ymzd);
        registerLocation(yytzl);
        registerLocation(yyt);
        registerLocation(ygzs);
        registerLocation(ssss);
        HOME.getNearbyList().add(jxq);
        jxq.getNearbyList().add(HOME);
        jxq.getNearbyList().add(ymjj);
        jxq.getNearbyList().add(rjzl);
        ymjj.getNearbyList().add(byl);
        ymjj.getNearbyList().add(jxq);
        byl.getNearbyList().add(ymjj);
        rjzl.getNearbyList().add(jxq);
        rjzl.getNearbyList().add(blss);
        rjzl.getNearbyList().add(xlt);
        rjzl.getNearbyList().add(mfsl);
        rjzl.getNearbyList().add(wzh);
        blss.getNearbyList().add(rjzl);
        xlt.getNearbyList().add(rjzl);
        xlt.getNearbyList().add(ymzd);
        wzh.getNearbyList().add(rjzl);
        wzh.getNearbyList().add(hmg);
        wzh.getNearbyList().add(ygzs);
        hmg.getNearbyList().add(wzh);
        mfsl.getNearbyList().add(rjzl);
        ymzd.getNearbyList().add(xlt);
        ymzd.getNearbyList().add(yytzl);
        yytzl.getNearbyList().add(ymzd);
        yytzl.getNearbyList().add(yyt);
        yyt.getNearbyList().add(yytzl);
        ygzs.getNearbyList().add(wzh);
        ssss.getNearbyList().add(ygzs);
        for (Location location : locationList) {
            location.init();
        }
    }

    public static void registerLocation(Location location) {
        locationMap.put(location.getName(), location);
        locationList.add(location);
    }

    public static Location findLocationByName(String locationName) {
        return locationMap.get(locationName);
    }

    public static void release() {
        locationMap = null;
        locationList = null;
        HOME = null;
    }
}
