package br.com.phs.imagepro.ui.camera

import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.phs.imagepro.databinding.ActivityCameraBinding
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat

class CameraActivity : AppCompatActivity(), CvCameraViewListener2 {

    companion object {
        private const val TAG = "CameraActivity"
    }

    private lateinit var binding: ActivityCameraBinding

    private lateinit var rgba: Mat
    private lateinit var grey: Mat

    private val loaderCallBack = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                SUCCESS -> {
                    Log.i("OpenCV Callback", "OpenCV is loaded!")
                    this@CameraActivity.binding.openCvCameraView.setCameraPermissionGranted()
                    this@CameraActivity.binding.openCvCameraView.enableView()
                    this@CameraActivity.setupView()
                }
                else -> {
                    Toast.makeText(this@CameraActivity, "OpenCV isn't loaded!", Toast.LENGTH_SHORT).show()
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

    }

    private fun setupView() {

        this.binding.openCvCameraView.visibility = SurfaceView.VISIBLE
        this.binding.openCvCameraView.setCvCameraViewListener(this)

    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        this.rgba = Mat(height, width, CvType.CV_8UC4)
        this.grey = Mat(height, width, CvType.CV_8UC1)
    }

    override fun onCameraViewStopped() {
        this.rgba.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        this.rgba = inputFrame.rgba()
        this.grey = inputFrame.gray()
        return rgba
    }

    override fun onResume() {
        super.onResume()
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "Opencv initialization done!")
            loaderCallBack.onManagerConnected(BaseLoaderCallback.SUCCESS)
        } else {
            Log.d(TAG, "Opencv not initialization!")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, this.loaderCallBack)
        }
    }

    override fun onPause() {
        super.onPause()
        this.binding.openCvCameraView.disableView()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.binding.openCvCameraView.disableView()
    }

}