package com.example.traditionfabric.TfLite

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.traditionfabric.databinding.ActivityTensorFlowBinding
import com.example.traditionfabric.ml.ModelTflite
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer

class TensorFlow : AppCompatActivity() {
    private lateinit var binding: ActivityTensorFlowBinding
    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var tvOutput: TextView
    private val GALLERY_REQUEST_CODE = 123
    private lateinit var intValues: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTensorFlowBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        imageView = binding.imageView
        button = binding.btnCaptureImage
        tvOutput = binding.tvOutput
        val buttonLoad = binding.btnLoadImage

        intValues = IntArray(imageView.width * imageView.height)

        button.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                takePicturePreview.launch(null)
            } else {
                requestPermission.launch(android.Manifest.permission.CAMERA)
            }
        }

        buttonLoad.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                onResult.launch(intent)
            } else {
                requestPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        // to redirct user to google search for the description name
        tvOutput.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/search?q=${tvOutput.text}")
            )
            startActivity(intent)
        }

        // to download image when longPress on ImageView
        imageView.setOnClickListener {
            requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    // Request camera permission
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                takePicturePreview.launch(null)
            } else {
                Toast.makeText(this, "Izin ditolak !! Coba lagi", Toast.LENGTH_SHORT).show()
            }
        }

    // Launch camera and take picture
    private val takePicturePreview =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
                outputGenerator(bitmap)
            }
        }

    // Handle image selection from gallery
    private val onResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.i("TAG", "this is the result: ${result.data} ${result.resultCode}")
            onResultReceived(GALLERY_REQUEST_CODE, result)
        }

    private fun onResultReceived(requestCode: Int, result: ActivityResult?) {
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (result?.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        Log.i("TAG", "onResultReceived: $uri")
                        val bitmap =
                            BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                        imageView.setImageBitmap(bitmap)
                        outputGenerator(bitmap)
                    }
                } else {
                    Log.e("TAG", "onActivity: kesalahan dalam memilih gambar")
                }
            }
        }
    }


    private fun outputGenerator(bitmap: Bitmap) {
        // Deklarasi variabel model TensorFlow Lite
        val model = ModelTflite.newInstance(this)

        // Definisikan ukuran input yang diharapkan oleh model
        val modelInputSize = 224

        // Persiapkan input tensor menggunakan TensorImage
        val inputImageBuffer = TensorImage(DataType.FLOAT32)
        inputImageBuffer.load(bitmap)

        // Lakukan transformasi dan pemrosesan gambar sesuai kebutuhan model
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(modelInputSize, modelInputSize))
            .add(ResizeOp(modelInputSize, modelInputSize, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(NormalizeOp(0f, 255f))
            .build()
        val processedImageBuffer = imageProcessor.process(inputImageBuffer)

        // Menjalankan inferensi model
        var inputTensorBuffer: TensorBuffer = processedImageBuffer.tensorBuffer

        // Pastikan ukuran buffer cocok dengan ukuran yang diharapkan oleh model
        if (inputTensorBuffer.buffer.capacity() != modelInputSize * modelInputSize * 3 * 4) {
            // Lakukan penyesuaian ukuran buffer jika perlu
            val resizedInputTensorBuffer = TensorBuffer.createFixedSize(
                intArrayOf(1, modelInputSize, modelInputSize, 3),
                DataType.FLOAT32
            )
            resizedInputTensorBuffer.loadBuffer(inputTensorBuffer.buffer.rewind() as ByteBuffer)
            inputTensorBuffer = resizedInputTensorBuffer
        }

        // Menjalankan inferensi model
        val outputs = model.process(inputTensorBuffer)

        // Mendapatkan hasil dari output tensor
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        // Mendefinisikan list kelas sesuai dengan dictionary
        val classList = listOf(
            "Batik Betawi",
            "Batik Cendrawasih",
            "Batik Dayak",
            "Batik Insang",
            "Batik Lasem",
            "Batik Megamendung",
            "Batik Pala",
            "Batik Parang",
            "Batik Tambal",
            "Kain Poleng",
            "Ulos Bintang Maratur",
            "Ulos Mangiring",
            "Ulos Ragi Hidup",
            "Ulos Ragi Hotang",
            "Ulos Sadum"
        )

        // Mendapatkan indeks dengan probabilitas tertinggi
        val maxProbabilityIndex = outputFeature0.indices.maxByOrNull { outputFeature0[it] }

        // Mendapatkan nama kelas berdasarkan indeks
        val className = if (maxProbabilityIndex != null) {
            classList[maxProbabilityIndex]
        } else {
            "Unknown"
        }

        // Menampilkan hasil atau melakukan tindakan
        tvOutput.text = "Detected image: $className"

        // Menampilkan probabilitas untuk setiap kelas
        val probabilities = outputFeature0.mapIndexed { index, value -> classList[index] to value }
        val sortedProbabilities = probabilities.sortedByDescending { it.second }
        val topKProbabilities = sortedProbabilities.take(3) // Mengambil 3 probabilitas tertinggi
        for ((label, probability) in topKProbabilities) {
            Log.i("TAG", "Label: $label, Probability: $probability")
        }

        // Releases model resources if no longer used.
        model.close()
    }


    // to download image to device
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                AlertDialog.Builder(this).setTitle("Unduh gambar?")
                    .setMessage("apakah Anda ingin mengunduh gambar ini ke perangkat Anda?")
                    .setPositiveButton("Ya") { _, _ ->
                        val drawable: BitmapDrawable = imageView.drawable as BitmapDrawable
                        downloadImage(drawable.bitmap)
                    }
                    .setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                Toast.makeText(this, "izinkan untuk mengunduh gambar", Toast.LENGTH_LONG)
                    .show()
            }
        }

    // fun that takes a bitmap and store to users device
    private fun downloadImage(mBitmap: Bitmap): Uri? {
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "Gambar_kain" + System.currentTimeMillis() / 1000
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        }
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        if (uri != null) {
            contentResolver.insert(uri, contentValues)?.also {
                contentResolver.openOutputStream(it).use { outputStream ->
                    if (!mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                        throw IOException("tidak bisa nave bitmap")
                    } else {
                        Toast.makeText(applicationContext, "Simpan Gambar", Toast.LENGTH_LONG).show()
                    }
                }

                return it
            }
        }
        return null
    }


}