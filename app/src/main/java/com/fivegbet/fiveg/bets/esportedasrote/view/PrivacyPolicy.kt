package  com.fivegbet.fiveg.bets.esportedasrote.view

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fivegbet.fiveg.bets.esportedasrote.R
import com.fivegbet.fiveg.bets.esportedasrote.data.AppSettings
import com.fivegbet.fiveg.bets.esportedasrote.data.DataManager
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PrivacyPolicy : AppCompatActivity() {

    var test: WebView? = null
    private val file_type = "image/*"
    private var cam_file_data: String? = null
    private var file_data
            : ValueCallback<Uri>? = null
    private var file_path
            : ValueCallback<Array<Uri>>? = null
    private val file_req_code = 1
    var results: Array<Uri>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        var activityContext = this

        test = findViewById(R.id.helper_policy)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        test?.setWebChromeClient(object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView, isDialog: Boolean,
                isUserGesture: Boolean, resultMsg: Message
            ): Boolean {
                val newWebView = WebView(this@PrivacyPolicy)
                newWebView.settings.javaScriptEnabled = true
                newWebView.settings.setSupportZoom(true)
                newWebView.settings.builtInZoomControls = true
                newWebView.settings.pluginState = WebSettings.PluginState.ON
                newWebView.settings.setSupportMultipleWindows(true)
                newWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                view.addView(newWebView)
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                newWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        if (url.startsWith("http:") || url.startsWith("https:")) {
                            view.loadUrl(url)
                            return true
                        }
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        return true
                    }
                }
                return true
            }

            override fun onJsAlert(
                view: WebView,
                url: String,
                message: String,
                result: JsResult
            ): Boolean {
                return super.onJsAlert(view, url, message, result)
            }

            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                return if (file_permission() && Build.VERSION.SDK_INT >= 21) {
                    file_path = filePathCallback
                    var takePictureIntent: Intent? = null
                    var takeVideoIntent: Intent? = null
                    var includeVideo = false
                    var includePhoto = false

                    paramCheck@ for (acceptTypes in fileChooserParams.acceptTypes) {
                        val splitTypes = acceptTypes.split(", ?+")
                            .toTypedArray()
                        for (acceptType in splitTypes) {
                            when (acceptType) {
                                "*/*" -> {
                                    includePhoto = true
                                    includeVideo = true
                                    break@paramCheck
                                }
                                "image/*" -> includePhoto = true
                                "video/*" -> includeVideo = true
                            }
                        }
                    }
                    if (fileChooserParams.acceptTypes.size == 0) {   //no `accept` parameter was specified, allow both photo and video
                        includePhoto = true
                        includeVideo = true
                    }
                    if (includePhoto) {
                        takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (takePictureIntent.resolveActivity(this@PrivacyPolicy.getPackageManager()) != null) {
                            var photoFile: File? = null
                            try {
                                photoFile = create_image()
                                takePictureIntent.putExtra("PhotoPath", cam_file_data)
                            } catch (ex: IOException) {
                                Log.e("DEV", "Image file creation failed", ex)
                            }
                            if (photoFile != null) {
                                cam_file_data = "file:" + photoFile.absolutePath
                                takePictureIntent.putExtra(
                                    MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile)
                                )
                            } else {
                                cam_file_data = null
                                takePictureIntent = null
                            }
                        }
                    }
                    if (includeVideo) {
                        takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        if (takeVideoIntent.resolveActivity(this@PrivacyPolicy.getPackageManager()) != null) {
                            var videoFile: File? = null
                            try {
                                videoFile = create_video()
                            } catch (ex: IOException) {
                                Log.e("DEV", "Video file creation failed", ex)
                            }
                            if (videoFile != null) {
                                cam_file_data = "file:" + videoFile.absolutePath
                                takeVideoIntent.putExtra(
                                    MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(videoFile)
                                )
                            } else {
                                cam_file_data = null
                                takeVideoIntent = null
                            }
                        }
                    }
                    val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    contentSelectionIntent.type = file_type
                    if (true) {
                        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    }
                    val intentArray: Array<Intent?>
                    intentArray = if (takePictureIntent != null && takeVideoIntent != null) {
                        arrayOf(takePictureIntent, takeVideoIntent)
                    } else takePictureIntent?.let { arrayOf(it) }
                        ?: (takeVideoIntent?.let { arrayOf(it) } ?: arrayOfNulls(0))
                    val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File chooser")
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                    startActivityForResult(
                        chooserIntent, file_req_code
                    )
                    true
                } else {
                    false
                }
            }
            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                callback.onCustomViewHidden()
            }
            override fun onHideCustomView() {
                super.onHideCustomView()
            }
        })
        test?.setWebViewClient(object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                Log.d(" TEST", errorCode.toString())
                overridePendingTransition(0, 0)
            }

            @TargetApi(23)
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                onReceivedError(
                    view,
                    error.errorCode,
                    error.description.toString(),
                    request.url.toString()
                )
            }
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url)
                    if (AppSettings.isFirstOpen) {
                        AppSettings.isFirstOpen = false
                        AppSettings.linkForOpen = url
                        DataManager.saveData(activityContext, AppSettings.isFirstOpen, AppSettings.linkForOpen)
                    }
                    return true
                }
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (e: Exception) {

                }
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                CookieSyncManager.getInstance().sync()
                test?.visibility = View.VISIBLE

                super.onPageFinished(view, url)
            }
        })

        test?.settings?.javaScriptEnabled = true

        test?.requestFocus(View.FOCUS_DOWN or View.FOCUS_UP)
        test?.settings?.lightTouchEnabled = true
        test?.settings?.allowContentAccess = true
        test?.settings?.domStorageEnabled = true
        test?.settings?.javaScriptCanOpenWindowsAutomatically = true

        test?.settings?.setSupportMultipleWindows(false)
        test?.setOnTouchListener(View.OnTouchListener { v: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP -> if (!v.hasFocus()) {
                    v.requestFocus()
                }
            }
            false
        })
        test?.settings?.builtInZoomControls = true
        test?.settings?.useWideViewPort = true
        test?.settings?.builtInZoomControls = true
        test?.settings?.displayZoomControls = false
        test?.settings?.allowFileAccess = true
        if (savedInstanceState != null) {
            test?.restoreState(savedInstanceState)
            test?.setVisibility(View.VISIBLE)
        } else {
            test?.loadUrl(intent.getStringExtra("link").toString())

        }

        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(test, true)
        } else CookieManager.getInstance().setAcceptCookie(true)


    }


    override fun onBackPressed() {
        if (test!!.canGoBack()) test!!.goBack() else super.onBackPressed()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (Build.VERSION.SDK_INT >= 21) {

            if (resultCode == RESULT_CANCELED) {
                if (requestCode == file_req_code) {
                    file_path!!.onReceiveValue(null)
                    return
                }
            }


            if (resultCode == RESULT_OK) {
                if (requestCode == file_req_code) {
                    if (null == file_path) {
                        return
                    }
                    var clipData: ClipData?
                    var stringData: String?
                    try {
                        clipData = intent!!.clipData
                        stringData = intent.dataString
                    } catch (e: Exception) {
                        clipData = null
                        stringData = null
                    }
                    if (clipData == null && stringData == null && cam_file_data != null) {
                        results = arrayOf(Uri.parse(cam_file_data))
                    } else {
                        if (clipData != null) {
                            for (i in 0 until clipData.itemCount) {
                                results!![i] = clipData.getItemAt(i).uri
                            }
                        } else {
                            results = arrayOf(Uri.parse(stringData))
                        }
                    }
                }
            }
            file_path!!.onReceiveValue(results)
            file_path = null
        } else {
            if (requestCode == file_req_code) {
                if (null == file_data) return
                val result = if (intent == null || resultCode != RESULT_OK) null else intent.data
                file_data?.onReceiveValue(result)
                file_data = null
            }
        }
    }

    fun file_permission(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                1
            )
            true
        } else {
            true
        }
    }

    @Throws(IOException::class)
    private fun create_image(): File? {
        @SuppressLint("SimpleDateFormat") val timeStamp =
            SimpleDateFormat("yyMMdd_HHmm").format(
                Date()
            )
        val imageFileName = "img_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    @Throws(IOException::class)
    private fun create_video(): File? {
        @SuppressLint("SimpleDateFormat") val file_name =
            SimpleDateFormat("yy_mm_ss").format(Date())
        val new_name = "file_" + file_name + "_"
        val sd_directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(new_name, ".3gp", sd_directory)
    }
}