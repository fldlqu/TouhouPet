package k.p.services

import k.p.action.barrage.Flandre
import k.p.action.barrage.Mokou
import k.p.action.studyinfo.AroundLakeRunning
import k.p.action.studyinfo.AttendClass
import k.p.action.workinfo.CleanCourtyardForYoumu
import k.p.action.workinfo.PartnerTraining
import k.p.action.workinfo.TestMedicine
import k.p.domain.BasePet
import k.p.domain.Flag
import k.p.item.drink.BlackTea
import k.p.item.drink.GreenTea
import k.p.item.drink.LakeWater
import k.p.item.drink.Liquor
import k.p.item.drink.Rinsing
import k.p.item.drink.ThermalSpring
import k.p.item.food.BambooShoot
import k.p.item.food.Cake
import k.p.item.food.Fish
import k.p.item.food.HotSpringEgg
import k.p.item.food.Meat
import k.p.item.food.Mushroom
import k.p.item.food.RiceBall
import k.p.item.food.SteamedBun
import k.p.item.otheritem.Pillow
import k.p.item.specialfood.Ghost
import k.p.location.BaseLocation
import k.p.location.Location
import k.p.view.sliderview.BarrageSliderItemList

object LocationService {
    @JvmField
    var HOME: Location? = null
    @JvmField
    var locationList: MutableList<Location>? = null
    @JvmField
    var locationMap: MutableMap<String, Location>? = null

    @JvmStatic
    fun init() {
        locationMap = HashMap()
        locationList = ArrayList()
        HOME = object : BaseLocation("家") {
            override fun onEnter() {
                super.onEnter()
                ViewService.sliderView!!.returnToMainList()
            }

            override fun init() {
            }
        }
        val jxq: Location = object : BaseLocation("间歇泉") {
            override fun init() {
                registerLocationAction(this.FindItemAction("温泉蛋", HotSpringEgg::class.java, 20))
                registerLocationAction(this.FindItemAction("温泉水", ThermalSpring::class.java, 70))
                registerLocationAction(this.FindNothingAction("啥都没找到", 10))
            }
        }
        val ymjj: Location = object : BaseLocation("幽冥结界") {
            override fun init() {
                registerLocationAction(this.FindNothingAction("这里有好多幽灵,要不要捉一只来尝尝呢...", 30))
                registerLocationAction(this.FindNothingAction("这里真的什么都没有...", 60))
                registerLocationAction(this.FindItemAction("幽灵", Ghost::class.java, 10))
            }
        }
        val byl: Location = object : BaseLocation("白玉楼") {
            override fun init() {
                registerLocationAction(this.FindNothingAction("果然妄图在白玉楼找吃的是不现实的...", 90))
                registerLocationAction(object : BaseLocation.LocationAction {
                    override fun onTrigger(pet: BasePet, location: Location?) {
                        if (pet.getPetSetting("CanCleanCourtyardForYoumu") == null) {
                            pet.setAchievement(pet.getAchievement() + 1)
                            pet.setPetSetting("CanCleanCourtyardForYoumu", Flag())
                            DialogService.alert("探索", "妖梦每天都要清理这么大的庭院啊...\r\n有空来帮帮她好了!\r\n\r\n新的工作可选 : 清理庭院")
                            ViewService.sliderView!!.addWork(CleanCourtyardForYoumu())
                        }
                    }

                    override fun getWeight(): Int = 10

                    override fun getDoneMessage(): String = "果然妄图在白玉楼找吃的是不现实的..."
                })
            }
        }
        val rjzl: Location = object : BaseLocation("人间之里") {
            override fun init() {
                registerLocationAction(this.FindItemAction("清水", Rinsing::class.java, 30))
                registerLocationAction(this.FindItemAction("饭团", RiceBall::class.java, 40))
                registerLocationAction(this.FindNothingAction("这里貌似什么都没有...", 20))
                registerLocationAction(object : BaseLocation.LocationAction {
                    override fun onTrigger(pet: BasePet, location: Location?) {
                        if (pet.getPetSetting("CanAttendClass") == null) {
                            pet.setAchievement(pet.getAchievement() + 1)
                            pet.setPetSetting("CanAttendClass", Flag())
                            DialogService.alert("探索", "慧音好像在这里教算术...\r\n有空来听听吧\r\n\r\n新的学习可选 : 上课")
                            ViewService.sliderView!!.addStudy(AttendClass())
                        }
                    }

                    override fun getWeight(): Int = 10

                    override fun getDoneMessage(): String = "这里貌似什么都没有..."
                })
            }
        }
        val blss: Location = object : BaseLocation("博丽神社") {
            override fun init() {
                registerLocationAction(this.FindItemAction("清茶", GreenTea::class.java, 30))
                registerLocationAction(this.FindNothingAction("这里貌似什么都没有...", 30))
                registerLocationAction(this.FindNothingAction("几天不见,灵梦你又瘦了不少...", 30))
                registerLocationAction(object : BaseLocation.LocationAction {
                    override fun onTrigger(pet: BasePet, location: Location?) {
                        ItemService.addItem(Meat())
                        ItemService.addItem(Liquor())
                    }

                    override fun getWeight(): Int = 10

                    override fun getDoneMessage(): String = "哇!西瓜又在这里开宴会了\r\n快进去蹭吃蹭喝...\r\n\r\n获得:\r\n酒 x 1 , 肉 x 1"
                })
            }
        }
        val xlt: Location = object : BaseLocation("香霖堂") {
            override fun init() {
                registerLocationAction(this.FindItemAction("饭团", RiceBall::class.java, 30))
                registerLocationAction(this.FindItemAction("蛋糕", Cake::class.java, 15))
                registerLocationAction(this.FindItemAction("包子", SteamedBun::class.java, 25))
                registerLocationAction(this.FindNothingAction("这里吃的很多,但是乡长盯得很紧啊...", 20))
                registerLocationAction(object : BaseLocation.LocationAction {
                    override fun onTrigger(pet: BasePet, location: Location?) {
                        if (pet.getPetSetting("HasPillow") == null) {
                            if (pet.getPoint() >= 100) {
                                DialogService.confirm("森近 霖之助", "最近新到货一个枕头哦!\r\n有效治疗各种颈椎病!\r\n只要100点一个!", "购买", "取消", object : DialogService.CallBack {
                                    override fun onReturn(retVal: Boolean) {
                                        if (retVal) {
                                            PetService.pet!!.changePoint(-100)
                                            ItemService.addItem(Pillow())
                                        }
                                    }
                                })
                            } else {
                                DialogService.alert("森近 霖之助", "最近新到货一个枕头哦!\r\n有效治疗各种颈椎病!\r\n只要100点一个!", "取消", null)
                            }
                        }
                    }

                    override fun getWeight(): Int = 10

                    override fun getDoneMessage(): String = "这里吃的很多,但是乡长盯得很紧啊..."
                })
            }
        }
        val wzh: Location = object : BaseLocation("雾之湖") {
            override fun init() {
                registerLocationAction(this.FindItemAction("鱼", Fish::class.java, 20))
                registerLocationAction(this.FindItemAction("湖水", LakeWater::class.java, 40))
                registerLocationAction(this.FindNothingAction("这里貌似什么都没有...", 30))
                registerLocationAction(object : BaseLocation.LocationAction {
                    override fun onTrigger(pet: BasePet, location: Location?) {
                        if (pet.getPetSetting("CanAroundLakeRunning") == null) {
                            pet.setAchievement(pet.getAchievement() + 1)
                            pet.setPetSetting("CanAroundLakeRunning", Flag())
                            DialogService.alert("探索", "这里风景不错呀\r\n以后可以来这里跑步!\r\n\r\n新的学习可选 : 绕湖跑步")
                            ViewService.sliderView!!.addStudy(AroundLakeRunning())
                        }
                    }

                    override fun getWeight(): Int = 10

                    override fun getDoneMessage(): String = "这里貌似什么都没有..."
                })
            }
        }
        val hmg: Location = object : BaseLocation("红魔馆") {
            override fun init() {
                registerLocationAction(this.FindItemAction("红茶", BlackTea::class.java, 30))
                registerLocationAction(this.FindItemAction("蛋糕", Cake::class.java, 30))
                registerLocationAction(this.FindNothingAction("16:闲杂人等不要擅自闯入!", 30))
                registerLocationAction(this.FindNothingAction("貌似迷路了...", 30))
                registerLocationAction(object : BaseLocation.LocationAction {
                    override fun onTrigger(pet: BasePet, location: Location?) {
                        if (pet.getPetSetting("CanBeatFlandre") == null) {
                            pet.setAchievement(pet.getAchievement() + 1)
                            pet.setPetSetting("CanBeatFlandre", Flag())
                            DialogService.alert("探索", "咦?这里有个通往地下室的暗门...\r\n\r\n新的弹幕对象可选 : 芙兰朵露")
                            ViewService.sliderView!!.addBarrage(BarrageSliderItemList.BarrageInfo(Flandre::class.java, "芙兰"))
                        }
                    }

                    override fun getWeight(): Int = 10

                    override fun getDoneMessage(): String = "貌似迷路了..."
                })
            }
        }
        val mfsl: Location = object : BaseLocation("魔法森林") {
            override fun init() {
                registerLocationAction(this.FindItemAction("蘑菇", Mushroom::class.java, 60))
                registerLocationAction(this.FindNothingAction("貌似迷路了...", 30))
                registerLocationAction(object : BaseLocation.LocationAction {
                    override fun onTrigger(pet: BasePet, location: Location?) {
                        if (pet.getPetSetting("CanPartnerTraining") == null) {
                            pet.setAchievement(pet.getAchievement() + 1)
                            pet.setPetSetting("CanPartnerTraining", Flag())
                            DialogService.alert("探索", "魔理沙正在找人玩弹幕,陪她玩玩说不定有蘑菇吃!\r\n\r\n新的工作可选 : 陪练")
                            ViewService.sliderView!!.addWork(PartnerTraining())
                        }
                    }

                    override fun getWeight(): Int = 10

                    override fun getDoneMessage(): String = "这里貌似什么都没有..."
                })
            }
        }
        val ymzd: Location = object : BaseLocation("夜盲之道") {
            override fun init() {
                registerLocationAction(this.FindNothingAction("这里貌似什么都没有...", 30))
            }
        }
        val yytzl: Location = object : BaseLocation("竹林") {
            override fun init() {
                registerLocationAction(this.FindItemAction("竹笋", BambooShoot::class.java, 30))
                registerLocationAction(this.FindNothingAction("貌似迷路了...", 60))
                registerLocationAction(object : BaseLocation.LocationAction {
                    override fun onTrigger(pet: BasePet, location: Location?) {
                        if (pet.getPetSetting("CanBeatMokou") == null) {
                            pet.setAchievement(pet.getAchievement() + 1)
                            pet.setPetSetting("CanBeatMokou", Flag())
                            DialogService.alert("探索", "发现妹红一只!\r\n\r\n新的弹幕对象可选 : 藤原 妹红")
                            ViewService.sliderView!!.addBarrage(BarrageSliderItemList.BarrageInfo(Mokou::class.java, "妹红"))
                        }
                    }

                    override fun getWeight(): Int = 10

                    override fun getDoneMessage(): String = "貌似迷路了..."
                })
            }
        }
        val yyt: Location = object : BaseLocation("永远亭") {
            override fun init() {
                registerLocationAction(this.FindNothingAction("NEET : \"兔子你怎么又ADD了一个机器人!蛋红去接怪.拉远点!\r\n远程不要A了!点了这只机器人!\"", 5))
                registerLocationAction(this.FindNothingAction("NEET : \"黑水!黑水!驱散!驱散!兔子你又踩黑水了!\"", 5))
                registerLocationAction(this.FindNothingAction("NEET : \"点燃!点燃!兔子你的点燃!\"", 5))
                registerLocationAction(this.FindNothingAction("NEET : \"宝珠!宝珠!打宝珠啊兔子!\"", 5))
                registerLocationAction(this.FindNothingAction("NEET : \"莫慌,我已支配BKB在手,一会开雾打了roshan,中推\"", 5))
                registerLocationAction(this.FindNothingAction("NEET : \"我们野区高地那里有眼,一会帝你去排了\"", 5))
                registerLocationAction(this.FindNothingAction("NEET : \"兔子你怎么又被秒了!能出个绿杖不!\"", 5))
                registerLocationAction(this.FindNothingAction("NEET : \"兔子你怎么又被抓了!40分钟就一双鞋是什么情况!\"", 5))
                registerLocationAction(this.FindNothingAction("NEET : \"哦也!终于FC彩虹小马了!\"", 5))
                registerLocationAction(this.FindNothingAction("这里貌似什么都没有...", 20))
                registerLocationAction(object : BaseLocation.LocationAction {
                    override fun onTrigger(pet: BasePet, location: Location?) {
                        if (pet.getPetSetting("CanTestMedicine") == null) {
                            pet.setAchievement(pet.getAchievement() + 1)
                            pet.setPetSetting("CanTestMedicine", Flag())
                            DialogService.alert("探索", "师匠做的新药都可以当饭吃了...\r\n饿得不行来吃点药吧...\r\n\r\n新的工作可选 : 试药")
                            ViewService.sliderView!!.addWork(TestMedicine())
                        }
                    }

                    override fun getWeight(): Int = 10

                    override fun getDoneMessage(): String = "这里貌似什么都没有..."
                })
            }
        }
        val ygzs: Location = object : BaseLocation("妖怪之山") {
            override fun init() {
                registerLocationAction(this.FindNothingAction("守矢神社什么都没有,暂时还不能让你上去哦", 30))
            }
        }
        val ssss: Location = object : BaseLocation("守矢神社") {
            override fun init() {
                registerLocationAction(this.FindNothingAction("这里貌似什么都没有...", 30))
            }
        }
        registerLocation(HOME!!)
        registerLocation(jxq)
        registerLocation(ymjj)
        registerLocation(byl)
        registerLocation(rjzl)
        registerLocation(blss)
        registerLocation(xlt)
        registerLocation(wzh)
        registerLocation(hmg)
        registerLocation(mfsl)
        registerLocation(ymzd)
        registerLocation(yytzl)
        registerLocation(yyt)
        registerLocation(ygzs)
        registerLocation(ssss)
        (HOME!!.getNearbyList() as MutableList<Location>).add(jxq)
        (jxq.getNearbyList() as MutableList<Location>).add(HOME!!)
        (jxq.getNearbyList() as MutableList<Location>).add(ymjj)
        (jxq.getNearbyList() as MutableList<Location>).add(rjzl)
        (ymjj.getNearbyList() as MutableList<Location>).add(byl)
        (ymjj.getNearbyList() as MutableList<Location>).add(jxq)
        (byl.getNearbyList() as MutableList<Location>).add(ymjj)
        (rjzl.getNearbyList() as MutableList<Location>).add(jxq)
        (rjzl.getNearbyList() as MutableList<Location>).add(blss)
        (rjzl.getNearbyList() as MutableList<Location>).add(xlt)
        (rjzl.getNearbyList() as MutableList<Location>).add(mfsl)
        (blss.getNearbyList() as MutableList<Location>).add(rjzl)
        (xlt.getNearbyList() as MutableList<Location>).add(rjzl)
        (xlt.getNearbyList() as MutableList<Location>).add(ymzd)
        (wzh.getNearbyList() as MutableList<Location>).add(rjzl)
        (wzh.getNearbyList() as MutableList<Location>).add(hmg)
        (wzh.getNearbyList() as MutableList<Location>).add(ygzs)
        (hmg.getNearbyList() as MutableList<Location>).add(wzh)
        (mfsl.getNearbyList() as MutableList<Location>).add(rjzl)
        (ymzd.getNearbyList() as MutableList<Location>).add(xlt)
        (ymzd.getNearbyList() as MutableList<Location>).add(yytzl)
        (yytzl.getNearbyList() as MutableList<Location>).add(ymzd)
        (yytzl.getNearbyList() as MutableList<Location>).add(yyt)
        (yyt.getNearbyList() as MutableList<Location>).add(yytzl)
        (ygzs.getNearbyList() as MutableList<Location>).add(wzh)
        (ssss.getNearbyList() as MutableList<Location>).add(ygzs)
        for (location in locationList!!) {
            location.init()
        }
    }

    @JvmStatic
    fun registerLocation(location: Location) {
        locationMap!![location.getName()] = location
        locationList!!.add(location)
    }

    @JvmStatic
    fun findLocationByName(locationName: String): Location? {
        return locationMap!![locationName]
    }

    @JvmStatic
    fun release() {
        locationMap = null
        locationList = null
        HOME = null
    }
}