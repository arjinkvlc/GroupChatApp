package com.sample.firebaseapp.ui.profile

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.sample.firebaseapp.databinding.ActivityProfilePageBinding
import com.sample.firebaseapp.model.UserModel
import kotlin.properties.Delegates

class ProfilePageViewModel(application: Application) : AndroidViewModel(application) {
    val context = getApplication<Application>()
    lateinit var binding: ActivityProfilePageBinding
    var imageUri: Uri = Uri.EMPTY
    lateinit var storageReference: StorageReference;
    lateinit var usersRef: DatabaseReference;
    var username: String? = null
    var isOwner by Delegates.notNull<Boolean>()
    var userModel: UserModel? = null


    fun loadProfileImage() {
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context)
                .load(uri)
                .centerCrop()
                .into(binding.profileImage)
            Log.d("ProfilePageTest", uri.toString())
        }.addOnFailureListener { exception ->
            Log.e("ProfilePageTest", "Resim indirme hatası: $exception")
        }
    }

    fun searchUserByName(name: String) {
        usersRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(object :
            ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UserModel::class.java)
                    val ad = user?.name
                    val soyad = user?.surName
                    binding.nameOnprofileTextview.text = ("$ad $soyad")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ProfilePageTest", "Search failed")
            }
        })


    }

}