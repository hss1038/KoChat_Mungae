package com.ssam.kochat_mungae

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity() {
    val TAG = "ChatActivity"
    val chatList = ArrayList<Chat>()
    var myAdapter : MyAdapter ?= null
    var mDataSet : Array<String> ?= null
    var database : FirebaseDatabase ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        database = FirebaseDatabase.getInstance()
        var stEmail = intent.getStringExtra("email")
        mDataSet = arrayOf("이순신", "홍길동", "유관순", "장길산")
        myAdapter = MyAdapter(chatList, stEmail)
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)
                val newChat = dataSnapshot.getValue(Chat::class.java)
                val commentKey = dataSnapshot.key
                var stEmail = newChat!!.email
                var stText = newChat!!.etMessage
                Log.d(TAG, "stText : $stText")
                Log.d(TAG, "stEmail : $stEmail")
                chatList.add(newChat)
                myAdapter!!.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                val commentKey = dataSnapshot.key
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)
                val movedChat = dataSnapshot.getValue(Chat::class.java)
                val commentKey = dataSnapshot.key
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(this@ChatActivity, "Failed to load comments.",
                    Toast.LENGTH_SHORT).show()
            }
        }
        val ref  = database!!.getReference("message")
        ref.addChildEventListener(childEventListener)


        btnSend.setOnClickListener {
            var etMessage = etMessage.text
            Toast.makeText(this, etMessage, Toast.LENGTH_SHORT).show()

            val c = Calendar.getInstance()
            val dateformat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa")
            val datetime: String = dateformat.format(c.time)
            println(datetime)
            val myRef = database!!.getReference("message").child(datetime)


            val getMessage: Hashtable<String, String> = Hashtable<String, String>()
            getMessage.put("email", stEmail)
            getMessage.put("etMessage", etMessage.toString())
            myRef.setValue(getMessage)
            etMessage.clear()
        }
        btnFinish.setOnClickListener {
            finish()
        }
    }
}
