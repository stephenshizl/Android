
package com.aispeech;

import com.lenovo.freeanswer.wakeup.util.AppLog;

public final class AIEngine {

    static {
        try {
            System.loadLibrary("lenovo-freeanswer-aiengine");
        } catch (Exception e) {
            AppLog.ex(e);
            e.printStackTrace();
        }
    }

    public interface aiengine_callback {
        public abstract int run(byte[] id, int type, byte[] data, int size);
    }

    public static int AIENGINE_MESSAGE_TYPE_JSON = 1;
    public static int AIENGINE_MESSAGE_TYPE_BIN = 2;

    public static int AIENGINE_OPT_GET_VERSION = 1;
    public static int AIENGINE_OPT_GET_MODULES = 2;
    public static int AIENGINE_OPT_GET_TRAFFIC = 3;

    public static native long aiengine_new(String cfg, Object androidContext);

    public static native int aiengine_delete(long engine);

    public static native int aiengine_start(long engine, String param, byte[] id,
            aiengine_callback callback);

    public static native int aiengine_feed(long engine, byte[] data, int size);

    public static native int aiengine_stop(long engine);

    public static native int aiengine_cancel(long engine);

    public static native int aiengine_log(long engine, String log);

    public static native int aiengine_opt(long engine, int opt, byte[] data, int size);

    public static native int aiengine_get_device_id(byte[] device_id, Object androidContext);
}
