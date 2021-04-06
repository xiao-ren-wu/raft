package org.ywb.raft.core.utils;

import java.util.function.Supplier;

/**
 * @author yuwenbo1
 * @date 2021/4/5 6:23 下午 星期一
 * @since 1.0.0
 */
public abstract class Assert {

    public static <E extends RuntimeException> void isTrue(Boolean expression, Supplier<E> e) {
        if (!expression) {
            throw e.get();
        }
    }

    public static <E extends RuntimeException> void isFalse(Boolean expression, Supplier<E> e) {
        if (expression) {
            throw e.get();
        }
    }
}
