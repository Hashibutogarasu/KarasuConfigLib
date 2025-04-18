package com.karasu256.karasuConfigLib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 設定クラスやフィールドに使用するアノテーション
 * このアノテーションを付与することで、リフレクションによる設定の自動読み込みが可能になります
 * 
 * @author Hashibutogarasu
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
public @interface Config {
    /**
     * 設定が属するプラグインの名前
     * 
     * @return プラグイン名
     */
    String pluginName() default "";

    /**
     * 設定ファイルの名前
     * 省略した場合はデフォルト値が使用されます
     * 
     * @return 設定ファイル名
     */
    String fileName();

    /**
     * 設定の説明（省略可能）
     * 将来的なドキュメント生成などに使用可能
     * 
     * @return 設定の説明
     */
    String description() default "";
}
