# Review GPT-4o

## Topics

1. **Class and Interface Design**: and interned strings. application's tagging
   and escaping mechanisms. BigInteger. functionality. handlers. library. of
   data. properties. TagProvider.
   - **AbstractEmitter class**: Abstract base class for emitters.
   - **AbstractParser class**: An abstract class implementing common parsing
   - **AbstractWriteHandler class**: Abstract class providing a base for write
   - **ArrayReader interface**: Interface for array readers.
   - **ArrayReadHandler interface**: Interface for array read handlers.
   - **Cache class**: Extends LinkedHashMap to implement an LRU cache.
   - **Constants class**: Contains several constant values related to the
   - **DefaultReadHandler interface**: Interface for default read handlers.
   - **Emitter interface**: Defines several methods for emitting different types
   - **JsonEmitter class**: Implements JSON emitting functionality.
   - **JsonParser class**: Extends AbstractParser for JSON parsing.
   - **JsonVerboseEmitter class**: Extends JsonEmitter for verbose JSON output.
   - **Keyword interface**: Represents a keyword.
   - **KeywordImpl class**: Implementation of the Keyword interface with caching
   - **Link interface**: Represents a hypermedia link.
   - **LinkImpl class**: Implementation of the Link interface with various
   - **ListBuilderImpl class**: A builder for lists implementing ArrayReader.
   - **MapBuilderImpl class**: A builder for maps implementing MapReader.
   - **MapReader interface**: Interface for map readers.
   - **MapReadHandler interface**: Interface for map read handlers.
   - **MsgpackEmitter class**: Implements Msgpack emitting functionality.
   - **MsgpackParser class**: Extends AbstractParser for Msgpack parsing.
   - **Named interface**: Represents a namespace-scoped named object.
   - **Parser interface**: Defines methods for parsing.
   - **Quote class**: Simple immutable class with a single field.
   - **Ratio interface**: Represents a ratio.
   - **RatioImpl class**: Implementation of the Ratio interface using
   - **ReadCache class**: Implements a cache for read operations.
   - **Reader interface**: Interface for reading values in transit format.
   - **ReaderFactory class**: Factory for creating readers.
   - **ReadHandler interface**: Interface for read handlers.
   - **ReadHandlerMap class**: Implements a map with read handlers.
   - **ReadHandlers class**: Contains various read handler implementations.
   - **SymbolImpl class**: Implementation of the Symbol interface.
   - **Tag class**: Simple class representing a tag with a value.
   - **TagFinder interface**: Defines a method for tag retrieval.
   - **TaggedValue interface**: Represents a tagged value.
   - **TaggedValueImpl class**: Implementation of the TaggedValue interface.
   - **TagProvider interface**: Interface for tag providers.
   - **TagProvider interface**: Interface for tag providers.
   - **TagProviderAware interface**: Interface for classes that need a
   - **TransitFactory class**: Main entry point for using the transit-java
   - **URI interface**: Represents a URI.
   - **URIImpl class**: Implementation of the URI interface.
   - **Util class**: Utility class with various helper methods.
   - **WriteCache class**: Implements a cache for write operations.
   - **WriteHandler interface**: Interface for write handlers.
   - **Writer interface**: Interface for writers.
   - **WriterFactory class**: Factory for creating writers.

2. **Coding Practices**:
   - Use of **final** keyword for immutability.
   - **ThreadLocal** usage for date formatting in `AbstractParser`.
   - **Generics** and **wildcards** in handler interfaces.
   - **Exception handling** strategies in parsing and writing classes.
   - **Deprecated annotation** usage in `JsonVerboseEmitter`.
   - Use of **synchronized blocks** in `WriterFactory` and `ReaderFactory`.

3. **Design Patterns**:
   - **Factory Pattern**: `ReaderFactory` and `WriterFactory`.
   - **Builder Pattern**: `ListBuilderImpl` and `MapBuilderImpl`.
   - **Strategy Pattern**: Different handlers for reading and writing.
   - **Cache Pattern**: `ReadCache` and `WriteCache`.

4. **API and Extensibility**:
   - How the system allows for custom handlers and extending functionality.
   - Use of interfaces to define contracts for handlers.

5. **Performance Considerations**:
   - LRU caching mechanism in `Cache`.
   - Use of `ThreadLocal` for date formatting.

6. **Serialization/Deserialization**:
   - Handling of different data formats (JSON, Msgpack).
   - Custom serialization and deserialization logic in handlers.

7. **Error Handling and Validation**:
   - Error messages and exception handling within parsers and emitters.
   - Validation of inputs in constructors (e.g., `LinkImpl`).

8. **Testing and Debugging**:
   - Potential areas to focus on in unit tests.
   - How to debug issues related to parsing and writing.

9. **Usage of External Libraries**:
   - Integration with Jackson for JSON processing.
   - Integration with MessagePack for Msgpack processing.

10. **Documentation and Comments**:

- Effectiveness of comments and documentation throughout the code.
- Use of `@Override` and `@SuppressWarnings` annotations.

### Analysis of Coupling in the Given Classes

**Coupling** refers to the degree of direct knowledge that one class has about
another class. High coupling can make the code less modular and harder to
maintain, while low coupling (or loose coupling) is generally preferred for
modularization and ease of maintenance. Here’s an analysis of the coupling in
the provided source code:

1. **High Coupling Areas**:
   - **AbstractParser and its subclasses**: These classes have strong
     dependencies on `ReadHandler` and `DefaultReadHandler`, as well as on the
     `MapReader` and `ArrayReader`. The `MsgpackParser` and `JsonParser` extend
     `AbstractParser`, creating tight coupling.
   - **Emitter and its subclasses**: `AbstractEmitter`, `JsonEmitter`, and
     `MsgpackEmitter` are tightly coupled with `WriteHandlerMap`, `WriteCache`,
     and `WriteHandler`.
   - **Factory classes**: `WriterFactory` and `ReaderFactory` heavily depend on
     various handler maps and configurations, leading to high coupling with many
     classes like `WriteHandlerMap`, `ReadHandlerMap`, and `WriteCache`.
   - **Implementation classes**: Classes like `KeywordImpl`, `LinkImpl`,
     `SymbolImpl`, and `URIImpl` are tightly coupled with their respective
     interfaces and sometimes with other implementation classes (e.g.,
     `LinkImpl` depends on `URIImpl`).

2. **Interfaces and Implementations**:
   - The use of interfaces (e.g., `TagFinder`, `WriteHandler`, `ReadHandler`,
     `ArrayReader`, `MapReader`) helps in achieving loose coupling. However, the
     concrete implementations (e.g., `JsonVerboseEmitter`, `MsgpackParser`,
     `KeywordImpl`) are tightly coupled with specific classes and interfaces.

3. **Constants and Utility Classes**:
   - The `Constants` class is used across various classes, leading to a central
     point of coupling. This can be beneficial for consistency but can also lead
     to tighter coupling if used extensively.

### Recommendations for Improving Package Modularization

To improve modularization, we can consider the following strategies:

1. **Separate Concerns into Distinct Packages**:
   - **Handlers**: Create a package specifically for handlers, both read and
     write. This includes classes like `ReadHandler`, `WriteHandler`,
     `DefaultReadHandler`, and their implementations.
     - `com.cognitect.transit.handlers`
   - **Parsers and Emitters**: Separate parsers and emitters into their packages
     to isolate the parsing and emitting logic.
     - `com.cognitect.transit.parsers`
     - `com.cognitect.transit.emitters`
   - **Core Models and Interfaces**: Place core interfaces and model classes
     (e.g., `Keyword`, `Symbol`, `URI`, `Link`) in a core package.
     - `com.cognitect.transit.core`
   - **Utilities and Constants**: Place utility classes and constants in a
     separate package.
     - `com.cognitect.transit.util`
   - **Factory Classes**: Place factory classes in a dedicated package, as they
     are responsible for object creation and configuration.
     - `com.cognitect.transit.factory`

2. **Use Dependency Injection**:
   - Implement Dependency Injection (DI) to manage dependencies, particularly in
     factory classes and parsers/emitters. This can help reduce direct coupling
     and improve testability.
   - Consider using a DI framework like Spring or a lighter alternative like
     Google Guice.

3. **Encapsulate Configuration**:
   - Use configuration classes or builders to encapsulate the configuration
     logic for handlers, readers, and writers. This can help reduce coupling by
     avoiding hard-coded dependencies in factory methods.

4. **Reduce Static Dependencies**:
   - Minimize the use of static methods and constants that create tight
     coupling. Instead, consider using instance methods and dependency injection
     to manage dependencies.

5. **Decouple Concrete Implementations**:
   - Ensure that concrete implementations do not directly depend on each other
     unless necessary. Use interfaces and abstractions to decouple them.
   - For example, avoid direct dependencies between `JsonEmitter` and
     `MsgpackEmitter` by using a common interface or abstract class.

### Proposed Package Structure

Here is a proposed package structure that could help in achieving better
modularization:

```
com.cognitect.transit
├── core
│   ├── Keyword.java
│   ├── Symbol.java
│   ├── URI.java
│   ├── Link.java
│   ├── TaggedValue.java
│   └── ... (other core interfaces and models)
├── handlers
│   ├── ReadHandler.java
│   ├── WriteHandler.java
│   ├── DefaultReadHandler.java
│   ├── ReadHandlerMap.java
│   ├── WriteHandlerMap.java
│   ├── WriteHandlers.java
│   ├── ReadHandlers.java
│   └── ... (other handler implementations)
├── parsers
│   ├── AbstractParser.java
│   ├── JsonParser.java
│   ├── MsgpackParser.java
│   ├── Parser.java
│   └── ... (other parser classes)
├── emitters
│   ├── AbstractEmitter.java
│   ├── JsonEmitter.java
│   ├── MsgpackEmitter.java
│   ├── Emitter.java
│   └── ... (other emitter classes)
├── factory
│   ├── WriterFactory.java
│   ├── ReaderFactory.java
│   └── ... (other factory classes)
├── util
│   ├── Constants.java
│   ├── Util.java
│   └── ... (other utility classes)
└── cache
    ├── ReadCache.java
    ├── WriteCache.java
    └── ... (other cache-related classes)
```

### Benefits of This Modularization

- **Improved Maintainability**: By organizing classes into logical packages, it
  becomes easier to locate and manage code.
- **Loose Coupling**: Reducing direct dependencies between classes and using
  interfaces where appropriate can help in achieving loose coupling.
- **Enhanced Testability**: With better modularization and dependency injection,
  unit testing becomes easier.
- **Scalability**: A modular structure allows for easier scaling and extension
  of functionality.

By following these recommendations, the codebase will become more modular,
maintainable, and easier to understand.

## Low-hanging fruits

Here are five low-hanging fruits for refactoring the provided codebase:

1. **Extract Constants to a Dedicated Package**:
   - **Effort**: Low
   - **Return**: High
   - **Action**: Move the `Constants` class to a dedicated package like
     `com.cognitect.transit.util`. This will help in centralizing the use of
     constant values and reduce the risk of duplication or inconsistency.
   - **Example Refactoring**:
     ```java
     package com.cognitect.transit.util;

     public final class Constants {
         public static final char ESC = '~';
         public static final String ESC_STR = String.valueOf(ESC);
         public static final char TAG = '#';
         public static final String TAG_STR = String.valueOf(TAG);
         public static final char SUB = '^';
         public static final String SUB_STR = String.valueOf(SUB);
         public static final char RESERVED = '`';
         public static final String ESC_TAG = String.valueOf(ESC) + TAG;
         public static final String QUOTE_TAG = ESC_TAG + "'";
         public static final String MAP_AS_ARRAY = "^ ";
     }
     ```
   - **Impact**: Simplifies the codebase by reducing the need to define
     constants in multiple places.

2. **Introduce Dependency Injection for Handlers**:
   - **Effort**: Low
   - **Return**: High
   - **Action**: Use dependency injection (DI) to inject `ReadHandler` and
     `WriteHandler` instances instead of hardcoding them. This can be done using
     a simple DI framework or even manually.
   - **Example Refactoring**:
     ```java
     public class SomeClass {
         private final ReadHandlerMap readHandlerMap;
         private final WriteHandlerMap writeHandlerMap;

         // Constructor injection
         public SomeClass(ReadHandlerMap readHandlerMap, WriteHandlerMap writeHandlerMap) {
             this.readHandlerMap = readHandlerMap;
             this.writeHandlerMap = writeHandlerMap;
         }
     }
     ```
   - **Impact**: Reduces coupling between classes and makes the code more
     flexible and easier to test.

3. **Encapsulate Handlers Initialization**:
   - **Effort**: Low
   - **Return**: High
   - **Action**: Move the initialization of `ReadHandlerMap` and
     `WriteHandlerMap` to their respective factory methods. This encapsulates
     the creation logic and makes the code more modular.
   - **Example Refactoring**:
     ```java
     public class ReaderFactory {
         private static final ReadHandlerMap defaultReadHandlerMap = new ReadHandlerMap(defaultHandlers());

         public static ReadHandlerMap getDefaultReadHandlerMap() {
             return defaultReadHandlerMap;
         }
     }

     public class WriterFactory {
         private static final WriteHandlerMap defaultWriteHandlerMap = new WriteHandlerMap(defaultHandlers());

         public static WriteHandlerMap getDefaultWriteHandlerMap() {
             return defaultWriteHandlerMap;
         }
     }
     ```
   - **Impact**: Improves code readability and maintainability by centralizing
     handler initialization.

4. **Use Utility Methods for Common Tasks**:
   - **Effort**: Low
   - **Return**: Medium
   - **Action**: Identify common tasks that are repeated across the codebase and
     extract them into utility methods. For example, string manipulation or date
     formatting can be centralized in the `Util` class.
   - **Example Refactoring**:
     ```java
     public class Util {
         public static String escapeString(String s) {
             int l = s.length();
             if (l > 0) {
                 char c = s.charAt(0);
                 if (c == Constants.ESC || c == Constants.SUB || c == Constants.RESERVED) {
                     return Constants.ESC + s;
                 }
             }
             return s;
         }
     }
     ```
   - **Impact**: Reduces code duplication and makes the codebase more DRY (Don't
     Repeat Yourself).

5. **Simplify Class Naming and Structure**:
   - **Effort**: Low
   - **Return**: Medium
   - **Action**: Ensure that class names clearly reflect their purpose and
     structure. For example, `JsonVerboseEmitter` can be simplified if it's just
     an extension of `JsonEmitter` with additional functionality.
   - **Example Refactoring**:
     ```java
     public class JsonVerboseEmitter extends JsonEmitter {
         // Existing code here
     }
     ```
   - **Impact**: Improves code readability and makes it easier for new
     developers to understand the codebase.

## Java 17 Impl

Java 17 introduced several new features and enhancements that can significantly
improve code readability, performance, and maintainability. Here are some
implementation areas in the provided code that could benefit from Java 17
features:

1. **Pattern Matching for `instanceof`**:
   - **Before**:
     ```java
     public Object parseString(Object o) {
         if (o instanceof String) {
             String s = (String) o;
             // existing logic
         }
         return o;
     }
     ```
   - **After**:
     ```java
     public Object parseString(Object o) {
         if (o instanceof String s) {
             // existing logic
         }
         return o;
     }
     ```
   - **Benefit**: Reduces boilerplate code and improves readability.

2. **Sealed Classes**:
   - **Use Case**: Sealing the handler classes or tag-related classes to
     restrict extensions.
   - **Before**:
     ```java
     public interface ReadHandler<T, Rep> {
         T fromRep(Rep rep);
     }

     public class KeywordReadHandler implements ReadHandler<Object, String> {
         @Override
         public Object fromRep(String rep) {
             return TransitFactory.keyword(rep);
         }
     }
     ```
   - **After**:
     ```java
     public sealed interface ReadHandler<T, Rep> permits KeywordReadHandler, SymbolReadHandler, ... {
         T fromRep(Rep rep);
     }

     public final class KeywordReadHandler implements ReadHandler<Object, String> {
         @Override
         public Object fromRep(String rep) {
             return TransitFactory.keyword(rep);
         }
     }
     ```
   - **Benefit**: Provides better control over the type hierarchy, enhancing
     security and maintainability.

3. **Records**:
   - **Use Case**: Simplifying data carrier classes.
   - **Before**:
     ```java
     public class Tag {
         private final String value;

         public Tag(String value) {
             this.value = value;
         }

         public String getValue() {
             return value;
         }
     }
     ```
   - **After**:
     ```java
     public record Tag(String value) {}
     ```
   - **Benefit**: Reduces boilerplate code and automatically provides methods
     like `equals`, `hashCode`, and `toString`.

4. **Text Blocks**:
   - **Use Case**: Simplifying JSON or XML strings.
   - **Before**:
     ```java
     String json = "{\n" +
                   "  \"key1\": \"value1\",\n" +
                   "  \"key2\": \"value2\"\n" +
                   "}";
     ```
   - **After**:
     ```java
     String json = """
                   {
                     "key1": "value1",
                     "key2": "value2"
                   }
                   """;
     ```
   - **Benefit**: Improves readability and maintainability of multi-line
     strings.

5. **Enhanced `switch` Expressions**:
   - **Use Case**: Simplifying complex switch statements.
   - **Before**:
     ```java
     switch(s.charAt(0)) {
         case Constants.ESC:
             switch (s.charAt(1)) {
                 case Constants.ESC:
                 case Constants.SUB:
                 case Constants.RESERVED:
                     return s.substring(1);
                 case Constants.TAG:
                     return new Tag(s.substring(2));
                 default:
                     return decode(s.substring(1, 2), s.substring(2));
             }
         case Constants.SUB:
             if (s.charAt(1) == ' ') {
                 return Constants.MAP_AS_ARRAY;
             }
     }
     ```
   - **After**:
     ```java
     return switch(s.charAt(0)) {
         case Constants.ESC -> switch (s.charAt(1)) {
             case Constants.ESC, Constants.SUB, Constants.RESERVED -> s.substring(1);
             case Constants.TAG -> new Tag(s.substring(2));
             default -> decode(s.substring(1, 2), s.substring(2));
         };
         case Constants.SUB && s.charAt(1) == ' ' -> Constants.MAP_AS_ARRAY;
         default -> o;
     };
     ```
   - **Benefit**: Increases readability and reduces boilerplate code.

### Specific Implementation Areas

1. **Parsing Logic**:
   - The `AbstractParser` and its subclasses can benefit from `instanceof`
     pattern matching and enhanced switch expressions to reduce boilerplate and
     improve readability.

2. **Handler Classes**:
   - Classes implementing `ReadHandler` and `WriteHandler` can use sealed
     classes to restrict the hierarchy and records for data carriers.

3. **Data Classes**:
   - Classes like `Tag`, `KeywordImpl`, `SymbolImpl`, and `URIImpl` can be
     refactored to records if they are primarily data carriers.

4. **Utility Methods**:
   - Methods dealing with multi-line strings in `Util` or other classes can use
     text blocks for cleaner string handling.

### Example Refactoring

Here's an example that combines several Java 17 features:

**Before**:

```java
public class Tag {
    private final String value;

    public Tag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

public class JsonParser extends AbstractParser {
    private final com.fasterxml.jackson.core.JsonParser jp;

    public JsonParser(com.fasterxml.jackson.core.JsonParser jp, Map<String, ReadHandler<?,?>> handlers, DefaultReadHandler<?> defaultHandler, MapReader<?, Map<Object, Object>, Object, Object> mapBuilder, ArrayReader<?, List<Object>, Object> listBuilder) {
        super(handlers, defaultHandler, mapBuilder, listBuilder);
        this.jp = jp;
    }

    private Object parseLong() throws IOException {
        Object val;
        try {
            val = jp.getLongValue();
        } catch(IOException e) {
            val = new BigInteger(jp.getText());
        }
        return val;
    }
}
```

**After**:

```java
public record Tag(String value) {}

public class JsonParser extends AbstractParser {
    private final com.fasterxml.jackson.core.JsonParser jp;

    public JsonParser(com.fasterxml.jackson.core.JsonParser jp, Map<String, ReadHandler<?,?>> handlers, DefaultReadHandler<?> defaultHandler, MapReader<?, Map<Object, Object>, Object, Object> mapBuilder, ArrayReader<?, List<Object>, Object> listBuilder) {
        super(handlers, defaultHandler, mapBuilder, listBuilder);
        this.jp = jp;
    }

    private Object parseLong() throws IOException {
        return switch (jp.getCurrentToken()) {
            case VALUE_NUMBER_INT -> {
                try {
                    yield jp.getLongValue();
                } catch (IOException e) {
                    yield new BigInteger(jp.getText());
                }
            }
            default -> throw new IllegalArgumentException("Unsupported token type");
        };
    }
}
```

## Handlers via ServiceLoader

To refactor the code to detect `ReadHandlers` and `WriteHandlers` via the Java
`ServiceLoader`, we can follow a systematic approach. This refactoring will
allow dynamic discovery and registration of handlers, similar to the JAX-RS
provider mechanism for `MessageBodyReader` and `MessageBodyWriter`.

### Step-by-Step Refactoring Outline

1. **Define Service Provider Interfaces (SPIs) for Handlers**:
   - Create `ReadHandlerProvider` and `WriteHandlerProvider` interfaces that
     will be implemented by service providers.

2. **Implement Service Provider Interfaces**:
   - Implement these interfaces in classes that will provide multiple
     `ReadHandlers` and `WriteHandlers`.

3. **Use `ServiceLoader` to Discover Handlers**:
   - Modify the `ReaderFactory` and `WriterFactory` to use the `ServiceLoader`
     to discover and register handlers.

4. **Implement a Priority System**:
   - Introduce a priority system to determine the order of handlers. This can be
     done using an annotation or an interface method.

### Detailed Implementation

#### 1. Define Service Provider Interfaces

Create interfaces for service providers that can provide multiple handlers.

```java
package com.cognitect.transit.spi;

import com.cognitect.transit.ReadHandler;
import com.cognitect.transit.WriteHandler;

import java.util.Map;

public interface ReadHandlerProvider {
    Map<String, ReadHandler<?, ?>> getReadHandlers();
}

public interface WriteHandlerProvider {
    Map<Class<?>, WriteHandler<?, ?>> getWriteHandlers();
}
```

#### 2. Implement Service Provider Interfaces

Implement these interfaces in your handler provider classes.

```java
package com.cognitect.transit.handlers;

import com.cognitect.transit.ReadHandler;
import com.cognitect.transit.spi.ReadHandlerProvider;

import java.util.HashMap;
import java.util.Map;

public class DefaultReadHandlerProvider implements ReadHandlerProvider {
    @Override
    public Map<String, ReadHandler<?, ?>> getReadHandlers() {
        Map<String, ReadHandler<?, ?>> handlers = new HashMap<>();
        handlers.put(":", new KeywordReadHandler());
        handlers.put("$", new SymbolReadHandler());
        // Add more handlers as needed
        return handlers;
    }
}
```

Similarly, for `WriteHandlerProvider`.

#### 3. Use `ServiceLoader` to Discover Handlers

Modify the `ReaderFactory` and `WriterFactory` to use the `ServiceLoader`.

```java
package com.cognitect.transit.impl;

import com.cognitect.transit.ReadHandler;
import com.cognitect.transit.spi.ReadHandlerProvider;
import com.cognitect.transit.DefaultReadHandler;

import java.util.*;

public class ReaderFactory {
    private static Map<String, ReadHandler<?,?>> handlerMap = loadHandlers();

    private static Map<String, ReadHandler<?,?>> loadHandlers() {
        Map<String, ReadHandler<?,?>> handlers = new HashMap<>(defaultHandlers());
        ServiceLoader<ReadHandlerProvider> loader = ServiceLoader.load(ReadHandlerProvider.class);
        for (ReadHandlerProvider provider : loader) {
            handlers.putAll(provider.getReadHandlers());
        }
        return handlers;
    }

    public static Reader getJsonInstance(InputStream in, DefaultReadHandler<?> customDefaultHandler) {
        // Use handlerMap for creating Reader
    }
}
```

Similarly, for `WriterFactory`.

#### 4. Implement a Priority System

Use an annotation or interface method to specify priorities.

```java
package com.cognitect.transit.spi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HandlerPriority {
    int value();
}
```

Apply the annotation to handler providers.

```java
package com.cognitect.transit.handlers;

import com.cognitect.transit.spi.HandlerPriority;
import com.cognitect.transit.spi.ReadHandlerProvider;
import com.cognitect.transit.ReadHandler;

import java.util.HashMap;
import java.util.Map;

@HandlerPriority(1)
public class DefaultReadHandlerProvider implements ReadHandlerProvider {
    @Override
    public Map<String, ReadHandler<?, ?>> getReadHandlers() {
        Map<String, ReadHandler<?, ?>> handlers = new HashMap<>();
        handlers.put(":", new KeywordReadHandler());
        handlers.put("$", new SymbolReadHandler());
        // Add more handlers as needed
        return handlers;
    }
}
```

Sort handlers by priority when loading them.

```java
package com.cognitect.transit.impl;

import com.cognitect.transit.ReadHandler;
import com.cognitect.transit.spi.ReadHandlerProvider;
import com.cognitect.transit.spi.HandlerPriority;

import java.util.*;

public class ReaderFactory {
    private static Map<String, ReadHandler<?,?>> handlerMap = loadHandlers();

    private static Map<String, ReadHandler<?,?>> loadHandlers() {
        Map<String, ReadHandler<?,?>> handlers = new HashMap<>(defaultHandlers());
        ServiceLoader<ReadHandlerProvider> loader = ServiceLoader.load(ReadHandlerProvider.class);
        List<ReadHandlerProvider> providers = new ArrayList<>();
        loader.forEach(providers::add);
        providers.sort(Comparator.comparingInt(p -> p.getClass().getAnnotation(HandlerPriority.class).value()));
        for (ReadHandlerProvider provider : providers) {
            handlers.putAll(provider.getReadHandlers());
        }
        return handlers;
    }

    public static Reader getJsonInstance(InputStream in, DefaultReadHandler<?> customDefaultHandler) {
        // Use handlerMap for creating Reader
    }
}
```

### Final Ordering of Handlers

A priority system is a good idea for determining the final order of handlers.
Using annotations like `@HandlerPriority` allows for a flexible and clear
mechanism to control the order in which handlers are applied.

### Example Usage

To use the refactored system, you would implement your handler providers and
register them via the `ServiceLoader` mechanism by creating a file named
`META-INF/services/com.cognitect.transit.spi.ReadHandlerProvider` and
`META-INF/services/com.cognitect.transit.spi.WriteHandlerProvider` with the
class names of your provider implementations.

### Summary

This refactoring leverages the Java `ServiceLoader` to dynamically discover and
register `ReadHandlers` and `WriteHandlers`, providing a modular and extensible
system. The priority system ensures that handlers are processed in the correct
order, similar to the JAX-RS provider mechanism for `MessageBodyReader` and
`MessageBodyWriter`. This approach enhances flexibility and maintainability
while maintaining the same ergonomics as JAX-RS.
