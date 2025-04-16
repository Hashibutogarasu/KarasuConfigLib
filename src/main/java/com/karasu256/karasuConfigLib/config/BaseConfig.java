package com.karasu256.karasuConfigLib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 設定クラスの基底抽象クラスです。
 * <p>
 * すべての設定クラスはこのクラスを継承する必要があります。このクラスは、
 * 設定オブジェクトをJSON形式に変換する機能と、JSON文字列から設定オブジェクトを
 * 復元する機能を提供します。
 * </p>
 * 
 * <p>
 * このクラスはシリアライズとデシリアライズの基本的な機能を提供し、
 * 継承クラスが独自の設定プロパティを追加して利用できるようにします。
 * </p>
 * 
 * <p>
 * 例:
 * </p>
 * 
 * <pre>
 * public class MyConfig extends BaseConfig {
 *     private String name = "default";
 *     private int value = 0;
 * 
 *     // getters and setters
 * }
 * </pre>
 * 
 * @author Hashibutogarasu
 * @version 1.0
 * @see com.karasu256.karasuConfigLib.AbstractJavaPluginConfigable
 */
public abstract class BaseConfig {
    /**
     * JSON変換に使用するGsonBuilderインスタンス。
     * すべてのアダプタや設定はこのビルダーに追加されます。
     */
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder().setPrettyPrinting();

    /**
     * 登録済みの型アダプタを保持するマップ
     */
    private static final Map<Type, TypeAdapter<?>> TYPE_ADAPTERS = new HashMap<>();

    /**
     * 登録済みの型アダプタファクトリを保持するリスト
     */
    private static final Map<Class<?>, TypeAdapterFactory> TYPE_ADAPTER_FACTORIES = new HashMap<>();

    /**
     * 現在のGsonBuilderから構築されたGsonインスタンス
     */
    private static Gson GSON = GSON_BUILDER.create();

    /**
     * カスタム型アダプタを登録します。
     * 
     * <p>
     * 特定の型のJSONシリアライズとデシリアライズの方法をカスタマイズするために、
     * 型アダプタを登録することができます。
     * </p>
     * 
     * <p>
     * 例:
     * </p>
     * 
     * <pre>
     * TypeAdapter&lt;LocalDateTime&gt; dateAdapter = new TypeAdapter&lt;LocalDateTime&gt;() {
     *     &#64;Override
     *     public void write(JsonWriter out, LocalDateTime value) throws IOException {
     *         if (value == null) {
     *             out.nullValue();
     *         } else {
     *             out.value(value.toString());
     *         }
     *     }
     * 
     *     &#64;Override
     *     public LocalDateTime read(JsonReader in) throws IOException {
     *         if (in.peek() == JsonToken.NULL) {
     *             in.nextNull();
     *             return null;
     *         }
     *         return LocalDateTime.parse(in.nextString());
     *     }
     * };
     * 
     * BaseConfig.registerTypeAdapter(LocalDateTime.class, dateAdapter);
     * </pre>
     * 
     * @param <T>     アダプタが対応する型
     * @param type    アダプタが対応する型のクラス
     * @param adapter 登録する型アダプタ
     */
    public static <T> void registerTypeAdapter(Type type, TypeAdapter<T> adapter) {
        TYPE_ADAPTERS.put(type, adapter);
        rebuildGson();
    }

    /**
     * カスタム型アダプタファクトリを登録します。
     * 
     * <p>
     * 型アダプタファクトリを使用すると、複数の関連する型に対して
     * 動的にアダプタを提供することができます。
     * </p>
     * 
     * @param factoryClass ファクトリクラス
     * @param factory      登録する型アダプタファクトリ
     */
    public static void registerTypeAdapterFactory(Class<?> factoryClass, TypeAdapterFactory factory) {
        TYPE_ADAPTER_FACTORIES.put(factoryClass, factory);
        rebuildGson();
    }

    /**
     * 登録されているすべての型アダプタとファクトリを適用して、
     * Gsonインスタンスを再構築します。
     */
    private static synchronized void rebuildGson() {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();

        // 登録されているすべての型アダプタを適用
        for (Map.Entry<Type, TypeAdapter<?>> entry : TYPE_ADAPTERS.entrySet()) {
            builder.registerTypeAdapter(entry.getKey(), entry.getValue());
        }

        // 登録されているすべての型アダプタファクトリを適用
        for (TypeAdapterFactory factory : TYPE_ADAPTER_FACTORIES.values()) {
            builder.registerTypeAdapterFactory(factory);
        }

        // GSONインスタンスを更新
        GSON = builder.create();
    }

    /**
     * 現在のGsonBuilderを取得します。
     * 
     * @return GsonBuilderインスタンス
     */
    public static GsonBuilder getGsonBuilder() {
        return GSON_BUILDER;
    }

    /**
     * 設定されたすべての型アダプタとファクトリを含むGsonインスタンスを取得します。
     * 
     * @return Gsonインスタンス
     */
    public static Gson getGson() {
        return GSON;
    }

    /**
     * このオブジェクトをJSON文字列に変換します。
     * 
     * @return JSON文字列
     */
    public String toJson() {
        return GSON.toJson(this);
    }

    /**
     * JSON文字列から指定された型のオブジェクトを生成します。
     * 
     * @param <T>  生成するオブジェクトの型
     * @param json JSON文字列
     * @param type 生成するオブジェクトのクラス
     * @return 指定された型のインスタンス
     */
    public static <T extends BaseConfig> T fromJson(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }
}
