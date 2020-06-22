package com.github.xpenatan.maskview.example

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.xpenatan.maskview.example.databinding.ActivityPreviewBinding

class PreviewActivity : AppCompatActivity(){

    companion object {

        private const val INTENT_URI = "uri"

        fun startActivity(context: Activity, uri: Uri) {
            val intent = Intent(context,  PreviewActivity::class.java)
            intent.putExtra(INTENT_URI, uri.toString())
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview)
        val imagePath= intent.getStringExtra(INTENT_URI);
        val fileUri = Uri.parse(imagePath)
        binding.imagePreview.setImageURI(fileUri)

        binding.button.setOnClickListener { finish() }
    }
}