package com.cognitect.transit.impl;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Writer;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class MsgpackEmitter extends AbstractEmitter {

    private final Packer gen;

    public MsgpackEmitter(Packer gen, Map<Class, Handler> handlers) {
        super(handlers);
        this.gen = gen;
    }

    @Override
    public void emit(Object o, boolean asMapKey, WriteCache cache) throws Exception {
        marshal(o, asMapKey, cache);
    }

    @Override
    public void emitNil(boolean asMapKey, WriteCache cache) throws Exception {
        this.gen.writeNil();
    }

    @Override
    public void emitString(String prefix, String tag, String s, boolean asMapKey, WriteCache cache) throws Exception {

        StringBuilder sb = new StringBuilder();
        if(prefix != null)
            sb.append(prefix);
        if(tag != null)
            sb.append(tag);
        if(s != null)
            sb.append(s);

        String outString = cache.cacheWrite(sb.toString(), asMapKey);

        if(asMapKey)
            this.gen.write(outString);
        else
            this.gen.write(outString);
    }

    @Override
    public void emitBoolean(Boolean b, boolean asMapKey, WriteCache cache) throws Exception {
        this.gen.write(b);
    }

    @Override
    public void emitInteger(Object o, boolean asMapKey, WriteCache cache) throws Exception {
        // TODO: BigInteger?

        if (o instanceof String) this.emitString(Writer.ESC, "i", o.toString(), asMapKey, cache);

        long i = Util.numberToPrimitiveLong(o);

        if ((i > Long.MAX_VALUE) || (i < Long.MIN_VALUE))
            this.emitString(Writer.ESC, "i", o.toString(), asMapKey, cache);

        this.gen.write(i);
    }

    @Override
    public void emitDouble(Object d, boolean asMapKey, WriteCache cache) throws Exception {
    }

    @Override
    public void emitBinary(Object b, boolean asMapKey, WriteCache cache) throws Exception {
    }

    @Override
    public long arraySize(Object a) {

        if(a instanceof List)
            return ((List)a).size();
        else return 0;
    }

    @Override
    public void emitArrayStart(Long size) throws Exception {
    }

    @Override
    public void emitArrayEnd() throws Exception {
    }

    @Override
    public long mapSize(Object m) {
        if(m instanceof Map)
            return ((Map)m).size();
        else return 0;
    }

    @Override
    public void emitMapStart(Long ignored) throws Exception {
    }

    @Override
    public void emitMapEnd() throws Exception {
    }

    @Override
    public void flushWriter() throws IOException {
    }

    @Override
    public boolean prefersStrings() {
        return true;
    }
}
