package com.karasu256.karasuConfigLib;

import com.karasu256.karasuConfigLib.config.BaseConfig;
import com.karasu256.karasuConfigLib.config.ExampleConfig;
import com.karasu256.karasuConfigLib.config.TestConfig;

import java.util.List;

/**
 * 設定ファイル管理ライブラリのメインプラグインクラスです。
 * <p>
 * このプラグインは、Bukkitプラグイン用の設定ファイル管理ライブラリを提供します。
 * {@link ExampleConfig}をデフォルト設定クラスとして使用し、
 * JSON形式での設定ファイルの読み書きを行います。
 * </p>
 * 
 * <p>
 * 他のプラグインはこのライブラリを使用することで、簡単に設定ファイルを管理できます。
 * </p>
 * 
 * @author Hashibutogarasu
 * @version 1.0
 * @see AbstractJavaPluginConfigable
 * @see ExampleConfig
 * @see TestConfig
 */
public final class KarasuConfigLib extends AbstractJavaPluginConfigable<ExampleConfig> {
    
    /**
     * プラグインの名前を表す定数です。
     * 設定ファイルのディレクトリ名などに使用されます。
     */
    public static final String PLUGIN_NAME = "KarasuConfigLib";

    /**
     * プラグインが有効化されたときに呼び出されるメソッドです。
     * <p>
     *     親クラスの処理を実行して、設定ファイルを読み込みます。
     * </p>
     */
    @Override
    public void onEnable() {
        super.onEnable();
    }

    /**
     * プラグインが無効化されたときに呼び出されるメソッドです。
     * <p>
     *     親クラスの処理を実行して、設定ファイルを保存します。
     * </p>
     */
    @Override
    public void onDisable() {
        super.onDisable();
    }

    /**
     * デフォルトで読み込む設定クラスのリストを返します。
     * <p>
     * このプラグインでは、{@link ExampleConfig}と{@link TestConfig}を
     * デフォルトの設定クラスとして使用します。
     * </p>
     * 
     * @return デフォルトの設定クラスのリスト
     */
    @Override
    public List<Class<? extends BaseConfig>> getDefaultConfigs() {
        return List.of(ExampleConfig.class, TestConfig.class);
    }
}
