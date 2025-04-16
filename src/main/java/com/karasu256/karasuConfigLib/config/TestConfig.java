package com.karasu256.karasuConfigLib.config;

import com.karasu256.karasuConfigLib.KarasuConfigLib;
import com.karasu256.karasuConfigLib.annotation.Config;

/**
 * テスト用設定クラスです。
 * <p>
 * このクラスは、設定ライブラリのテストおよびサンプルとして使用される基本的な設定クラスです。
 * 単一の文字列プロパティ（foo）を持ち、それに対するゲッターとセッターを提供します。
 * </p>
 * 
 * <p>
 * {@link Config}アノテーションにより、ファイル名は「testConfig.json」として指定され、
 * プラグイン名は{@link KarasuConfigLib#PLUGIN_NAME}から取得されます。
 * </p>
 * 
 * @author Hashibutogarasu
 * @version 1.0
 * @see BaseConfig
 * @see Config
 */
@Config(fileName = "testConfig.json", pluginName = KarasuConfigLib.PLUGIN_NAME)
public class TestConfig extends ExampleBaseConfig {

    /**
     * 設定値を保持する文字列プロパティです。
     * デフォルト値は「bar」です。
     */
    private String foo = "bar";

    /**
     * デフォルトコンストラクタ。
     * 新しい{@code TestConfig}インスタンスを初期値で作成します。
     */
    public TestConfig() {

    }

    /**
     * fooプロパティの値を取得します。
     * 
     * @return fooプロパティの現在の値
     */
    public String getFoo() {
        return foo;
    }

    /**
     * fooプロパティの値を設定します。
     * 
     * @param foo 設定する新しい値
     */
    public void setFoo(String foo) {
        this.foo = foo;
    }
}
