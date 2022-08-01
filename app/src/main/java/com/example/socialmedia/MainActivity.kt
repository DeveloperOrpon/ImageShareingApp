package com.example.socialmedia

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmedia.model.Post
import com.example.socialmedia.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_post.*

const val TAG="MainActivity"
const val EXTRA_USERNAME = "EXTRA_USERNAME"
open class MainActivity : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    lateinit var posts: MutableList<Post>
    lateinit var postAdapter: PostAdapter
    lateinit var signedInUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        //FireStore to load data for All post
        var postsRef=db.collection("posts")
            .limit(20)
            .orderBy("creation_time_ms",Query.Direction.DESCENDING)
        //profile post filter start
        val userName = intent.getStringExtra(EXTRA_USERNAME)
        if (userName != null) {
            supportActionBar?.title = userName
            postsRef = postsRef.whereEqualTo("user.userName", userName)
        }
        //profile post filter end

        postsRef.addSnapshotListener{snapshot,exception->
            if (snapshot==null || exception!=null){
                Log.i(TAG,"exception when query post",exception)
                return@addSnapshotListener
            }
            posts.clear()
            val postList=snapshot.toObjects(Post::class.java)
            posts.addAll(postList)
            postAdapter.notifyDataSetChanged()
        }
        //RecyclerView create to view post
        //1.Create layout file which represent the post --Done{list_post.xml}
        //2.Create data source
        posts= mutableListOf()
        //3.Create adapter
        postAdapter=PostAdapter(this,posts) //context and data source
        //4.Bind the adapter with layout
        rvPostId.adapter=postAdapter
        rvPostId.layoutManager=LinearLayoutManager(this)

        //Create post
        createPostButton.setOnClickListener {
            var intent=Intent(this,CreatePostActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    // when profile button click in intent to profile activity
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.ProfileBtn){
            var intent=Intent(this,ProfileActivity::class.java)
            intent.putExtra(EXTRA_USERNAME, signedInUser.userName)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}