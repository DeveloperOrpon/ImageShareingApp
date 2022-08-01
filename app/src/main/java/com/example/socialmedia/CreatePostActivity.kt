package com.example.socialmedia

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.socialmedia.model.Post
import com.example.socialmedia.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_post.*
const val REQUEST_CODE=1122;
const val TAG2="CreatePostActivity";
class CreatePostActivity : AppCompatActivity() {
    private var photoUri: Uri? =null
    lateinit var signedInUser: User
    lateinit var db: FirebaseFirestore
    lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        storageReference=FirebaseStorage.getInstance().reference
        imageChooseBtn.setOnClickListener {
            val imagePickerIntent=Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type="image/*"
            if (imagePickerIntent.resolveActivity(packageManager)!=null){
                startActivityForResult(imagePickerIntent,REQUEST_CODE)
            }
        }
        //Find Current user Right now for profile
        db= FirebaseFirestore.getInstance()
        db.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { snapshot ->
                signedInUser = snapshot.toObject(User::class.java)!!
                Log.i(TAG, "signed in user: $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Failure fetching signed in user", exception)
            }
        //when click submit button
        SubmitPostId.setOnClickListener{
            handleSubmit()
        }
    }

    private fun handleSubmit() {
        if (photoUri==null){
            Toast.makeText(this,"No Photo Selected",Toast.LENGTH_SHORT).show()
            return
        }
        if (uploadDiscId.text.isBlank()){
            Toast.makeText(this,"No Photo Title",Toast.LENGTH_SHORT).show()
            return
        }
        if (signedInUser==null){
            Toast.makeText(this,"NO User SignIn Now",Toast.LENGTH_SHORT).show()
            return
        }
        SubmitPostId.isEnabled=false
        //Upload image to firebase storage
        val photoRef=storageReference.child("image/${System.currentTimeMillis()}_photo.jpg")
        val uploadImageUri= photoUri as Uri
        photoRef.putFile(uploadImageUri)
            .continueWithTask { uploadTask->
                // upload image retrieve image url
                Log.i(TAG2,"Upload photo ${uploadTask.result.bytesTransferred}")
                photoRef.downloadUrl
            }.continueWithTask{ downloadtask->
                // create a post object with data and image
                val post=Post(
                    uploadDiscId.text.toString(),
                    downloadtask.result.toString(),
                    System.currentTimeMillis(),
                    signedInUser)
                db.collection("posts").add(post)
            }.addOnCompleteListener { postcreateTask->
                SubmitPostId.isEnabled=true
                if (!postcreateTask.isSuccessful){
                    Log.i(TAG2,"Upload Error ${postcreateTask.exception?.message}")
                    Toast.makeText(this,"Failed to Upload Post",Toast.LENGTH_SHORT).show()
                }
                uploadImage.setImageResource(0)
                uploadDiscId.text.clear()
                val profileIntent=Intent(this,ProfileActivity::class.java)
                profileIntent.putExtra(EXTRA_USERNAME,signedInUser.userName)
                startActivity(profileIntent)
                finish()

             }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode== REQUEST_CODE){
            if (resultCode==Activity.RESULT_OK){
                photoUri=data?.data
                uploadImage.setImageURI(photoUri)
                Log.i(TAG2,"Image Uri $photoUri")
            }
            else{
                Toast.makeText(this,"Please Select A Photo",Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}