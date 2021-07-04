package org.ywb.codec.support;

import java.util.Locale;

/**
 * @author yuwenbo1
 * @date 2021/7/3 4:12 下午 星期六
 * @since 1.0.0
 */
public class MagicCodeErrorException extends RuntimeException {
    public MagicCodeErrorException(int magic) {
        super("magic is valid " + Integer.toHexString(magic).toUpperCase(Locale.ROOT));
    }
}
