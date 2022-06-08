package com.example.glucohealth.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.glucohealth.R
import com.example.glucohealth.databinding.FragmentCameraXBinding
import com.example.glucohealth.helper.ObjectDetectionHelper
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import java.util.concurrent.Executors

class CameraXFragment : Fragment() {

    private var _binding: FragmentCameraXBinding? = null
    private val binding get() = _binding!!

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private lateinit var bitmapBuffer: Bitmap

    private var sudutRotasi = 0
    private val tfImageBuffer = TensorImage(DataType.UINT8)

    private val tfImageProcessor by lazy {
        val cropSize = minOf(bitmapBuffer.width, bitmapBuffer.height)
        ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(
                tfInputSize.height, tfInputSize.width, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(Rot90Op(sudutRotasi / 90))
            .add(NormalizeOp(0f, 1f))
            .build()
    }

    private val tflite by lazy {
        Interpreter(
            FileUtil.loadMappedFile(layoutInflater.context, MODEL_PATH),
            Interpreter.Options().addDelegate(NnApiDelegate()))
    }

    private val detector by lazy {
        ObjectDetectionHelper(tflite, FileUtil.loadLabels(layoutInflater.context, LABELS_PATH))
    }

    private val tfInputSize by lazy {
        val inputIndex = 0
        val inputShape = tflite.getInputTensor(inputIndex).shape()
        Size(inputShape[2], inputShape[1])
    }

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraXBinding.inflate(inflater, container, false)

        binding.switchCamera.setOnClickListener {
            cameraSelector = if(cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }else{
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }

        (activity as AppCompatActivity?)?.supportActionBar?.run {
            hide()
            setShowHideAnimationEnabled(false)
        }
        binding.rbSendok.isFocusable = false
        binding.rbSendok.rating = 2.2f

        binding.tvProduct.isSelected = true
        binding.viewHasil.visibility = View.VISIBLE

        binding.viewFinder.post { startCamera() }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        binding.viewHasil.visibility = View.GONE
        _binding = null
    }

    override fun onResume() {
        binding.viewFinder.post { startCamera() }
        super.onResume()
    }

    private fun scanProduct(cameraProvider: ProcessCameraProvider){
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor, { image ->
                if (!::bitmapBuffer.isInitialized) {
                    sudutRotasi = image.imageInfo.rotationDegrees
                    bitmapBuffer = Bitmap.createBitmap(
                        image.width, image.height, Bitmap.Config.ARGB_8888
                    )
                }

                val tfImage = tfImageProcessor.process(tfImageBuffer.apply { load(bitmapBuffer) })

                val predictions = detector.predict(tfImage)

                Log.e("LUAR FUNGSI", predictions.maxByOrNull { it.label }.toString())

                hasilPrediksi(predictions.maxByOrNull { it.score })
            })

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
                preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            } catch (exc: Exception) {
                Toast.makeText(
                    layoutInflater.context,
                    getString(R.string.failtoopencamera),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(layoutInflater.context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            scanProduct(cameraProvider)
        }, ContextCompat.getMainExecutor(layoutInflater.context))
    }

    private fun hasilPrediksi(
        prediksi: ObjectDetectionHelper.ObjectPrediction?
    ){
        if (prediksi == null || prediksi.score < ACCURACY_THRESHOLD){
            binding.viewHasil.visibility = View.VISIBLE
        }else{
            binding.viewHasil.visibility = View.VISIBLE
            binding.tvProduct.text = prediksi.label
        }
    }

    companion object {
        private const val ACCURACY_THRESHOLD = 0.5f
        private const val MODEL_PATH = "coco_ssd_mobilenet_v1_1.0_quant.tflite"
        private const val LABELS_PATH = "coco_ssd_mobilenet_v1_1.0_labels.txt"
    }
}