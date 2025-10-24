package com.spbu.itclinicapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.FirebaseApp
import com.spbu.itclinicapp.R
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var loginBtn: Button

    // firebaseAuth variable to be initialized later
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ensure FirebaseApp is initialized before any Firebase API usage
        val firebaseApp = FirebaseApp.initializeApp(this)
        if (firebaseApp == null) {
            Toast.makeText(
                this,
                "Firebase is not initialized. Add google-services.json and apply the Google Services plugin.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        loginBtn = findViewById(R.id.github_login_btn)

        // initializing auth
        auth = FirebaseAuth.getInstance()


        // call signInWithGithubProvider() method
        // after clicking login Button
        loginBtn.setOnClickListener {
            // Just start the GitHub sign-in flow. GitHub will decide if the user has a session.
            signInWithGithubProvider()
        }
    }

    // To check if there is a pending result, call pendingAuthResult
    private fun signInWithGithubProvider() {

        // There's something already here! Finish the sign-in for your user.
        val pendingResultTask: Task<AuthResult>? = auth.pendingAuthResult
        if (pendingResultTask != null) {
            pendingResultTask
                .addOnSuccessListener {
                    // User is signed in.
                    Toast.makeText(this, "User exist", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    // Handle failure.
                    Toast.makeText(this, "Error : $it", Toast.LENGTH_LONG).show()
                }
        } else {
            // Build the OAuth provider without a login hint; GitHub will use existing session or ask for auth.
            val provider = OAuthProvider.newBuilder("github.com").apply {
                scopes = arrayListOf("read:user", "user:email")
            }
            auth.startActivityForSignInWithProvider( /* activity= */this, provider.build())
                .addOnSuccessListener(
                    OnSuccessListener<AuthResult?> {
                        // User is signed in.
                        // retrieve the current user
                        firebaseUser = auth.currentUser!!

                        // navigate to HomePageActivity after successful login
                        val intent = Intent(this, HomePageActivity::class.java)

                        // send github user name from MainActivity to HomePageActivity
                        intent.putExtra("githubUserName", firebaseUser.displayName)
                        this.startActivity(intent)
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_LONG).show()

                    })
                .addOnFailureListener(
                    OnFailureListener {
                        // Handle failure.
                        Toast.makeText(this, "Error : $it", Toast.LENGTH_LONG).show()
                    })
        }

    }
}