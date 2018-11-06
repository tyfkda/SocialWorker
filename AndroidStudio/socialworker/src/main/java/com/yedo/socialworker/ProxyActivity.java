package com.yedo.socialworker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

// シェア結果を取得するためのアクティビティ
public class ProxyActivity extends Activity {
  public static final String KEY_POST = "post";
  public static final String KEY_IMAGE_PATH = "image";
  public static final String KEY_NAME = "name";
  public static final String KEY_CHOOSER = "chooser";

  private static final String TAG = ProxyActivity.class.getSimpleName();

  private static final int REQUEST_CODE = 1234;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent fromIntent = getIntent();
    String post = fromIntent.getStringExtra(KEY_POST);
    String imagePath = fromIntent.getStringExtra(KEY_IMAGE_PATH);
    String name = fromIntent.getStringExtra(KEY_NAME);
    boolean chooser = fromIntent.getBooleanExtra(KEY_CHOOSER, false);

    try {
      String type = SocialWorker.getIntentTypeForImage(imagePath);
      Intent intent = SocialWorker.createAppIntent(name, Intent.ACTION_SEND, type);
      if (intent != null) {
        intent.putExtra(Intent.EXTRA_TEXT, post);
        if (imagePath != null && !imagePath.equals("")) {
          intent = SocialWorker.shareImageIntent(intent, imagePath);
        }
        if (chooser) {
          intent = Intent.createChooser(intent, "Share");
        }
        startActivityForResult(intent, REQUEST_CODE);
        //UnityPlayer.UnitySendMessage(SocialWorker.UNITY_SEND_GAMEOBJECT, SocialWorker.UNITY_SEND_CALLBACK, SocialWorker.RESULT_SUCCESS);
        UnityPlayer.UnitySendMessage(SocialWorker.UNITY_SEND_GAMEOBJECT, SocialWorker.UNITY_SEND_CALLBACK, SocialWorker.RESULT_DIALOG_OPENED);
      } else {
        UnityPlayer.UnitySendMessage(SocialWorker.UNITY_SEND_GAMEOBJECT, SocialWorker.UNITY_SEND_CALLBACK, SocialWorker.RESULT_NOT_AVAILABLE);
        finish();
      }
    } catch(Exception e) {
      Log.e(TAG, "postTwitterOrFacebook", e);
      UnityPlayer.UnitySendMessage(SocialWorker.UNITY_SEND_GAMEOBJECT, SocialWorker.UNITY_SEND_CALLBACK, SocialWorker.RESULT_ERROR);
      finish();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    UnityPlayer.UnitySendMessage(SocialWorker.UNITY_SEND_GAMEOBJECT, SocialWorker.UNITY_SEND_CALLBACK,
        resultCode == 0 ? SocialWorker.RESULT_CANCELLED : SocialWorker.RESULT_POST_DONE);
    finish();
  }
}
