package com.example.photoai

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.system.Os.bind
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.photoai.databinding.ActivityMainBinding
import com.google.android.material.chip.Chip
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
             private var isText = false
           override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
     binding= ActivityMainBinding.inflate(layoutInflater)

       setContentView(binding.root)
                 switch1.setOnClickListener {
                  isText = switch1.isChecked

               }
         binding.fab.setOnClickListener {
               pickImage()
        }
    }


           override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if(resultCode==Activity.RESULT_OK)
              {
                        when(requestCode){
             IMAGE_PICK_CODE->{
                                   val bitmap=getImageFromData(data)
                 bitmap.apply { imageView.setImageBitmap(this)
                  if(!isText){
               if (bitmap != null) {
                          }
                 }
            if(isText){
              if (bitmap != null) {
             starttextRecognizing(bitmap)
                   }
                                }
               }
                    }
              }
               }
           super.onActivityResult(requestCode, resultCode, data)
                        }
                 private fun processImageTagging(bitmap: Bitmap) {
           val visionImg = FirebaseVisionImage.fromBitmap(bitmap)
         FirebaseVision.getInstance().onDeviceImageLabeler.processImage(visionImg)
                                .addOnSuccessListener { tags ->
                binding.chipGroup.removeAllViews()
                  tags.sortedByDescending { it.confidence }.map {
                    Chip(
                     this,
                          null,
              R.style.Widget_MaterialComponents_Chip_Choice
                    ).apply { text = it.text }
                }
                    .forEach {
                        binding.chipGroup.addView(it)
                    }

         }.addOnFailureListener{ ex ->
            Log.wtf("Log J", ex)
        }
    }


    private fun getImageFromData(data: Intent?): Bitmap? {
        val selectedImage = data?.data
        return MediaStore.Images.Media.getBitmap(

            this.contentResolver, selectedImage
        )

    }

    private fun starttextRecognizing(bitmap: Bitmap) {
        if (binding.imageView.drawable != null) {
            val image=FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
                detector.processImage(image).addOnSuccessListener { firebaseVisionText ->
                processTextBlock(firebaseVisionText)
            }.addOnFailureListener {
                binding.textView.text = "Failed"
            }
        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
        }

    }

    private fun processTextBlock(result: FirebaseVisionText?) {
        binding.chipGroup.removeAllViews()
        if (result != null) {
            result.textBlocks.map {
                Chip(this, null, R.style.Widget_MaterialComponents_Chip_Choice).apply {
                    text = it.text
                }
            }.forEach {
                    binding.chipGroup.addView(it)
                }
        }
    }

    private fun pickImage(){
        val intent=Intent().apply {
            action=Intent.ACTION_PICK
            type="image/*"
        }
        startActivityForResult(Intent.createChooser(intent,"select picture"),IMAGE_PICK_CODE)
    }
    companion object{
        private var IMAGE_PICK_CODE=180
    }
}