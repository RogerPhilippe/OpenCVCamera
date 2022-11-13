package br.com.phs.imagepro

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.phs.imagepro.databinding.ActivityMainBinding
import br.com.phs.imagepro.ui.camera.CameraActivity
import org.opencv.android.OpenCVLoader

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CAMERA = 0
    }

    private lateinit var binding: ActivityMainBinding
    private var cameraPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(this.binding.root)

        val msg = if (OpenCVLoader.initDebug()) "OpenCV is Initialized!" else "Error initialize OpenCV"

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

        this.setupListeners()

    }

    private fun setupListeners() {

        binding.cameraBtn.setOnClickListener {

            if (!cameraPermission) {
                Toast.makeText(
                    this,
                    "Você precisa dar permissão para utilizar a câmera!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val intent = Intent(this, CameraActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startCameraResult.launch(intent)
        }

    }

    private val startCameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), PERMISSION_REQUEST_CAMERA)
        } else {
            this.cameraPermission = true
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        this.cameraPermission =
            (requestCode == PERMISSION_REQUEST_CAMERA && grantResults.any { it == PackageManager.PERMISSION_GRANTED })
    }

}