package com.ssam.kochat_mungae


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    val mAuth = FirebaseAuth.getInstance();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLogin.setOnClickListener {
            val email = etId.text.toString()
            val password = etPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(applicationContext, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            progressBar.bringToFront()
            progressBar.visibility = View.VISIBLE
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        val user = mAuth.currentUser
                        val name = user!!.displayName
                        val email = user.email
                        Log.d(TAG, "name: $name, email: $email")

                        val sharedPref: SharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("email", email)
                        editor.commit()


                        val intent = Intent(this@MainActivity, TabActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@MainActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnRegister.setOnClickListener {
            val email = etId.text.toString()
            val password = etPassword.text.toString()
            progressBar.bringToFront()
            progressBar.visibility = View.VISIBLE
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(applicationContext, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, object : OnCompleteListener<AuthResult?> {
                        override fun onComplete(task: Task<AuthResult?>) {
                            progressBar.visibility = View.GONE
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success")
                                val user = mAuth.currentUser

                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException())
                                Toast.makeText(this@MainActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
    }
}
