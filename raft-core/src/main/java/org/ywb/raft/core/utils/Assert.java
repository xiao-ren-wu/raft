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

    public static void isTrue(Boolean expression, String err) {
        if (!expression) {
            throw new IllegalArgumentException(err);
        }
    }

    public static void nonNull(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
    }

    public static void nonNull(Object obj, String errMsg) {
        if (obj == null) {
            throw new IllegalArgumentException(errMsg);
        }
    }

    public static void hasText(String text, String errMsg) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(errMsg);
        }
    }
}
