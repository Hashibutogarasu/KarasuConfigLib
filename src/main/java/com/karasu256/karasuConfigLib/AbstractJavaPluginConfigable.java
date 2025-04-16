package com.karasu256.karasuConfigLib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.karasu256.karasuConfigLib.annotation.Config;
import com.karasu256.karasuConfigLib.config.BaseConfig;
import com.karasu256.karasuConfigLib.util.ConfigUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * 設定ファイルを管理するプラグインのための抽象クラス
 * <p>
 * このクラスはJavaPluginを拡張し、設定ファイルを簡単に管理するための機能を提供します。
 * JSON形式の設定ファイルの読み込み、保存、更新などの操作を行うことができます。
 * </p>
 * 
 * @param <T> ベース設定クラスの型パラメータ
 * @author Hashibutogarasu
 * @version 1.0
 */
public abstract class AbstractJavaPluginConfigable<T extends BaseConfig> extends JavaPlugin
        implements IPluginConfigable<T> {

    /** クラス用のロガーインスタンス */
    public static final Logger LOGGER = Logger.getLogger(AbstractJavaPluginConfigable.class.getName());

    /** 設定ファイル名と設定オブジェクトのマッピング */
    private final Map<String, BaseConfig> configMap = new HashMap<>();

    @Override
    public void onEnable() {
        checkIfRecord();
        initializeDefaultConfigs();
        load();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        save();
    }

    /**
     * デフォルトの設定ファイルを初期化します
     */
    private void initializeDefaultConfigs() {
        List<Class<? extends T>> defaultConfigs = getDefaultConfigs();
        if (defaultConfigs != null) {
            for (Class<? extends T> configClass : defaultConfigs) {
                try {
                    String fileName = ConfigUtils.getFileName(configClass).orElseThrow();
                    addConfig(fileName, configClass);
                } catch (Exception e) {
                    LOGGER.severe("Failed to get config file name for class " + configClass.getName() + ": "
                            + e.getMessage());
                }
            }
            LOGGER.info("Initialized " + defaultConfigs.size() + " default config files");
        }
    }

    /**
     * デフォルトの設定クラスのリストを返します
     * 
     * @return 設定クラスのリスト
     */
    public abstract List<Class<? extends T>> getDefaultConfigs();

    /**
     * Configアノテーションに基づいて設定フォルダの名前を取得します
     * Configアノテーションが存在しない場合はプラグイン名を使用します
     * 
     * @param configClass 設定クラス
     * @return 設定フォルダ名
     */
    protected String getConfigFolderName(Class<?> configClass) {
        if (configClass != null) {
            Optional<String> pluginName = ConfigUtils.getPluginName(configClass);
            if (pluginName.isPresent()) {
                return pluginName.get();
            }
        }
        return getName();
    }

    /**
     * 設定フォルダのパスを取得します
     * 
     * @param configClass 設定クラス
     * @return 設定フォルダのパス
     */
    protected File getPluginConfigFolder(Class<?> configClass) {
        return getDataFolder().getParentFile().toPath().resolve(getConfigFolderName(configClass)).toFile();
    }

    /**
     * Tがレコードであるかをチェックします
     */
    private void checkIfRecord() {
        Class<T> configClass = getBaseConfig();
        if (configClass.isRecord()) {
            String errorMsg = "Record classes are not supported. Please use a regular class for the config.";
            LOGGER.severe(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
    }

    /**
     * 指定されたディレクトリが存在しない場合に作成します
     * 
     * @param directory 作成するディレクトリ
     * @return 作成に成功した場合はtrue、失敗した場合はfalse
     */
    protected boolean ensureDirectoryExists(File directory) {
        try {
            if (directory != null) {
                Files.createDirectories(directory.toPath());
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.severe("Failed to create directory " + directory + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * 設定クラスに対応するフォルダを作成します
     * 
     * @param configClass 設定クラス
     * @return 作成に成功した場合はtrue、失敗した場合はfalse
     */
    protected boolean ensureConfigFolderExists(Class<T> configClass) {
        File configFolder = getPluginConfigFolder(configClass);
        return ensureDirectoryExists(configFolder);
    }

    /**
     * 現在の設定を全て保存します
     */
    public void save() {
        configMap.forEach((fileName, config) -> {
            try {
                File configFolder = getPluginConfigFolder(config.getClass());
                ensureDirectoryExists(configFolder);
                ConfigUtils.getFileName(config.getClass()).ifPresent(fileName1 -> {
                    try {
                        var file = configFolder.toPath().resolve(fileName1);
                        Files.writeString(file, config.toJson());
                        LOGGER.info("Config saved to " + file);
                    } catch (Exception e) {
                        LOGGER.severe("Failed to save config: " + e.getMessage());
                    }
                });
            } catch (Exception e) {
                LOGGER.severe("Failed to save config: " + e.getMessage());
            }
        });
    }

    /**
     * 指定したファイル名の設定を保存します
     * 
     * @param fileName 保存する設定ファイル名
     * @return 保存に成功した場合はtrue、失敗した場合はfalse
     */
    public boolean save(String fileName) {
        BaseConfig config = configMap.get(fileName);
        if (config == null) {
            LOGGER.warning("Config for file " + fileName + " not found in configMap, cannot save.");
            return false;
        }

        try {
            var json = config.toJson();
            File configFolder = getPluginConfigFolder(config.getClass());
            ensureDirectoryExists(configFolder);
            var file = configFolder.toPath().resolve(fileName);
            Files.writeString(file, json);
            LOGGER.info("Config saved to " + file);
            return true;
        } catch (Exception e) {
            LOGGER.severe("Failed to save config for file " + fileName + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * 設定を読み込みます
     */
    public void load() {
        configMap.forEach((fileName, baseConfig) -> {
            try {
                var confClass = baseConfig.getClass();
                var file = getConfigFile(fileName);
                var filePath = file.toPath();
                var pluginConfigFolder = getPluginConfigFolder(confClass);
                ensureDirectoryExists(pluginConfigFolder);

                if (!Files.exists(filePath)) {
                    LOGGER.warning("Config file not found, creating a new one.");
                    var defaultConfig = getDefaultConfig(confClass);

                    if (defaultConfig == null) {
                        LOGGER.severe("Default config is null, cannot create a new config file.");
                        return;
                    }

                    var json = defaultConfig.toJson();
                    Files.writeString(filePath, json);
                }
                String json = Files.readString(filePath);
                BaseConfig config = getGson().fromJson(json, baseConfig.getClass());
                configMap.put(fileName, config);
                LOGGER.info("Config class loaded successfully");
            } catch (Exception e) {
                LOGGER.severe("Failed to load config: " + e.getMessage());
            }
        });
    }

    public void saveList(List<T> configList, String fileName) {
        if (configList == null) {
            LOGGER.warning("Config list is null, cannot save.");
            return;
        }

        try {
            // 親ディレクトリが存在しない場合は作成
            File parentDir = getDataFolder();
            ensureDirectoryExists(parentDir);
            var file = parentDir.toPath().resolve(fileName);

            // リストをJSONに変換
            String json = getGson().toJson(configList);

            java.nio.file.Files.writeString(file, json);
            LOGGER.info("Config list saved to " + file);
        } catch (Exception e) {
            LOGGER.severe("Failed to save config list: " + e.getMessage());
        }
    }

    public List<T> loadList(String fileName) {
        try {
            var pluginConfigFolder = getPluginConfigFolder(getBaseConfig());
            Files.createDirectories(pluginConfigFolder.toPath());

            var filePath = getDataFolder().toPath().resolve(fileName);

            if (!Files.exists(filePath)) {
                LOGGER.warning("Config list file not found, creating a new one with empty list: " + fileName);
                List<T> defaultList = new ArrayList<>();
                saveList(defaultList, fileName);
                return defaultList;
            }

            String json = Files.readString(filePath);

            // JSON文字列からリストを復元するためのTypeToken
            Type listType = TypeToken.getParameterized(List.class, getBaseConfig()).getType();
            List<T> configList = getGson().fromJson(json, listType);

            return configList != null ? configList : new ArrayList<>();
        } catch (Exception e) {
            LOGGER.severe("Failed to load config list: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public File getConfigFile(String fileName) {
        return getDataFolder().toPath().resolve(fileName).toFile();
    }

    public GsonBuilder getConfigGsonBuilder() {
        return BaseConfig.getGsonBuilder();
    }

    @Nullable
    public T getDefaultConfig() {
        try {
            return getBaseConfig().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            LOGGER.severe("Failed to create default config: " + e.getMessage());
            return null;
        }
    }

    /**
     * 指定したクラスのデフォルト設定インスタンスを取得します
     * 
     * @param configClass 設定クラス
     * @param <C>         BaseConfigを継承したクラス
     * @return デフォルト設定インスタンス、作成に失敗した場合はnull
     */
    @Nullable
    public <C extends BaseConfig> C getDefaultConfig(Class<C> configClass) {
        try {
            return configClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            LOGGER.severe("Failed to create default config for " + configClass.getName() + ": " + e.getMessage());
            return null;
        }
    }

    public Gson getGson() {
        return BaseConfig.getGson();
    }

    /**
     * 指定した名前と設定クラスで設定を登録します
     * 
     * @param fileName    ファイル名
     * @param configClass 設定クラス
     * @param <C>         BaseConfigを継承したクラス
     * @return 読み込まれた設定、ファイルが存在しない場合は新しいインスタンス
     */
    public <C extends T> C addConfig(String fileName, Class<C> configClass) {
        try {
            // ファイルから設定を読み込むか、新しいインスタンスを作成
            C config = loadConfigFromFile(fileName, configClass);

            // マップに登録
            configMap.put(fileName, config);

            return config;
        } catch (Exception e) {
            LOGGER.severe("Failed to add config " + fileName + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * 登録済みの設定を取得します
     * 
     * @param fileName    ファイル名
     * @param configClass 設定クラス
     * @param <C>         BaseConfigを継承したクラス
     * @return 設定オブジェクト、存在しない場合はnull
     */
    @SuppressWarnings("unchecked")
    public <C extends T> C getConfig(String fileName, Class<C> configClass) {
        BaseConfig config = configMap.get(fileName);
        if (config == null) {
            return addConfig(fileName, configClass);
        }

        if (!configClass.isInstance(config)) {
            LOGGER.warning("Config " + fileName + " is not an instance of " + configClass.getName());
            return null;
        }

        return (C) config;
    }

    /**
     * 指定したクラスの設定を取得します。ファイル名はクラスのアノテーションから
     * 取得されるか、クラス名をベースにしたデフォルト名が使用されます。
     * 
     * <p>
     * このメソッドは、指定されたクラスに対応する設定を取得します。設定が登録されていない場合は、
     * 自動的に新しい設定インスタンスが作成され、登録されます。ファイル名は{@link Config}アノテーションから
     * 取得されるか、クラス名に基づいて自動生成されます。
     * </p>
     * 
     * <p>
     * 例:
     * </p>
     * 
     * <pre>
     * MyConfig config = getConfig(MyConfig.class);
     * </pre>
     * 
     * @param configClass 設定クラス
     * @param <C>         BaseConfigを継承したクラス
     * @return 設定オブジェクト、存在しない場合は新しく作成されます
     * @see #getConfig(String, Class)
     * @see #getFileName(Class)
     */
    public <C extends T> C getConfig(Class<C> configClass) throws NoSuchElementException {
        String fileName = ConfigUtils.getFileName(configClass).orElseThrow();
        return getConfig(fileName, configClass);
    }

    /**
     * 指定したクラスタイプのすべての設定をconfigMapから取得し、リストとして返します。
     * 
     * <p>
     * このメソッドは、内部の設定マップから指定されたクラスタイプのすべてのインスタンスを
     * 検索し、それらをリストとして返します。これは異なるファイル名で登録された
     * 同じ型の複数の設定インスタンスを取得する場合に便利です。
     * </p>
     * 
     * <p>
     * 例えば、複数のプロファイル設定を持つ場合:
     * </p>
     * 
     * <pre>
     * List&lt;ProfileConfig&gt; allProfiles = getConfigsOfType(ProfileConfig.class);
     * </pre>
     * 
     * @param configClass 設定クラス
     * @param <C>         BaseConfigを継承したクラス
     * @return 該当するすべての設定オブジェクトのリスト（一致するものがない場合は空のリスト）
     */
    @SuppressWarnings("unchecked")
    public <C extends BaseConfig> List<C> getConfigsOfType(Class<C> configClass) {
        List<C> result = new ArrayList<>();

        for (BaseConfig config : configMap.values()) {
            if (configClass.isInstance(config)) {
                result.add((C) config);
            }
        }

        return result;
    }

    /**
     * 登録済みの設定をファイルに保存します
     * 
     * @param fileName ファイル名
     */
    public void saveConfig(String fileName) {
        BaseConfig config = configMap.get(fileName);
        if (config == null) {
            LOGGER.warning("Config " + fileName + " not found, cannot save.");
            return;
        }

        try {
            File configFolder = getPluginConfigFolder(config.getClass());
            // フォルダが存在しない場合は作成
            Files.createDirectories(configFolder.toPath());
            var file = configFolder.toPath().resolve(fileName);
            String json = config.toJson();
            Files.writeString(file, json);
            LOGGER.info("Config saved to " + file);
        } catch (Exception e) {
            LOGGER.severe("Failed to save config " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * ファイルから設定を読み込むか、存在しない場合は新しいインスタンスを作成します
     * 
     * @param fileName    ファイル名
     * @param configClass 設定クラス
     * @param <C>         BaseConfigを継承したクラス
     * @return 設定オブジェクト
     */
    private <C extends T> C loadConfigFromFile(String fileName, Class<C> configClass) {
        try {
            File configFolder = getPluginConfigFolder(configClass);
            configFolder.mkdirs();
            var filePath = configFolder.toPath().resolve(fileName);

            if (!Files.exists(filePath)) {
                LOGGER.warning("Config file not found, creating a new one: " + fileName);
                C defaultConfig = createNewInstance(configClass);
                String json = defaultConfig.toJson();
                Files.writeString(filePath, json);
                return defaultConfig;
            }

            String json = Files.readString(filePath);
            return BaseConfig.fromJson(json, configClass);
        } catch (Exception e) {
            LOGGER.severe("Failed to load config " + fileName + ": " + e.getMessage());
            return createNewInstance(configClass);
        }
    }

    /**
     * 設定クラスの新しいインスタンスを作成します
     * 
     * @param configClass 設定クラス
     * @param <C>         BaseConfigを継承したクラス
     * @return 新しいインスタンス
     */
    private <C extends BaseConfig> C createNewInstance(Class<C> configClass) {
        try {
            return configClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            LOGGER.severe("Failed to create new instance of " + configClass.getName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * すべての登録済み設定を再読み込みします。
     * ファイルが存在しない場合は、その設定をマップから削除します。
     * 
     * @return 正常に読み込まれた設定の数
     */
    public int reloadAll() {
        int successCount = 0;
        List<String> keysToRemove = new ArrayList<>();

        for (Map.Entry<String, BaseConfig> entry : new HashMap<>(configMap).entrySet()) {
            String fileName = entry.getKey();
            BaseConfig config = entry.getValue();

            if (config == null) {
                LOGGER.warning("Config is null for file: " + fileName + ", removing from registry");
                keysToRemove.add(fileName);
                continue;
            }

            Class<? extends BaseConfig> configClass = (Class<? extends BaseConfig>) config.getClass();

            // ファイルの存在確認
            File configFolder = getPluginConfigFolder(configClass);
            File configFile = configFolder.toPath().resolve(fileName).toFile();
            if (!configFile.exists()) {
                LOGGER.warning("Config file does not exist, removing from registry: " + fileName);
                keysToRemove.add(fileName);
                continue;
            }

            // 再読み込みを試行
            if (reloadConfig(fileName, configClass)) {
                successCount++;
            }
        }

        // 存在しないファイルのキーを削除
        for (String key : keysToRemove) {
            configMap.remove(key);
        }

        LOGGER.info("Reloaded " + successCount + " configs, removed " + keysToRemove.size() + " missing configs");
        return successCount;
    }

    /**
     * 指定した設定を再読み込みします
     * 
     * @param fileName    ファイル名
     * @param configClass 設定クラス
     * @param <C>         BaseConfigを継承したクラス
     * @return 読み込みに成功した場合はtrue
     */
    public <C extends BaseConfig> boolean reloadConfig(String fileName, Class<C> configClass) {
        try {
            File configFolder = getPluginConfigFolder(configClass);
            // フォルダが存在しない場合は作成
            Files.createDirectories(configFolder.toPath());
            File configFile = configFolder.toPath().resolve(fileName).toFile();
            if (!configFile.exists()) {
                LOGGER.warning("Config file does not exist: " + fileName);
                return false;
            }

            String json = Files.readString(configFile.toPath());
            C config = BaseConfig.fromJson(json, configClass);

            if (config == null) {
                LOGGER.warning("Failed to parse config from file: " + fileName);
                return false;
            }

            configMap.put(fileName, config);
            LOGGER.info("Successfully reloaded config: " + fileName);
            return true;
        } catch (Exception e) {
            LOGGER.severe("Error reloading config " + fileName + ": " + e.getMessage());
            return false;
        }
    }
}
