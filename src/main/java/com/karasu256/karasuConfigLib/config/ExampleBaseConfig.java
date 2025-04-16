package com.karasu256.karasuConfigLib.config;

import com.karasu256.karasuConfigLib.KarasuConfigLib;
import com.karasu256.karasuConfigLib.annotation.Config;

/**
 * サンプル設定クラス
 * <p>
 * このクラスは、KarasuConfigLibの基本的な使用方法を示すためのサンプル設定クラスです。
 * {@link BaseConfig}を継承し、{@link Config}アノテーションを使用して設定ファイルを定義します。
 * </p>
 * 
 * <p>
 * 設定ファイルは、JSONフォーマットで保存され、プラグインの再起動時に自動的に読み込まれます。
 * すべての設定クラスは{@link BaseConfig}を継承し、デフォルトコンストラクタを持つ必要があります。
 * </p>
 * 
 * <p>設定例：</p>
 * <pre>
 * {
 *   "exampleString": "defaultString",
 *   "exampleInt": 42,
 *   "exampleBoolean": true
 * }
 * </pre>
 * 
 * @author Hashibutogarasu
 * @version 1.0
 * @see BaseConfig
 * @see Config
 */
@Config(fileName = "exampleConfig.json", pluginName = KarasuConfigLib.PLUGIN_NAME)
public class ExampleBaseConfig extends BaseConfig {
    
    /**
     * サンプル文字列設定
     * <p>
     * 文字列形式の設定値を格納します。デフォルト値は "defaultString" です。
     * </p>
     */
    private String exampleString = "defaultString";
    
    /**
     * サンプル整数設定
     * <p>
     * 整数形式の設定値を格納します。デフォルト値は 42 です。
     * </p>
     */
    private int exampleInt = 42;
    
    /**
     * サンプル真偽値設定
     * <p>
     * 真偽値形式の設定値を格納します。デフォルト値は true です。
     * </p>
     */
    private boolean exampleBoolean = true;

    /**
     * デフォルトコンストラクタ
     * <p>
     * すべての設定クラスはデフォルトコンストラクタを持つ必要があります。
     * このコンストラクタは、設定ファイルからインスタンスを生成する際に使用されます。
     * </p>
     */
    public ExampleBaseConfig() {
    }

    /**
     * パラメータ付きコンストラクタ
     * <p>
     * すべてのフィールドの初期値を指定して、設定オブジェクトを作成します。
     * </p>
     *
     * @param exampleString 文字列設定の初期値
     * @param exampleInt 整数設定の初期値
     * @param exampleBoolean 真偽値設定の初期値
     */
    public ExampleBaseConfig(String exampleString, int exampleInt, boolean exampleBoolean) {
        this.exampleString = exampleString;
        this.exampleInt = exampleInt;
        this.exampleBoolean = exampleBoolean;
    }

    /**
     * サンプル文字列設定の値を取得します
     *
     * @return 文字列設定の現在値
     */
    public String getExampleString() {
        return exampleString;
    }

    /**
     * サンプル文字列設定の値を設定します
     *
     * @param exampleString 設定する文字列値
     */
    public void setExampleString(String exampleString) {
        this.exampleString = exampleString;
    }

    public int getExampleInt() {
        return exampleInt;
    }

    public void setExampleInt(int exampleInt) {
        this.exampleInt = exampleInt;
    }

    public boolean isExampleBoolean() {
        return exampleBoolean;
    }

    public void setExampleBoolean(boolean exampleBoolean) {
        this.exampleBoolean = exampleBoolean;
    }
}
