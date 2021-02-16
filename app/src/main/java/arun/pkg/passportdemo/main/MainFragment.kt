package arun.pkg.passportdemo.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import arun.pkg.passportdemo.BuildConfig
import arun.pkg.passportdemo.databinding.MainFragmentBinding
import arun.pkg.passportdemo.main.util.FileUriUtils
import com.theartofdev.edmodo.cropper.CropImage
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val REQUEST_CHOOSE_IMAGE = 1001
private const val REQUEST_CAMERA_IMAGE = 1002

class MainFragment : BaseFragment() {
    private val viewModel: MainViewModel by ViewModelDelegate()
    private var disposable: CompositeDisposable = CompositeDisposable()

    private var cameraImageFile: File? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = MainFragmentBinding.inflate(inflater)
            .also {
                it.viewModel = viewModel
            }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkPermission()
        disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun handleEvent(event: MainViewModel.Event) =
        when (event) {
            is MainViewModel.Event.OnClickGalleryButton -> {
                // startChooseImageIntentForResult()
                CropImage.activity()
                    .setAllowFlipping(false)
                    .setAllowRotation(true)
                    .start(context!!, this)
            }
            is MainViewModel.Event.OnClickCameraButton -> {
                startCameraIntentForResult()
            }
        }

    private fun startChooseImageIntentForResult() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CHOOSE_IMAGE)
    }

    private fun startCameraIntentForResult() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context!!.packageManager) != null) {
            // Create the File where the photo should go
            try {
                cameraImageFile = getOutputMediaFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            cameraImageFile?.let {
                val photoURI = FileProvider.getUriForFile(
                    context!!,
                    BuildConfig.APPLICATION_ID + ".provider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_CAMERA_IMAGE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val imageUri = result.uri
                val path = getOutputMediaFile()
                imageUri.let {
                    val sourceFile = File(FileUriUtils.getRealPath(context!!, it))
                    path?.let { destPath ->
                        viewModel.copyMediaFile(sourceFile, destPath)
//                    viewModel.runTextExtraction(destPath)
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(
                    context, "There was some error : ${result.error.message}", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
//            val path = getOutputMediaFile()
//            data!!.data?.let {
//                val sourceFile = File(FileUriUtils.getRealPath(context!!, it))
//                path?.let { destPath ->
//                    viewModel.copyMediaFile(sourceFile, destPath)
////                    viewModel.runTextExtraction(destPath)
//                }
//            }
//        } else if (requestCode == REQUEST_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {
//            viewModel.runFirebaseML(cameraImageFile!!)
//        }
//    }

    /** Create a File for saving an image or video  */
    private fun getOutputMediaFile(): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        val mediaStorageDir = activity?.cacheDir

        mediaStorageDir?.let {
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                }
            }
            // Create a media file name
            val timeStamp: String = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
            val mediaFile: File
            val mImageName = "Posti_$timeStamp.jpg"
            mediaFile = File(mediaStorageDir.path + File.separator + mImageName)
            return mediaFile
        }
        return null
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                121
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
