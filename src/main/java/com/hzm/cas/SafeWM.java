package com.hzm.cas;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 把某个类封装成原子类,然后使用cas操作进行无锁更新
 */
public class SafeWM {
    class WMRange {
        final int upper;
        final int lower;

        WMRange(int upper, int lower) {
            // 省略构造函数实现
            this.lower = lower;
            this.upper = upper;
        }
    }

    final AtomicReference<WMRange> rf = new AtomicReference<>(new WMRange(0, 0));

    // 设置库存上限
    void setUpper(int v) {
        WMRange nr;
        WMRange or;
        do {
            or = rf.get();
            // 检查参数合法性
            if (v < or.lower) {
                throw new IllegalArgumentException();
            }
            nr = new WMRange(v, or.lower);
        } while (!rf.compareAndSet(or, nr));
    }
}
