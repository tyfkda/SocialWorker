//----------------------------------------------
// SocialWorker
// © 2015 yedo-factory
//----------------------------------------------
using UnityEngine;
using UnityEngine.UI;
using System.IO;

namespace SWorker
{
    /// <summary>
    /// デモシーン
    /// </summary>
    public class Demo : MonoBehaviour
    {
        static readonly string ExtensionImage = ".png";
        //static readonly string ExtensionImage = ".jpeg";
        static readonly string message   = "message";
        static readonly string url = "http://yedo-factory.co.jp/";

        public Text Result;
        public RawImage Image;

        private static string ImageDir {
            get { return Application.temporaryCachePath + "/share"; }
        }

        private static string ImagePath {
            get { return ImageDir + "/image" + ExtensionImage; }
        }

        /// <summary>
        /// 開始処理
        /// </summary>
        void Start()
        {
            // Post画像は端末から読み込むので、ない場合はあらかじめ保存しておくこと
            if (File.Exists(ImagePath))
                File.Delete(ImagePath);

            Directory.CreateDirectory(ImageDir);
            Texture2D texture = (Texture2D)Image.texture;
            byte[] data = (ExtensionImage == ".png") ? texture.EncodeToPNG () : texture.EncodeToJPG ();
            File.WriteAllBytes(ImagePath, data); 
        }

        /// <summary>
        /// Twitter投稿
        /// </summary>
        public void OnPostTwitter()
        {
            SocialWorker.PostTwitter(message, url, ImagePath, OnResult);
        }

        /// <summary>
        /// Facebook投稿。ただしFacebookは画像の投稿のみ許可しており、テキストの投稿は無視されることに注意。
        /// </summary>
        public void OnPostFacebook()
        {
            SocialWorker.PostFacebook(ImagePath, OnResult);
        }

        /// <summary>
        /// Line投稿。Lineはメッセージと画像の同時投稿は行えないことに注意。
        /// </summary>
        public void OnPostLine()
        {
            SocialWorker.PostLine(message + "\n" + url, ImagePath, OnResult);
        }

        /// <summary>
        /// Instagram投稿。Instagramは画像の投稿のみ行える。
        /// </summary>
        public void OnPostInstagram()
        {
            SocialWorker.PostInstagram(ImagePath, OnResult);
        }

        /// <summary>
        /// メール投稿
        /// </summary>
        public void OnPostMail()
        {
            string[] to      = new string[] { "to@hoge.com" };
            string[] cc      = new string[] { "cc@hoge.com" };
            string[] bcc     = new string[] { "bcc@hoge.com" };
            string subject   = "subject";
            SocialWorker.PostMail(to, cc, bcc, subject, message + "\n" + url, ImagePath, OnResult);
        }

        /// <summary>
        /// アプリ選択式での投稿
        /// </summary>
        public void OnCreateChooser()
        {
            SocialWorker.CreateChooser(message + "\n" + url, ImagePath, OnResult);
        }

        /// <summary>
        /// 結果コールバック
        /// </summary>
        /// <param name="res">結果値</param>
        public void OnResult(SocialWorkerResult res)
        {
            switch(res)
            {
            case SocialWorkerResult.Success:
                Result.text = "Result : Success";
                break;
            case SocialWorkerResult.NotAvailable:
                Result.text = "Result : NotAvailable";
                break;
            case SocialWorkerResult.Error:
                Result.text = "Result : Error";
                break;
            }
        }
    }
}
