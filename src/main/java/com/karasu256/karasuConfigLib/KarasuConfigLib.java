package com.karasu256.karasuConfigLib;

import com.karasu256.karasuConfigLib.config.ExampleBaseConfig;
import com.karasu256.karasuConfigLib.config.TestConfig;

import java.util.List;

/**
 * 設定ファイル管理ライブラリのメインプラグインクラスです。
 * <p>
 * このプラグインは、Bukkitプラグイン用の設定ファイル管理ライブラリを提供します。
 * {@link ExampleBaseConfig}をデフォルト設定クラスとして使用し、
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
 * @see ExampleBaseConfig
 * @see TestConfig
 */
public final class KarasuConfigLib extends AbstractJavaPluginConfigable<ExampleBaseConfig> {
    /**
     * プラグインの名前を表す定数です。
     * 設定ファイルのディレクトリ名などに使用されます。
     */
    public static final String PLUGIN_NAME = "KarasuConfigLib";

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public List<Class<? extends ExampleBaseConfig>> getDefaultConfigs() {
        return List.of(ExampleBaseConfig.class, TestConfig.class);
    }

    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }
}
