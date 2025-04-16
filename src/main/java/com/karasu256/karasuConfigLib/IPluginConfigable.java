package com.karasu256.karasuConfigLib;

import com.karasu256.karasuConfigLib.config.BaseConfig;

/**
 * プラグイン設定機能を提供するインターフェースです。
 * このインターフェースは、Bukkitプラグインに設定機能を追加するための基本的な
 * コントラクトを定義します。
 * 
 * <p>
 * これを実装するクラスは、{@link BaseConfig}を継承した設定クラスを
 * タイプパラメータとして指定する必要があります。
 * </p>
 * 
 * <p>
 * 例:
 * </p>
 * 
 * <pre>
 * public class MyPlugin extends JavaPlugin implements IPluginConfigable&lt;MyConfig&gt; {
 *     // 実装...
 * }
 * </pre>
 *
 * @param <T> プラグインが使用する設定クラス（{@link BaseConfig}を継承していること）
 * @author Hashibutogarasu
 * @version 1.0
 * @see AbstractJavaPluginConfigable
 * @see BaseConfig
 */
public interface IPluginConfigable<T extends BaseConfig> {

    /**
     * プラグインの基本設定クラスを返します。
     * 
     * <p>
     * このメソッドは、プラグインが使用する設定クラスの{@code Class}オブジェクトを返します。
     * デフォルト実装では{@link BaseConfig}クラス自体を返しますが、
     * 実装クラスでは通常このメソッドをオーバーライドして、
     * 実際に使用する設定クラスを返すべきです。
     * </p>
     * 
     * <p>
     * 例:
     * </p>
     * 
     * <pre>
     * &#64;Override
     * public Class&lt;MyConfig&gt; getBaseConfig() {
     *     return MyConfig.class;
     * }
     * </pre>
     *
     * @return プラグインの設定クラス
     */
    @SuppressWarnings("unchecked")
    default Class<T> getBaseConfig() {
        return (Class<T>) BaseConfig.class;
    }
}
