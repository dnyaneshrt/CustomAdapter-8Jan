package com.tech.cusomadaptertest

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity() {

    val STORAGE_REQ_CODE = 11
    var listview: ListView? = null
    var imageView: ImageView? = null
    var txt_name: TextView? = null
    var txt_size: TextView? = null
    var img_delete: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listview = findViewById(R.id.listview)

        var status =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (status == PackageManager.PERMISSION_GRANTED) {
            fetchData()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), STORAGE_REQ_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            fetchData()
        } else {
            Toast.makeText(this, "user is not allowed to access", Toast.LENGTH_SHORT)
        }
    }


    private fun fetchData() {
        var path = "/storage/emulated/0/WhatsApp/Media/WhatsApp Images/"
        var f = File(path)
        if (!f.exists()) {
            var path = "/storage/SdCard0/WhatsApp/Media/WhatsApp Images/"
            var f = File(path)
        }
        var listfiles = f.listFiles()

        //create custom Adapter
        var custAdapter = object : BaseAdapter() {
            override fun getCount(): Int {
                return listfiles.size
            }

            override fun getItem(position: Int): Any {
                return 0
            }

            override fun getItemId(position: Int): Long {
                return 0
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                var inflater = LayoutInflater.from(this@MainActivity)
                var view = inflater.inflate(R.layout.my_view, null)

                imageView = view.findViewById(R.id.imageview)

                txt_name = view.findViewById(R.id.txt_name)

                txt_size = view.findViewById(R.id.txt_size)

                img_delete = view.findViewById(R.id.img_delete)


                var files = listfiles[position]

                //to set the image to imageview
                var bitmap = BitmapFactory.decodeFile(files.path)
                var comressed_bmp = ThumbnailUtils.extractThumbnail(bitmap, 100, 100)
                imageView?.setImageBitmap(comressed_bmp)

                txt_name?.text = files.name
                txt_size?.text = files.length().toString()

                img_delete?.setOnClickListener {

                    var builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("deleting image..")
                    builder.setIcon(R.drawable.ic_baseline_delete_24)
                    builder.setMessage("r u sure want to delete iamge permannatly??")

                    var listener =
                        DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                            if (i == DialogInterface.BUTTON_POSITIVE) {
                                //logic to delete image permamantly
                                if (files.delete()) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "image deleted succesfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    fetchData()
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "unable to delete ",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }


                            } else if (i == DialogInterface.BUTTON_NEGATIVE) {
                                dialogInterface.dismiss()
                            } else if (i == DialogInterface.BUTTON_NEUTRAL) {
                                dialogInterface.dismiss()
                            }
                        }

                    builder.setPositiveButton("yes", listener)
                    builder.setNegativeButton("no", listener)
                    builder.setNeutralButton("cancel", listener)
                    builder.show()

                }

                return view
            }


        }

        listview?.adapter = custAdapter
    }

}