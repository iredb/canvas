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
    private lateinit var colorButtons: List<Button>
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

        colorButtons = listOf(
            findViewById(R.id.btnBlack),
            findViewById(R.id.btnRed),
            findViewById(R.id.btnGreen),
            findViewById(R.id.btnBlue)
        )
        colorButtons[0].setOnClickListener { drawingView.setColor(android.graphics.Color.BLACK) }
        colorButtons[1].setOnClickListener { drawingView.setColor(android.graphics.Color.RED) }
        colorButtons[2].setOnClickListener { drawingView.setColor(android.graphics.Color.GREEN) }
        colorButtons[3].setOnClickListener { drawingView.setColor(android.graphics.Color.BLUE) }

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
            val outputStream = contentResolver.openOutputStream(uri!!)!!
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream?.close()
        }

        btnClear.setOnClickListener {
            drawingView.clearCanvas()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageCode && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            drawingView.setBackgroundBitmap(bitmap)
        }
    }
}
