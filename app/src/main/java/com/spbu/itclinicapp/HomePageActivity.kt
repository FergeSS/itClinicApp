package com.spbu.itclinicapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.spbu.itclinicapp.R

class HomePageActivity : AppCompatActivity() {

    var userName = ""

    private lateinit var githubUserName: TextView
    private lateinit var logoutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        githubUserName = findViewById(R.id.id)
        logoutBtn = findViewById(R.id.logOut)
        userName = intent.getStringExtra("githubUserName")!!
        githubUserName.text = userName

        logoutBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }
    }
}