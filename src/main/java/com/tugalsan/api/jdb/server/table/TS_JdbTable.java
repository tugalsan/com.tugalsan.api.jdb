package com.tugalsan.api.jdb.server.table;

import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.jdb.server.simple.TS_JdbSimple;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TS_JdbTable {

    final private static TS_Log d = TS_Log.of(false, TS_JdbTable.class);

    private TS_JdbTable(Path path, TS_JdbTableConfig colConfig) {
        this.simple = TS_JdbSimple.of(path);
        this.colConfig = colConfig;
    }
    final private TS_JdbSimple simple;
    final public TS_JdbTableConfig colConfig;

    public static TS_JdbTable of(Path path, TS_JdbTableConfig colConfig) {
        return new TS_JdbTable(path, colConfig);
    }

    public static TS_JdbTable of(Path path, TS_JdbTableColBase... types) {
        return new TS_JdbTable(path, TS_JdbTableConfig.of(types));
    }

    public Path path() {
        return simple.path;
    }

    public long rowSize() {
        if (!TS_FileUtils.isExistFile(path())) {
            return 0L;
        }
        var sizeInBytes_db = TS_FileUtils.getFileSizeInBytes(path());
        var sizeInBytes_row = colConfig.byteSize();
        return sizeInBytes_db / sizeInBytes_row;
    }

    private long position(long idx) {
        var pos = idx * colConfig.byteSize();
        d.ci("position", pos);
        return pos;
    }

    public List<TS_JdbTableColBase> rowGet(long idx) {
        List<TS_JdbTableColBase> lst = new ArrayList();
        var position = position(idx);
        for (var i = 0; i < colConfig.types.size(); i++) {
            var type = colConfig.types.get(i);
            if (type instanceof TS_JdbTableColDbl) {
                var val = simple.getDoubleFromPostion(position).orThrowFirstInfo();
                var value = TS_JdbTableColDbl.of(val);
                lst.add(value);
                position += value.byteSize();
                d.ci("rowGet", "i", i, "pos", position);
            } else if (type instanceof TS_JdbTableColLng) {
                var val = simple.getLongFromPostion(position).orThrowFirstInfo();
                var value = TS_JdbTableColLng.of(val);
                lst.add(value);
                position += value.byteSize();
                d.ci("rowGet", "i", i, "pos", position);
            } else if (type instanceof TS_JdbTableColStr typeStr) {
                var valOp = simple.getStringFromPostion(position);
                d.ci("valOp", valOp);
                var val = valOp.orThrowFirstInfo();
                var valueStr = TS_JdbTableColStr.of(typeStr.byteSize());
                valueStr.set(val);
                var value = valueStr;
                lst.add(value);
                position += value.byteSize();
                d.ci("rowGet", "i", i, "pos", position);
            } else if (type instanceof TS_JdbTableColStr) {
                throw new RuntimeException("ERROR @ TS_JdbList.rowGet: unkwon col type");
            }
        }
        return lst;
    }

    public Exception rowSet(long idx, TS_JdbTableColBase... rowValues) {
        return rowSet(idx, List.of(rowValues));
    }

    public Exception rowSet(long idx, List<? extends TS_JdbTableColBase> newRow) {
        return TGS_UnSafe.call(() -> {
            var position = position(idx);
            for (var i = 0; i < colConfig.types.size(); i++) {
                var type = colConfig.types.get(i);
                Object value = newRow.get(i);
                if (type instanceof TS_JdbTableColDbl) {
                    position = simple.setDoubleFromPostion_calcNextPosition(position, ((TS_JdbTableColDbl) value).value).orThrowFirstInfo();
                    d.ci("rowSet", "i", i, "pos", position);
                } else if (type instanceof TS_JdbTableColLng) {
                    position = simple.setLongFromPostion_calcNextPosition(position, ((TS_JdbTableColLng) value).value).orThrowFirstInfo();
                    d.ci("rowSet", "i", i, "pos", position);
                } else if (type instanceof TS_JdbTableColStr typeStr) {
                    simple.setStringFromPostion_calcNextPosition(position, ((TS_JdbTableColStr) value).get()).orThrowFirstInfo();
                    position += typeStr.byteSize();
                    d.ci("rowSet", "i", i, "pos", position);
                } else {
                    throw new RuntimeException("ERROR @ TS_JdbList.rowGet: unkwon col type");
                }
            }
            return null;
        }, e -> e);
    }
}
