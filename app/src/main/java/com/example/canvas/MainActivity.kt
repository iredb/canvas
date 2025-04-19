package com.example.canvas

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.canvas.R
import com.example.paintapp.DrawingView
import java.io.File
import java.io.FileOutputStream

// я проверил, все работает
//　　　　　　　　　　　　　　;' ':;,,　　　　 ,;'':;,
//　　　　　　　　　　　　　;'　　 ':;,.,.,.,.,.,,,;'　　';,
//　　ー　　　　　　　　 ,:'　　　　　　　　 　::::::::､
//　_＿　　　　　　　　,:' ／ 　 　　　　＼ 　　::::::::', Have A Good day
//　　　　　二　　　　:'　 ●　　　　　 ●　 　　 ::::::::i.
//　　￣　　　　　　　i　 '''　(__人_)　　'''' 　　 ::::::::::i
//　　　　-‐　　　　　 :　 　　　　　　　　　 　::::::::i
//　　　　　　　　　　　`:,､ 　　　　　 　 　 :::::::::: /
//　　　　／　　　　　　 ,:'　　　　　　　 : ::::::::::::｀:､
//　　　　　　　　　　　 ,:'　　　　　　　　 : : ::::::::::｀:､

class MainActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var brushSizeSeekBar: SeekBar
    private val pickImageCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawingView)
        brushSizeSeekBar = findViewById(R.id.brushSizeSeekBar)
        val btnImport = findViewById<Button>(R.id.btnImport)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnClear = findViewById<Button>(R.id.btnClear)

        findViewById<Button>(R.id.btnBlack).setOnClickListener { drawingView.setColor(android.graphics.Color.BLACK) }
        findViewById<Button>(R.id.btnRed).setOnClickListener { drawingView.setColor(android.graphics.Color.RED) }
        findViewById<Button>(R.id.btnGreen).setOnClickListener { drawingView.setColor(android.graphics.Color.GREEN) }
        findViewById<Button>(R.id.btnBlue).setOnClickListener { drawingView.setColor(android.graphics.Color.BLUE) }

        brushSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                drawingView.setBrushSize(progress.toFloat())
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        btnImport.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, pickImageCode)
        }

        btnSave.setOnClickListener {
            val bitmap = drawingView.getBitmap()

            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "drawing_${System.currentTimeMillis()}.png")
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)

            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let{
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }
        }

        btnClear.setOnClickListener {
            drawingView.clearCanvas()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageCode && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->  
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    drawingView.setBackgroundBitmap(bitmap)
                }
            }
        }
    }
}
