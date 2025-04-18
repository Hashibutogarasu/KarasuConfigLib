package com.karasu256.karasuConfigLib.util;

import java.util.Optional;

import com.karasu256.karasuConfigLib.AbstractJavaPluginConfigable;
import com.karasu256.karasuConfigLib.annotation.Config;

/**
 * Configアノテーションからの情報取得を支援するユーティリティクラス
 * Class<T>オブジェクトからConfigアノテーションの情報を抽出します
 * 
 * @author Hashibutogarasu
 * @version 1.0
 * @see AbstractJavaPluginConfigable
 */
public class ConfigUtils {

    /**
     * 指定されたクラスからConfigアノテーションのプラグイン名を取得します
     * プラグイン名が空文字の場合は空のOptionalを返します
     *
     * @param <T>         設定クラスの型
     * @param configClass 設定クラス
     * @return プラグイン名（アノテーションが見つからない、または空文字の場合は空のOptional）
     */
    public static <T> Optional<String> getPluginName(Class<T> configClass) {
        if (configClass.isAnnotationPresent(Config.class)) {
            Config config = configClass.getAnnotation(Config.class);
            String pluginName = config.pluginName();
            return pluginName != null && !pluginName.isEmpty() ? Optional.of(pluginName) : Optional.empty();
        }
        return Optional.empty();
    }

    /**
     * 指定されたクラスからConfigアノテーションのファイル名を取得します
     *
     * @param <T>         設定クラスの型
     * @param configClass 設定クラス
     * @return ファイル名（アノテーションが見つからない場合は空のOptional）
     */
    public static <T> Optional<String> getFileName(Class<T> configClass) {
        if (configClass.isAnnotationPresent(Config.class)) {
            Config config = configClass.getAnnotation(Config.class);
            return Optional.of(config.fileName());
        }
        return Optional.empty();
    }

    /**
     * 指定されたクラスからConfigアノテーションの説明を取得します
     *
     * @param <T>         設定クラスの型
     * @param configClass 設定クラス
     * @return 説明（アノテーションが見つからない場合は空のOptional）
     */
    public static <T> Optional<String> getDescription(Class<T> configClass) {
        if (configClass.isAnnotationPresent(Config.class)) {
            Config config = configClass.getAnnotation(Config.class);
            return Optional.of(config.description());
        }
        return Optional.empty();
    }

    /**
     * 指定されたクラスからConfigアノテーションのすべての情報を取得します
     *
     * @param <T>         設定クラスの型
     * @param configClass 設定クラス
     * @return Configアノテーション（アノテーションが見つからない場合は空のOptional）
     */
    public static <T> Optional<Config> getConfigAnnotation(Class<T> configClass) {
        if (configClass.isAnnotationPresent(Config.class)) {
            return Optional.of(configClass.getAnnotation(Config.class));
        }
        return Optional.empty();
    }
}
