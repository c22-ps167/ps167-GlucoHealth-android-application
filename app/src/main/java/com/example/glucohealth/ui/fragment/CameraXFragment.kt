package com.example.glucohealth.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.glucohealth.R
import com.example.glucohealth.databinding.FragmentCameraXBinding
import com.example.glucohealth.helper.ViewModelFactory
import com.example.glucohealth.response.NutritionFact
import com.example.glucohealth.ui.activity.ProductDetailActivity
import com.example.glucohealth.utils.createFile
import com.example.glucohealth.utils.reduceFileImage
import com.example.glucohealth.viewmodels.ProductViewModel
import com.example.glucohealth.viewmodels.SugarViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class CameraXFragment : Fragment() {

    private var _binding: FragmentCameraXBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModelApi: ProductViewModel
    private lateinit var viewModel : SugarViewModel


    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var imageCapture: ImageCapture? = null
    private var takePicture = false
    private var sugarConsum = 0

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraXBinding.inflate(inflater, container, false)

        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        (activity as AppCompatActivity?)?.supportActionBar?.run {
            hide()
            setShowHideAnimationEnabled(false)
        }

        viewModelApi = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ProductViewModel::class.java]
        viewModel = obtainViewModel(requireActivity())

        startCamera()
        binding.wrapperHasil.visibility = View.GONE
        binding.captureImage.setOnClickListener {
            takePicture = !takePicture
            takePhoto()
        }
        binding.btnClose.setOnClickListener {
            binding.wrapperHasil.visibility = View.GONE
        }

        binding.tvProduct.isSelected = true

        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formatedTime = dateFormat.format(currentDate)
        viewModel.getAllProduct(formatedTime).observe(viewLifecycleOwner){product->
            product.forEach {
                sugarConsum += it.sugar
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(layoutInflater.context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .build()
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    layoutInflater.context,
                    getString(R.string.failtoopencamera),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(layoutInflater.context))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createFile(activity!!.application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(layoutInflater.context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        layoutInflater.context,
                        getString(R.string.failTakePicture),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    sendImage(photoFile)
                }
            }
        )
    }

    private fun sendImage(photo: File){
        val file = reduceFileImage(photo)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestImageFile)
        viewModelApi.getPrediction(imageMultipart)
        viewModelApi.predict.removeObservers(viewLifecycleOwner)
        viewModelApi.predict.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()){
                reviewHasil(it)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun reviewHasil(productId: String){
        viewModelApi.getProductDetail(productId)
        viewModelApi.detailProduct.observe(viewLifecycleOwner){ product->
            binding.tvProduct.text = product.name
            val sugar = product.nutritionFact.sugar ?: 0
            binding.lblTambahan.text = getString(R.string.tambah).format(sugar)
            binding.pgTvProgress.text = getString(R.string.lblprogressharian).format(sugar + sugarConsum)
            setProgress(sugar + sugarConsum)
            Glide.with(layoutInflater.context).load(product.url).into(binding.imgProduct)
            binding.viewHasil.setOnClickListener{ _->
                productDetail(product.name, product.url, product.nutritionFact, productId)
            }
        }
        binding.wrapperHasil.visibility = View.VISIBLE
    }

    private fun setProgress(sugar: Int){
        val konsumsi = ((sugar / 50.0) * 100.0).toInt()
        binding.pgGulaharian.progress = konsumsi
    }

    private fun productDetail(productName: String, imgUrl: String, Nutrition: NutritionFact, productId: String){
        val descriptionIntent = Intent(layoutInflater.context, ProductDetailActivity::class.java)
            .putExtra(ProductDetailActivity.EXTRA_PRODUCTNAME, productName)
            .putExtra(ProductDetailActivity.EXTRA_IMGURL, imgUrl)
            .putExtra(ProductDetailActivity.EXTRA_CALORIES, Nutrition.calories)
            .putExtra(ProductDetailActivity.EXTRA_PROTEIN,Nutrition.protein)
            .putExtra(ProductDetailActivity.EXTRA_FAT, Nutrition.saturatedFat)
            .putExtra(ProductDetailActivity.EXTRA_SERVINGSIZE, Nutrition.servingSize)
            .putExtra(ProductDetailActivity.EXTRA_SODIUM, Nutrition.sodium)
            .putExtra(ProductDetailActivity.EXTRA_SUGAR, Nutrition.sugar)
            .putExtra(ProductDetailActivity.EXTRA_CARBO, Nutrition.totalCarbohydrate)
            .putExtra(ProductDetailActivity.EXTRA_PRODUCTID, productId)
        startActivity(descriptionIntent)
    }

    private fun obtainViewModel(activity: FragmentActivity): SugarViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[SugarViewModel::class.java]
    }
}