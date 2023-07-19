package com.sample.firebaseapp

import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.sample.firebaseapp.databinding.ActivityProfilePageBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.ui.profile.ProfilePageViewModel

class ProfilePageActivity : AppCompatActivity() {
    private val viewModel: ProfilePageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.binding = ActivityProfilePageBinding.inflate(layoutInflater)

        setContentView(viewModel.binding.root)
        viewModel.binding.backButton.setBackgroundColor(Color.TRANSPARENT)
        viewModel.username = intent.getStringExtra("username").toString()
        viewModel.isOwner = intent.getBooleanExtra("isOwner", false)
        viewModel.binding.usernameOnProfileTextView.text = viewModel.username
        viewModel.storageReference =
            FirebaseStorage.getInstance().getReference("images/${viewModel.username}")
        viewModel.usersRef = FirebaseDatabase.getInstance().getReference("Users")
        FirebaseHelper.getCurrentUserModel {
            viewModel.userModel = it

            val name = viewModel.userModel?.name + " " + (viewModel.userModel?.surName)
            viewModel.binding.nameOnprofileTextview.text = name

            Log.d("denemeuser3", viewModel.userModel?.userId.toString())

        }
        val userName = viewModel.username
        if (userName != null) {
            viewModel.searchUserByName(userName)
        }
        viewModel.binding.backButton.setOnClickListener() {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isOwner) {
            editProfilePhoto()
            viewModel.storageReference =
                FirebaseStorage.getInstance().getReference("images/${viewModel.username}")
            viewModel.storageReference.putFile(viewModel.imageUri).addOnSuccessListener {
                viewModel.loadProfileImage()
            }

        } else {
            viewModel.binding.editProfileimageButton.isVisible = false
        }

        viewModel.storageReference.downloadUrl.addOnSuccessListener() { uri ->
            Glide
                .with(viewModel.context)
                .load(uri)
                .centerCrop()
                .into(viewModel.binding.profileImage);
            Log.d("ProfilePageTest", uri.toString())
        }.addOnFailureListener { exception ->
            Log.e("ProfilePageTest", "Resim indirme hatası: $exception")
        }
    }


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.imageUri = it
                viewModel.binding.profileImage.setImageURI(it)
            }
        }

    fun editProfilePhoto() {
        viewModel.binding.editProfileimageButton.setOnClickListener() {
            galleryLauncher.launch("image/*")
        }
    }

}
