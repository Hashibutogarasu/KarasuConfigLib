# カラス印の設定ライブラリ

KarasuConfigLib は、Bukkit プラグイン用の設定ファイル管理ライブラリです。JSON 形式での設定ファイルの読み書きを簡単に行えます。

## 特徴

- 簡単な設定クラスの定義
- JSON 形式での自動シリアライズ/デシリアライズ
- 複数の設定ファイルの管理
- カスタム型のサポート
- 設定リストのサポート

## インストール方法

### Gradle を使用する場合

```gradle
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/karasu256/KarasuConfigLib")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation 'com.karasu256:karasuConfigLib:0.1.0.4'
}
```

> **注意**: GitHub Packagesを使用するには認証が必要です。`gradle.properties` ファイルに認証情報を追加するか、環境変数を設定してください。
> 
> ```
> gpr.user=YOUR_GITHUB_USERNAME
> gpr.key=YOUR_GITHUB_PERSONAL_ACCESS_TOKEN
> ```
> 
> または環境変数 `GITHUB_USERNAME` と `GITHUB_TOKEN` を設定してください。

### プラグインとして使用する場合

1. 最新の`.jar`ファイルをダウンロード
2. サーバーの`plugins`フォルダに配置

## 基本的な使い方

### 1. 設定クラスの作成

設定クラスは`BaseConfig`を継承し、`@Config`アノテーションを使用して設定します。

```java
import com.karasu256.karasuConfigLib.KarasuConfigLib;
import com.karasu256.karasuConfigLib.annotation.Config;
import com.karasu256.karasuConfigLib.config.BaseConfig;

@Config(fileName = "myConfig.json", pluginName = KarasuConfigLib.PLUGIN_NAME)
public class MyConfig extends BaseConfig {
    private String serverName = "Default Server";
    private int maxPlayers = 20;
    private boolean enableFeature = true;

    // デフォルトコンストラクタ（必須）
    public MyConfig() {
    }

    // ゲッターとセッター
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isEnableFeature() {
        return enableFeature;
    }

    public void setEnableFeature(boolean enableFeature) {
        this.enableFeature = enableFeature;
    }
}
```

### 2. プラグインでの実装

JavaPlugin の代わりに`AbstractJavaPluginConfigable`を継承します。

```java
import com.karasu256.karasuConfigLib.AbstractJavaPluginConfigable;
import com.karasu256.karasuConfigLib.config.BaseConfig;
import java.util.List;

public class MyPlugin extends AbstractJavaPluginConfigable<MyConfig> {
    
    public static final String PLUGIN_NAME = "MyPlugin";

    @Override
    public void onEnable() {
        super.onEnable(); // 設定ファイルの初期化と読み込みを行います

        // 設定値の利用
        MyConfig config = getConfig(MyConfig.class);
        getLogger().info("サーバー名: " + config.getServerName());
        getLogger().info("最大プレイヤー数: " + config.getMaxPlayers());

        // 設定の変更と保存
        config.setServerName("新しいサーバー名");
        saveConfig("myConfig.json");
    }

    @Override
    public void onDisable() {
        super.onDisable(); // 設定の保存を行います
    }

    @Override
    public List<Class<? extends MyConfig>> getDefaultConfigs() {
        return List.of(MyConfig.class);
    }

    @Override
    public Class<MyConfig> getBaseConfig() {
        return MyConfig.class;
    }
}
```

## 複数の設定ファイルの管理

複数の設定クラスを登録して管理できます。

```java
@Override
public List<Class<? extends MyConfig>> getDefaultConfigs() {
    return List.of(
        MainConfig.class,
        DatabaseConfig.class,
        PermissionsConfig.class
    );
}
```

各設定ファイルへのアクセス：

```java
MainConfig mainConfig = getConfig(MainConfig.class);
DatabaseConfig dbConfig = getConfig(DatabaseConfig.class);
```

## 設定リストの管理

リスト形式の設定も簡単に管理できます。

```java
// リストの読み込み
List<UserConfig> users = loadList("users.json");

// 新しい項目の追加
UserConfig newUser = new UserConfig("username", 100);
users.add(newUser);

// リストの保存
saveList(users, "users.json");
```

## カスタム型のサポート

独自のクラスをシリアライズ/デシリアライズするためのカスタムアダプタを登録できます。

```java
// カスタムアダプタの登録
TypeAdapter<LocalDateTime> dateAdapter = new TypeAdapter<LocalDateTime>() {
    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return LocalDateTime.parse(in.nextString());
    }
};

BaseConfig.registerTypeAdapter(LocalDateTime.class, dateAdapter);
```

## 高度な使い方

### 設定の再読み込み

```java
// 特定の設定ファイルの再読み込み
reloadConfig("myConfig.json", MyConfig.class);

// すべての設定ファイルの再読み込み
reloadAll();
```

### 設定フォルダの取得

```java
File configFolder = getPluginConfigFolder(MyConfig.class);
```

## API リファレンス

### 主要クラス

- `AbstractJavaPluginConfigable<T>` - プラグインの基本クラス
- `BaseConfig` - すべての設定クラスの基底クラス
- `@Config` - 設定クラスに使用するアノテーション

### 重要なメソッド

#### AbstractJavaPluginConfigable クラス

| メソッド                                           | 説明                                       |
| -------------------------------------------------- | ------------------------------------------ |
| `getConfig(Class<C> configClass)`                  | 指定したクラスの設定を取得                 |
| `getConfig(String fileName, Class<C> configClass)` | 指定したファイル名と設定クラスで設定を取得 |
| `addConfig(String fileName, Class<C> configClass)` | 新しい設定ファイルを追加                   |
| `save()`                                           | すべての設定を保存                         |
| `save(String fileName)`                            | 指定したファイルの設定を保存               |
| `reloadAll()`                                      | すべての設定を再読み込み                   |
| `loadList(String fileName)`                        | 設定リストを読み込み                       |
| `saveList(List<T> configList, String fileName)`    | 設定リストを保存                           |

#### BaseConfig クラス

| メソッド                                                                        | 説明                                  |
| ------------------------------------------------------------------------------- | ------------------------------------- |
| `toJson()`                                                                      | 設定を JSON 文字列に変換              |
| `fromJson(String json, Class<T> type)`                                          | JSON 文字列から設定オブジェクトを生成 |
| `registerTypeAdapter(Type type, TypeAdapter<T> adapter)`                        | カスタム型アダプタを登録              |
| `registerTypeAdapterFactory(Class<?> factoryClass, TypeAdapterFactory factory)` | カスタム型アダプタファクトリを登録    |

## ライセンス

このレポジトリはCC BYライセンスの下で公開されています