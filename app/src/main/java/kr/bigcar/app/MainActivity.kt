package kr.bigcar.app

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.util.Log
import android.webkit.WebChromeClient
import kotlinx.android.synthetic.main.activity_main.*
import android.webkit.GeolocationPermissions
import android.webkit.ValueCallback
import android.webkit.WebView
import android.os.Parcelable
import android.provider.MediaStore
import java.io.File
import android.app.Activity


class MainActivity : AppCompatActivity() {

    val FILECHOOSER_NORMAL_REQ_CODE = 1
    val FILECHOOSER_LOLLIPOP_REQ_CODE = 2

    private var mUploadMessage: ValueCallback<Uri>? = null
    private var mCapturedImageURI: Uri? = null
    private var filePathCallbackNormal: ValueCallback<Uri>? = null;
    private var filePathCallbackLollipop: ValueCallback<Array<Uri>>? = null;
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webview.clearCache(true)
        webview.clearHistory()
        webview.settings.javaScriptEnabled = true
        webview.settings.javaScriptCanOpenWindowsAutomatically = true
        webview.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback)
                callback.invoke(origin, true, false)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                Log.d("file", "choose")
                if (filePathCallbackLollipop !== null) {
                    filePathCallbackLollipop = null
//                    filePathCallbackLollipop!!.onReceiveValue(null)
                }
                filePathCallbackLollipop = filePathCallback
                // Create AndroidExampleFolder at sdcard
                val imageStorageDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "AndroidExampleFolder"
                )
                if (!imageStorageDir.exists()) {
                    // Create AndroidExampleFolder at sdcard
                    imageStorageDir.mkdirs()
                }

                // Create camera captured image file path and name
                val file = File(imageStorageDir.toString() + File.separator + "IMG_" + System.currentTimeMillis().toString() + ".jpg")
                mCapturedImageURI = Uri.fromFile(file)

                val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)

                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "image/*"

                // Create file chooser intent
                val chooserIntent = Intent.createChooser(i, "Image Chooser")
                // Set camera intent to file chooser
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(captureIntent))

                startActivityForResult(Intent.createChooser(i, "File Choose"), FILECHOOSER_LOLLIPOP_REQ_CODE)
                return true
//                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
            }
        }
        webview.loadUrl("http://172.30.1.2:3000")
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === FILECHOOSER_NORMAL_REQ_CODE) {
            if (filePathCallbackNormal == null) return
            val result = if (data == null || resultCode !== Activity.RESULT_OK) null else data.data
            filePathCallbackNormal!!.onReceiveValue(result)
            filePathCallbackNormal = null
        } else if (requestCode === FILECHOOSER_LOLLIPOP_REQ_CODE) {
            var result: Array<Uri> = arrayOf()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (resultCode === Activity.RESULT_OK) {
                    result =
                        if (data == null) arrayOf<Uri>(mCapturedImageURI!!)
                        else WebChromeClient.FileChooserParams.parseResult(
                            resultCode,
                            data
                        )
                }
                filePathCallbackLollipop!!.onReceiveValue(result)
            }
        }
    }
}
