package com.tugalsan.api.file.ra.server.simple;

import com.tugalsan.api.file.ra.server.simple.core.TS_FileRaSimpleUtils;
import com.tugalsan.api.callable.client.TGS_CallableType1;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TS_FileRaSimple {

    private TS_FileRaSimple(Path path) {
        this.path = path;
        this.file = path.toFile();
    }
    final public Path path;
    final private File file;

    public static TS_FileRaSimple of(Path path) {
        return new TS_FileRaSimple(path);
    }

    private <T> TGS_UnionExcuse<T> call(TGS_CallableType1<T, RandomAccessFile> jdb) {
        lock.lock();
        try (var raf = TS_FileRaSimpleUtils.create(file)) {
            return TGS_UnionExcuse.of(jdb.call(raf));
        } catch (IOException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        } finally {
            lock.unlock();
        }
    }
    final private Lock lock = new ReentrantLock();

    public TGS_UnionExcuse<Double> getDoubleFromPostion(long position) {
        var u = call(jdb -> TS_FileRaSimpleUtils.getDoubleFromPostion(jdb, position));
        if (u.isExcuse()) {
            return u.toExcuse();
        }
        return u.value();
    }

    public TGS_UnionExcuse<Long> setDoubleFromPostion_calcNextPosition(long position, double value) {
        var u = call(raf -> TS_FileRaSimpleUtils.setDoubleFromPostion_calcNextPosition(raf, position, value));
        if (u.isExcuse()) {
            return u.toExcuse();
        }
        return u.value();
    }

    public TGS_UnionExcuse<Long> getLongFromPostion(long position) {
        var u = call(jdb -> TS_FileRaSimpleUtils.getLongFromPostion(jdb, position));
        if (u.isExcuse()) {
            return u.toExcuse();
        }
        return u.value();
    }

    public TGS_UnionExcuse<Long> setLongFromPostion_calcNextPosition(long position, long value) {
        var u = call(raf -> TS_FileRaSimpleUtils.setLongFromPostion_calcNextPosition(raf, position, value));
        if (u.isExcuse()) {
            return u.toExcuse();
        }
        return u.value();
    }

    public TGS_UnionExcuse<String> getStringFromPostion(long position) {
        var u = call(jdb -> TS_FileRaSimpleUtils.getStringFromPostion(jdb, position));
        if (u.isExcuse()) {
            return u.toExcuse();
        }
        return u.value();
    }

    @Deprecated //WARNING: CHECK BYTE SIZE
    public TGS_UnionExcuse<Long> setStringFromPostion_calcNextPosition(long position, String value) {
        var u = call(raf -> TS_FileRaSimpleUtils.setStringFromPostion_calcNextPosition(raf, position, value));
        if (u.isExcuse()) {
            return u.toExcuse();
        }
        return u.value();
    }
}
