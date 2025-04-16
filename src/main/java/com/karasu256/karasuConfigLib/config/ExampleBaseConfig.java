package com.karasu256.karasuConfigLib.config;

import com.karasu256.karasuConfigLib.KarasuConfigLib;
import com.karasu256.karasuConfigLib.annotation.Config;

@Config(fileName = "exampleConfig.json", pluginName = KarasuConfigLib.PLUGIN_NAME)
public class ExampleConfig extends BaseConfig{
    private String exampleString = "defaultString";
    private int exampleInt = 42;
    private boolean exampleBoolean = true;

    /**
     * リファクタリングで使用されるため、必ず、空のコンストラクタを作成してください。
     */
    public ExampleConfig() {

    }

    /**
     * コンストラクタ
     * @param exampleString 文字列の例
     * @param exampleInt 数値の例
     * @param exampleBoolean 真偽値の例
     */
    public ExampleConfig(String exampleString, int exampleInt, boolean exampleBoolean) {
        this.exampleString = exampleString;
        this.exampleInt = exampleInt;
        this.exampleBoolean = exampleBoolean;
    }


    public String getExampleString() {
        return exampleString;
    }

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
