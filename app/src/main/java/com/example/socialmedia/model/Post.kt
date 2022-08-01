package com.example.socialmedia.model

import com.google.firebase.firestore.PropertyName

data class Post(
    var description: String = "",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl: String = "",
    @get:PropertyName("creation_time_ms") @set:PropertyName("creation_time_ms") var creationTimeMs: Long = 0,
    var user: User? = null
)

//data class Post(
//    @get:PropertyName("current_time") @set:PropertyName("current_time") var currentTime:Int=0,
//    var description: String="",
//    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl:String="",
//    val user: User? =null
//)
