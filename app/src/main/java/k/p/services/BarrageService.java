package k.p.services;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import k.p.action.barrage.BaseEnemy;
import k.p.action.barrage.BaseShoujo;
import k.p.action.barrage.Shoujo;
import k.p.domain.BasePet;
import k.p.main.MainService;
import k.p.main.R;

/* JADX INFO: loaded from: classes.dex */
public class BarrageService {
    public static View barrageView;
    private static TextView battleStatus;
    private static TextView enemyHP;
    private static TextView enemyName;
    private static Handler handler = new Handler() { // from class: k.p.services.BarrageService.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            BarrageService.enemyHP.setText(String.valueOf(BarrageService.target.getCurrentHP()) + "/" + BarrageService.target.getMaxHP());
            BarrageService.playerHP.setText(String.valueOf(BarrageService.player.getCurrentHP()) + "/" + BarrageService.player.getMaxHP());
            BarrageService.battleStatus.setText(BarrageService.getStringFromList(BarrageService.statusList));
            MainService.context.updateBarrageView();
        }
    };
    public static BaseShoujo player;
    private static TextView playerHP;
    private static TextView playerName;
    private static List<String> statusList;
    public static BaseEnemy target;

    /* JADX WARN: Type inference failed for: r1v35, types: [k.p.services.BarrageService$2] */
    public static void newBarrage(BasePet pet, BaseEnemy enemy) {
        if (statusList != null) {
            statusList.clear();
        } else {
            statusList = new ArrayList();
        }
        barrageView = View.inflate(MainService.context, R.layout.barrage, null);
        enemyName = (TextView) barrageView.findViewById(R.id.barrage_enemyname);
        enemyHP = (TextView) barrageView.findViewById(R.id.barrage_enemyhp);
        battleStatus = (TextView) barrageView.findViewById(R.id.barrage_battlestatus);
        playerName = (TextView) barrageView.findViewById(R.id.barrage_playername);
        playerHP = (TextView) barrageView.findViewById(R.id.barrage_playerhp);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = 2003;
        params.flags = 520;
        params.gravity = 17;
        params.width = 600;
        params.height = -2;
        params.format = 1;
        barrageView.setLayoutParams(params);
        player = new BaseShoujo(pet.getStrength(), pet.getSpeed(), pet.getMagic());
        player.setName(pet.getName());
        target = enemy;
        player.init();
        target.init();
        enemyName.setText(target.getName());
        playerName.setText(player.getName());
        enemyHP.setText(String.valueOf(target.getCurrentHP()) + "/" + target.getMaxHP());
        playerHP.setText(String.valueOf(player.getCurrentHP()) + "/" + player.getMaxHP());
        MainService.context.requestNewBarrageView();
        new Thread() { // from class: k.p.services.BarrageService.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                for (int roundCount = 1; roundCount < 100; roundCount++) {
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                    }
                    BarrageService.putString("回合" + roundCount + "-------");
                    if (BarrageService.player.getSpeed() > BarrageService.target.getSpeed()) {
                        if (!BarrageService.roundDamage(BarrageService.player, BarrageService.target, roundCount) || !BarrageService.roundDamage(BarrageService.target, BarrageService.player, roundCount)) {
                            break;
                        }
                        BarrageService.handler.sendEmptyMessage(0);
                    } else {
                        if (!BarrageService.roundDamage(BarrageService.target, BarrageService.player, roundCount) || !BarrageService.roundDamage(BarrageService.player, BarrageService.target, roundCount)) {
                            break;
                        }
                        BarrageService.handler.sendEmptyMessage(0);
                    }
                }
                if (BarrageService.player.getCurrentHP() <= 0) {
                    BarrageService.putString(String.valueOf(BarrageService.target.getName()) + " 获得了胜利!");
                } else {
                    BarrageService.putString(String.valueOf(BarrageService.player.getName()) + " 获得了胜利!");
                }
                BarrageService.handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(4000L);
                } catch (InterruptedException e2) {
                }
                MainService.context.removeBarrageView();
                if (BarrageService.player.getCurrentHP() <= 0) {
                    BarrageService.target.onLose(PetService.pet);
                } else {
                    BarrageService.target.onWin(PetService.pet);
                }
            }
        }.start();
    }

    public static void putString(String status) {
        if (status != null && statusList != null) {
            statusList.add(status);
            if (statusList.size() > 15) {
                statusList.remove(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getStringFromList(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str);
            sb.append("\r\n");
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean roundDamage(Shoujo src, Shoujo target2, int roundCount) {
        int damage;
        Random random = new Random();
        src.onRoundStart(target2, roundCount);
        if (src.getSpeed() > 0) {
            damage = src.getMagic() + random.nextInt(src.getSpeed());
        } else if (src.getSpeed() < 0) {
            damage = src.getMagic() - random.nextInt(-src.getSpeed());
        } else {
            damage = src.getMagic();
        }
        if (damage > 0) {
            target2.setCurrentHP(target2.getCurrentHP() - damage);
            putString(String.valueOf(src.getName()) + " 对 " + target2.getName() + " 造成了 " + damage + " 点伤害!");
            src.onCauseDamage(target2, damage);
            target2.onDamaged(src, damage);
            if (target2.getCurrentHP() <= 0) {
                target2.setCurrentHP(0);
                return false;
            }
        }
        src.onRoundEnd(target2, roundCount);
        if (target2.getCurrentHP() > 0) {
            return true;
        }
        target2.setCurrentHP(0);
        return false;
    }

    public static void release() {
        barrageView = null;
        player = null;
        target = null;
        enemyName = null;
        enemyHP = null;
        battleStatus = null;
        playerName = null;
        playerHP = null;
        statusList = null;
    }
}
