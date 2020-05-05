package com.ssam.kochat_mungae.ui.notifications

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.ssam.kochat_mungae.R
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File


class ProfileFragment : Fragment() {
    val TAG = "ProfileFragment"
    val REQUEST_IMAGE_CODE = 1001
    val REQUEST_EXTERNAL_STORAGE_PERMISSION = 1002
    private var mStorageRef: StorageReference? = null
    var stEmail : String ?= null

    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
//        val textView: TextView = root.findViewById(R.id.tvProfile)
//        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        val sharedPref = activity?.getSharedPreferences("shared", Context.MODE_PRIVATE)
        stEmail = sharedPref!!.getString("email", "")

        mStorageRef = FirebaseStorage.getInstance().getReference()
        Log.d(TAG, "stEmail: $stEmail")



        //개발자 사이트에서 앱에 권한요청 검색함,  상수는 바꾸어 줌,  this.requireActivity(=root.context) 도
        if (ContextCompat.checkSelfPermission(root.context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this.requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_EXTERNAL_STORAGE_PERMISSION)
            }
        } else {

        }

        var ivUser = root.findViewById<ImageView>(R.id.imageProfile)
        ivUser.setOnClickListener {
            Toast.makeText(it.context, "보임", Toast.LENGTH_SHORT).show()
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_CODE)
        }

        val riversRef: StorageReference = mStorageRef!!.child("users").child(stEmail.toString()).child("profile.jpg")
        val localFile : File = File.createTempFile("images", "jpg")
        riversRef.getFile(localFile).addOnSuccessListener{
            val bitmap : Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            ivUser.setImageBitmap(bitmap)
        }.addOnFailureListener{

        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {   //타이핑함
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CODE){
            val image : Uri? = data!!.getData()
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, image)
                imageProfile.setImageBitmap(bitmap)
            } catch (e: Exception){
                e.printStackTrace()
            }

            //val file = Uri.fromFile(File("path/to/images/profile.jpg"))
            val riversRef: StorageReference = mStorageRef!!.child("users").child(stEmail.toString()).child("profile.jpg")
            riversRef.putFile(image!!)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    // Get a URL to the uploaded content
                    //val downloadUrl: Uri = taskSnapshot.getDownloadUrl()
                    Toast.makeText(context, taskSnapshot.toString(), Toast.LENGTH_SHORT).show()
                })
                .addOnFailureListener(OnFailureListener {
                    // Handle unsuccessful uploads
                    // ...
                })

        }
    }

    //타이핑함
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
