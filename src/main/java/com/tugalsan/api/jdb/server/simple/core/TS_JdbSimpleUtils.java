package com.tugalsan.api.jdb.server.simple.core;

import com.tugalsan.api.optional.client.TGS_Optional;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class TS_JdbSimpleUtils {

    public static RandomAccessFile create(File file) throws FileNotFoundException {
        return new RandomAccessFile(file, "rw");
    }

    public static TGS_Optional<String> getStringFromPostion(RandomAccessFile raf, int position) {
        return TGS_UnSafe.call(() -> {
            raf.seek(position);
            return TGS_Optional.of(raf.readUTF());
        }, e -> TGS_Optional.ofEmpty(e.getMessage()));
    }

    public static TGS_Optional<Boolean> getBooleanFromPostion(RandomAccessFile raf, int position) {
        return TGS_UnSafe.call(() -> {
            raf.seek(position);
            return TGS_Optional.of(raf.readBoolean());
        }, e -> TGS_Optional.ofEmpty(e.getMessage()));
    }

    public static TGS_Optional<Double> getDoubleFromPostion(RandomAccessFile raf, int position) {
        return TGS_UnSafe.call(() -> {
            raf.seek(position);
            return TGS_Optional.of(raf.readDouble());
        }, e -> TGS_Optional.ofEmpty(e.getMessage()));
    }

    public static TGS_Optional<Long> getLongFromPostion(RandomAccessFile raf, int position) {
        return TGS_UnSafe.call(() -> {
            raf.seek(position);
            return TGS_Optional.of(raf.readLong());
        }, e -> TGS_Optional.ofEmpty(e.getMessage()));
    }
}
