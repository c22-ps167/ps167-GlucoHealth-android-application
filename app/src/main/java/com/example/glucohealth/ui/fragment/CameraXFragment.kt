package com.example.glucohealth.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface.ROTATION_0
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
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.log

class CameraXFragment : Fragment() {

    private var _binding: FragmentCameraXBinding? = null
    private val binding get() = _binding!!

    private lateinit var  objectDetector: ObjectDetector
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var cameraExecutor: ExecutorService

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraXBinding.inflate(inflater, container, false)
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraProviderFuture = ProcessCameraProvider.getInstance(layoutInflater.context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindCamera(cameraProvider)
        }, ContextCompat.getMainExecutor(layoutInflater.context))

        val localModel = LocalModel.Builder()
            .setAssetFilePath(MODEL_PATH)
            .build()

        val customObjectDetectorOptions = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableClassification()
            .setClassificationConfidenceThreshold(ACCURACY_THRESHOLD)
            .setMaxPerObjectLabelCount(1)
            .build()

        objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)

        (activity as AppCompatActivity?)?.supportActionBar?.run {
            hide()
            setShowHideAnimationEnabled(false)
        }

        val konsumsiGula = 20.0
        val progressKonsumsiGula = (konsumsiGula / 50.0) * 100.0
        Toast.makeText(layoutInflater.context, progressKonsumsiGula.toString(), Toast.LENGTH_SHORT).show()

        binding.pgGulaharian.progress = progressKonsumsiGula.toInt()
        binding.pgTvProgress.text = getString(R.string.lblprogressharian).format(konsumsiGula.toString())

        binding.tvProduct.isSelected = true
        binding.viewHasil.visibility = View.GONE

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCamera(cameraProvider: ProcessCameraProvider){
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(ROTATION_0)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(layoutInflater.context),{imageProxy ->
                val sudutRotasi = imageProxy.imageInfo.rotationDegrees
                val image = imageProxy.image
                if (image != null){
                    val inputImage = InputImage.fromMediaImage(image,sudutRotasi)
                    objectDetector.process(inputImage).addOnFailureListener{
                        imageProxy.close()
                        binding.viewHasil.visibility = View.GONE
                    }.addOnSuccessListener { objek ->
                        for(it in objek){
                            if(it.labels.firstOrNull() != null) {
                                binding.viewHasil.visibility = View.VISIBLE
                                binding.tvProduct.text = it.labels.first().text
                            }else{
                                binding.viewHasil.visibility = View.GONE
                            }
                        }
                        imageProxy.close()
                    }
                }
            })

            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    imageAnalysis,
                    preview
                )
            }catch (ex: Exception){
                Toast.makeText(layoutInflater.context, "fail to open camera", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val ACCURACY_THRESHOLD = 0.5f
        private const val MODEL_PATH = "converted_models.tflite"
    }

}