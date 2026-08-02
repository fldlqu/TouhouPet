package k.p.main

import android.os.SystemClock
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import k.p.modern.AppScopes

/**
 * 架构现代化:游戏主循环(替代 MainService 的裸 Thread + Thread.sleep)。
 *
 * 语义与原版逐句等价:
 * - 每 tick 计算实际经过时间,回调 onTick(elapsed)
 * - 若 tick 耗时小于 interval,delay 补齐(原版 Thread.sleep)
 * - stop() 协程取消,立即退出(原版 join(2000) 超时语义更干净)
 */
class GameLoop(
    private val intervalMs: Int,
    private val onTick: (Int) -> Unit
) {
    private val scope = AppScopes.newDefault()
    private var lastTime = 0L

    fun start() {
        scope.launch {
            lastTime = SystemClock.elapsedRealtime()
            while (isActive) {
                val now = SystemClock.elapsedRealtime()
                val time = (now - lastTime).toInt()
                lastTime = now
                onTick(time)
                val sleepTime = intervalMs - time
                if (sleepTime > 0) {
                    delay(sleepTime.toLong())
                }
            }
        }
    }

    fun stop() {
        scope.cancel()
    }
}