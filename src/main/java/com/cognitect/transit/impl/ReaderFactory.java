// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;
import com.cognitect.transit.SPI.ReaderSPI;
import com.fasterxml.jackson.core.JsonFactory;
import org.msgpack.MessagePack;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ReaderFactory {

    private static Map<Map<String, ReadHandler<?,?>>, Map<String, ReadHandler<?,?>>> handlerCache = new HashMap<Map<String, ReadHandler<?,?>>, Map<String, ReadHandler<?,?>>>();

    public static Map<String, ReadHandler<?,?>> defaultHandlers() {

        Map<String, ReadHandler<?,?>> handlers = new HashMap<String, ReadHandler<?,?>>();

        handlers.put(":", new ReadHandlers.KeywordReadHandler());
        handlers.put("$", new ReadHandlers.SymbolReadHandler());
        handlers.put("i", new ReadHandlers.IntegerReadHandler());
        handlers.put("?", new ReadHandlers.BooleanReadHandler());
        handlers.put("_", new ReadHandlers.NullReadHandler());
        handlers.put("f", new ReadHandlers.BigDecimalReadHandler());
        handlers.put("n", new ReadHandlers.BigIntegerReadHandler());
        handlers.put("d", new ReadHandlers.DoubleReadHandler());
        handlers.put("z", new ReadHandlers.SpecialNumberReadHandler());
        handlers.put("c", new ReadHandlers.CharacterReadHandler());
        handlers.put("t", new ReadHandlers.VerboseTimeReadHandler());
        handlers.put("m", new ReadHandlers.TimeReadHandler());
        handlers.put("r", new ReadHandlers.URIReadHandler());
        handlers.put("u", new ReadHandlers.UUIDReadHandler());
        handlers.put("b", new ReadHandlers.BinaryReadHandler());
        handlers.put("\'", new ReadHandlers.IdentityReadHandler());
        handlers.put("set", new ReadHandlers.SetReadHandler());
        handlers.put("list", new ReadHandlers.ListReadHandler());
        handlers.put("ratio", new ReadHandlers.RatioReadHandler());
        handlers.put("cmap", new ReadHandlers.CmapReadHandler());
        handlers.put("link", new ReadHandlers.LinkReadHandler());
        return handlers;
    }

    public static DefaultReadHandler<TaggedValue<Object>> defaultDefaultHandler() {
        return new DefaultReadHandler<TaggedValue<Object>>() {
            @Override
            public TaggedValue<Object> fromRep(String tag, Object rep) {
                return TransitFactory.taggedValue(tag, rep);
            }
        };
    }

    private static void disallowOverridingGroundTypes(Map<String, ReadHandler<?,?>> handlers) {
        if (handlers != null) {
            String groundTypeTags[] = {"_", "s", "?", "i", "d", "b", "'", "map", "array"};
            for (String tag : groundTypeTags) {
                if (handlers.containsKey(tag)) {
                    throw new IllegalArgumentException("Cannot override decoding for transit ground type, tag " + tag);
                }
            }
        }
    }

    private static Map<String, ReadHandler<?,?>> handlers(Map<String, ReadHandler<?,?>> customHandlers) {
        if (handlerCache.containsKey(customHandlers)) {
            return handlerCache.get(customHandlers);
        }

        synchronized (ReaderFactory.class) {
            if (handlerCache.containsKey(customHandlers)) {
                return handlerCache.get(customHandlers);
            } else {
                disallowOverridingGroundTypes(customHandlers);
                Map<String, ReadHandler<?,?>> handlers = defaultHandlers();
                if(customHandlers != null) {
                    Iterator<Map.Entry<String, ReadHandler<?,?>>> i = customHandlers.entrySet().iterator();
                    while(i.hasNext()) {
                        Map.Entry<String, ReadHandler<?,?>> e = i.next();
                        handlers.put(e.getKey(), e.getValue());
                    }
                }
                handlerCache.put(customHandlers, handlers);
                return handlers;
            }
        }
    }

    private static DefaultReadHandler defaultHandler(DefaultReadHandler customDefaultHandler) {
        return customDefaultHandler != null ? customDefaultHandler : defaultDefaultHandler();
    }

    public static Reader getJsonInstance(InputStream in,
                                     Map<String, ReadHandler<?,?>> customHandlers,
                                     DefaultReadHandler<?> customDefaultHandler) {
        return new JsonReaderImpl(in, handlers(customHandlers), defaultHandler(customDefaultHandler));
    }

    public static Reader getMsgpackInstance(InputStream in,
                                            Map<String, ReadHandler<?,?>> customHandlers,
                                            DefaultReadHandler<?> customDefaultHandler) {
        return new MsgPackReaderImpl(in, handlers(customHandlers), defaultHandler(customDefaultHandler));
    }

    private abstract static class ReaderImpl implements Reader, ReaderSPI {

        InputStream in;
        Map<String, ReadHandler<?,?>> handlers;
        DefaultReadHandler defaultHandler;
        MapReader<?, Map<Object, Object>, Object, Object> mapBuilder;
        ArrayReader<?, List<Object>, Object> listBuilder;
        ReadCache cache;
        AbstractParser p;
        boolean initialized;

        public ReaderImpl(InputStream in, Map<String, ReadHandler<?,?>> handlers, DefaultReadHandler<?> defaultHandler) {
            this.initialized = false;
            this.in = in;
            this.handlers = handlers;
            this.defaultHandler = defaultHandler;
            this.cache = new ReadCache();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T read() {
            if (!initialized) initialize();
            try {
                return (T) p.parse(cache.init());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Reader setBuilders(MapReader<?, Map<Object, Object>, Object, Object> mapBuilder,
                                  ArrayReader<?, List<Object>, Object> listBuilder) {
            if (initialized) throw new IllegalStateException("Cannot set builders after read has been called");
            this.mapBuilder = mapBuilder;
            this.listBuilder = listBuilder;
            return this;
        }

        private void ensureBuilders() {
            if (mapBuilder == null) mapBuilder = new MapBuilderImpl();
            if (listBuilder == null) listBuilder = new ListBuilderImpl();
        }

        protected void initialize() {
            ensureBuilders();
            p = createParser();
            initialized = true;
        }

        protected abstract AbstractParser createParser();
    }

    private static class JsonReaderImpl extends ReaderImpl {

        public JsonReaderImpl(InputStream in, Map<String, ReadHandler<?,?>> handlers, DefaultReadHandler<?> defaultHandler) {
            super(in, handlers, defaultHandler);
        }

        @Override
        protected AbstractParser createParser() {
            try {
                JsonFactory jf = new JsonFactory();
                return new JsonParser(jf.createParser(in), handlers, defaultHandler,
                        mapBuilder, listBuilder);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class MsgPackReaderImpl extends ReaderImpl {

        public MsgPackReaderImpl(InputStream in, Map<String, ReadHandler<?,?>> handlers, DefaultReadHandler<?> defaultHandler) {
            super(in, handlers, defaultHandler);
        }

        @Override
        protected AbstractParser createParser() {
            MessagePack mp = new MessagePack();
            return new MsgpackParser(mp.createUnpacker(in), handlers, defaultHandler,
                    mapBuilder, listBuilder);
        }
    }
}
