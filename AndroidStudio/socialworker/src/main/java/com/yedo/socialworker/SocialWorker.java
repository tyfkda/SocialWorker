//----------------------------------------------
// SocialWorker
// © 2015 yedo-factory
//----------------------------------------------
package com.yedo.socialworker;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

/**
 * SocialWorker
 * @author okamura
 */
public class SocialWorker {
  /** タグ */
  public static final String TAG = SocialWorker.class.getSimpleName();
  /** 改行 */
  public static final String BR = System.getProperty("line.separator");

  /** UnitySendMessage：GameObject名 */
  public static final String UNITY_SEND_GAMEOBJECT = "SocialWorker";
  /** UnitySendMessage：コールバック名 */
  public static final String UNITY_SEND_CALLBACK = "OnSocialWorkerResult";

  /** 結果：成功 */
  public static final String RESULT_SUCCESS = "0";
  /** 結果：利用できない */
  public static final String RESULT_NOT_AVAILABLE = "1";
  /** 結果：予期せぬエラー */
  public static final String RESULT_ERROR = "2";

  /** 結果：ダイアログが開かれた */
  public static final String RESULT_DIALOG_OPENED = "3";
  /** 結果：キャンセルされた */
  public static final String RESULT_CANCELLED = "4";
  /** 結果：ポストされた */
  public static final String RESULT_POST_DONE = "5";

  /**
   * Twitter or Facebook 投稿。ただしFacebookは画像の投稿のみ許可しており、テキストの投稿は無視されることに注意。
   * @param isTwitter true：Twitter、false：Facebook
   * @param message メッセージ
   * @param url URL。空文字の場合は処理されない。
   * @param imagePath 画像パス(PNG/JPGのみ)。空文字の場合は処理されない。
   */
  public static void postTwitterOrFacebook(boolean isTwitter, String message, String url, String imagePath) {
    String name = (isTwitter) ? "com.twitter.android" : "com.facebook.katana";
    String post = message + SocialWorker.BR + url;

    Intent intent = new Intent(UnityPlayer.currentActivity, ProxyActivity.class);
    intent.putExtra(ProxyActivity.KEY_POST, post);
    intent.putExtra(ProxyActivity.KEY_IMAGE_PATH, imagePath);
    intent.putExtra(ProxyActivity.KEY_NAME, name);

    UnityPlayer.currentActivity.startActivity(intent);

    /*
    try {
			String type = getIntentTypeForImage(imagePath);
			Intent intent = createAppIntent(name, Intent.ACTION_SEND, type);
    		if(intent != null) {
    			intent.putExtra(Intent.EXTRA_TEXT, message + BR + url);
				if (imagePath != null && !imagePath.equals("")) {
					intent = shareImageIntent(intent, imagePath);
    			}
    			UnityPlayer.currentActivity.startActivity(intent);
    			UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_SUCCESS);
    		} else {
    			UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_NOT_AVAILABLE);
    		}
    	} catch(Exception e) {
    		Log.e(TAG, "postTwitterOrFacebook", e);
    		UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_ERROR);
    	}
    	*/
  }

  /**
   * Line投稿。Lineはメッセージと画像の同時投稿は行えないことに注意。
   * @param message メッセージ
   * @param imagePath 画像パス(PNG/JPGのみ)。空文字の場合は処理されない。
   */
  public static void postLine(String message, String imagePath) {
    try {
      Intent intent = createAppIntent("jp.naver.line.android", Intent.ACTION_SEND, "text/plain");
      if(intent != null) {
        if (imagePath != null && !imagePath.equals("")) {
          intent = new Intent(Intent.ACTION_VIEW, Uri.parse("line://msg/text/" + URLEncoder.encode(message, "UTF-8")));
        } else {
          intent = new Intent(Intent.ACTION_VIEW, Uri.parse("line://msg/image/" + imagePath));
        }
        UnityPlayer.currentActivity.startActivity(intent);
        UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_SUCCESS);
      } else {
        UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_NOT_AVAILABLE);
      }
    } catch(Exception e) {
      Log.e(TAG, "postLine", e);
      UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_ERROR);
    }
  }

  /**
   * Instagram投稿。Instagramは画像の投稿のみ行える。
   * @param imagePath 画像パス(PNG/JPGのみ)
   */
  public static void postInstagram(String imagePath) {
    try {
      Intent intent = createAppIntent("com.instagram.android", Intent.ACTION_SEND, getIntentTypeForImage(imagePath));
      if(intent != null) {
        if (imagePath != null && !imagePath.equals("")) {
          intent = shareImageIntent(intent, imagePath);
        }
        UnityPlayer.currentActivity.startActivity(intent);
        UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_SUCCESS);
      } else {
        UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_NOT_AVAILABLE);
      }
    } catch(Exception e) {
      Log.e(TAG, "postInstagram", e);
      UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_ERROR);
    }
  }

  /**
   * メール投稿
   * @param to 宛先。カンマ区切りの配列。
   * @param cc CC。カンマ区切りの配列。
   * @param bcc BCC。カンマ区切りの配列。
   * @param subject タイトル
   * @param message メッセージ
   * @param imagePath 画像パス(PNG/JPGのみ)。空文字の場合は処理されない。
   */
  public static void postMail(String to, String cc, String bcc, String subject, String message, String imagePath) {
    try {
      Intent intent = createAppIntent(null, Intent.ACTION_SEND, "message/rfc822");
      if(intent != null) {
        intent.putExtra(Intent.EXTRA_EMAIL, to.split(","));
        intent.putExtra(Intent.EXTRA_CC, cc.split(","));
        intent.putExtra(Intent.EXTRA_BCC, bcc.split(","));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (imagePath != null && !imagePath.equals("")) {
    			intent = shareImageIntent(intent, imagePath);
        }
        UnityPlayer.currentActivity.startActivity(intent);
        UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_SUCCESS);
      } else {
        UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_NOT_AVAILABLE);
      }
    } catch(Exception e) {
      Log.e(TAG, "postMail", e);
      UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_ERROR);
    }
  }

  /**
   * アプリ選択式での投稿
   * @param message メッセージ
   * @param imagePath 画像パス(PNG/JPGのみ)。空文字の場合は処理されない。
   */
  public static void createChooser(String message, String imagePath) {
    Intent intent = new Intent(UnityPlayer.currentActivity, ProxyActivity.class);
    intent.putExtra(ProxyActivity.KEY_POST, message);
    intent.putExtra(ProxyActivity.KEY_IMAGE_PATH, imagePath);
    intent.putExtra(ProxyActivity.KEY_CHOOSER, true);

    UnityPlayer.currentActivity.startActivity(intent);

      /*
    	try {
    		String type = getIntentTypeForImage(imagePath);
    		Intent intent = createAppIntent(null, Intent.ACTION_SEND, type);
    		if(intent != null) {
    			intent.putExtra(Intent.EXTRA_TEXT, message);
					if (imagePath != null && !imagePath.equals("")) {
    				intent = shareImageIntent(intent, imagePath);
    			}
    			UnityPlayer.currentActivity.startActivity(Intent.createChooser(intent, "Share"));
    			UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_SUCCESS);
    		} else {
    			UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_NOT_AVAILABLE);
    		}
    	} catch(Exception e) {
    		Log.e(TAG, "createChooser", e);
    		UnityPlayer.UnitySendMessage(UNITY_SEND_GAMEOBJECT, UNITY_SEND_CALLBACK, RESULT_ERROR);
    	}
    	*/
  }

  /**
   * 画像のIntentタイプを取得
   * @param imagePath 画像パス(PNG/JPGのみ)
   * @return Intentタイプ
   */
  public static String getIntentTypeForImage(String imagePath) {
    if (imagePath == null || imagePath.equals(""))
      return "text/plain";
    String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1);
    return extension.equalsIgnoreCase("png") ? "image/png" : "image/jpg";
  }

  /**
   * 特定のアプリを起動させるためのIntentを生成
   * @param name アプリパッケージ名。null or 空文字 で無視する。
   * @param action Intentアクション
   * @param type Intentタイプ
   * @return Intent。アプリがない場合は null
   */
  public static Intent createAppIntent(String name, String action, String type) throws Exception {
    try {
      Intent intent = new Intent(action);
      intent.setType(type);

      List<ResolveInfo> ris = UnityPlayer.currentActivity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
      if(name == null || name.equals("")) {
        return (!ris.isEmpty()) ? intent : null;
      } else {
        for (ResolveInfo ri : ris) {
          if (ri.activityInfo.packageName.equals(name)) {
            intent.setClassName(ri.activityInfo.packageName, ri.activityInfo.name);
            return intent;
          }
        }
      }
      return null;
    } catch (Exception e) {
      throw e;
    }
  }

  private static Uri getShareUri(String imagePath) {
    Uri shareUri = null;

    //if(isInternal(imagePath)){
    Context context = UnityPlayer.currentActivity;
    shareUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(imagePath));
    //}else{
    //	shareUri = Uri.fromFile(new File(imagePath));
    //}

    return shareUri;
  }

  public static Intent shareImageIntent(Intent baseIntent, String imagePath) {
    Intent intent = new Intent(baseIntent);
    Uri uri = getShareUri(imagePath);
    String mimeType = getIntentTypeForImage(imagePath);
    intent.setDataAndType(uri, mimeType);
    intent.putExtra(Intent.EXTRA_STREAM, uri);
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    return intent;
  }
}
