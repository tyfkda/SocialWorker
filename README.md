# SocialWorker
[【UnityAsset】SocialWorker – Twitter、Facebook、Line、Instagram、メールへの個別連携](http://okamura0510.jp/てんぷらブログ/socialworker/)


### オリジナルからの変更点

  * AndroidでFileProviderを使用するようにしました (from [Wonderplanet](https://github.com/Wonderplanet/SocialWorker))
    * それに伴いWrite External Storageのパーミッションは必要なくなりました。
  * Android用のプラグインを.jarから.aarに変更しました。
  * ダイアログが開いたときに`Success`ではなく`DialogOpened`を返し、その後の選択によって`PostDone`か`Cancelled`を返すようにしました。
    （`Success`は残してあるだけで、使われなくなりました。）
  * iOS11以降ではChooserしか使えません。

### Androidでのビルド方法

  * UnityプロジェクトのAssets/Plugins/Android/AndroidManifest.xmlにFileProviderの設定を記述する

```xml
<manifest ...>
  <application ...>
    ...
    <provider
        android:name="android.support.v4.content.FileProvider"
        android:authorities="[YourAppBundleName].fileprovider"
        android:grantUriPermissions="true"
        android:exported="false">
      <meta-data
          android:name="android.support.FILE_PROVIDER_PATHS"
          android:resource="@xml/provider_paths" />
    </provider>
```

  * Assets/Plugins/Android/res/xml/provider_paths.xmlに設定を記述する
    （このリポジトリのAssets/SocialWorker/Plugins/Android/res/xml/file_paths.xmlを参考に）

```
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
  <files-path name="share" path="share" />
  <cache-path name="share_cache" path="share" />
  <external-path name="share_external" path="Android/data/[YourAppBundleName]" />
</paths>
```

  * Unity用C#のスクリプトからは画像の格納先として `Application.temporaryCachePath` 以下、`/share/image.png` などのパスを使用する。
