package elsaghier.com.flashlightapp

import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.os.Build
import android.hardware.camera2.CameraAccessException
import android.view.View
import android.view.View.OnClickListener


class MainActivity : AppCompatActivity() {

    private var objCameraManager: CameraManager? = null
    private var mCameraId: String? = null
    private var ivOnOFF: ImageView? = null
    private var objMediaPlayer: MediaPlayer? = null

    private var isTorchOn: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ivOnOFF = findViewById(R.id.ivOnOFF)
        isTorchOn = false
        val isFlashAvailable =
            applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        if (isFlashAvailable) {
            val alert = AlertDialog.Builder(this).create()
            alert.setTitle(getString(R.string.app_name))
            alert.setMessage(getString(R.string.msg_error))
            alert.setButton(
                DialogInterface.BUTTON_POSITIVE, getString(R.string.lbl_ok),
                { dialog, which -> finish() })
            alert.show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            objCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                mCameraId = objCameraManager!!.getCameraIdList()[0]
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }


        ivOnOFF!!.setOnClickListener(OnClickListener {
            fun onClick(v: View) {
                try {
                    isTorchOn = if (isTorchOn!!) {
                        turnOffLight()
                        false
                    } else {
                        turnOnLight()
                        true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })


    }

    /**
     * Method for turning light OFF
     */
    fun turnOffLight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                objCameraManager!!.setTorchMode(mCameraId!!, false)
                playOnOffSound()
                ivOnOFF!!.setImageResource(R.drawable.off)

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun playOnOffSound() {
        objMediaPlayer = MediaPlayer.create(this, R.raw.flash_sound)
        objMediaPlayer!!.setOnCompletionListener({ mp -> mp.release() })
        objMediaPlayer!!.start()
    }


    /**
     * Method for turning light ON
     */
    fun turnOnLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                objCameraManager!!.setTorchMode(mCameraId, true)
                playOnOffSound()
                ivOnOFF!!.setImageResource(R.drawable.on)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onResume() {
        super.onResume()
        if (isTorchOn!!) {
            turnOnLight()
        }
    }

    override fun onStop() {
        super.onStop()
        if (isTorchOn!!) {
            turnOffLight()
        }
    }

    override fun onPause() {
        super.onPause()

        if (isTorchOn!!) {
            turnOffLight()
        }

    }
}
