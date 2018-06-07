package com.example.muzafarimran.lastingsales.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.muzafarimran.lastingsales.R
import kotlinx.android.synthetic.main.activity_trial_expiry.*

class UpgradeActivity : AppCompatActivity() {
        companion object {
            const val KEY_MESSAGE = "message"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade)
//        supportActionBar!!.title = "About"

        val intent = getIntent();
        val ss: String = intent.getStringExtra(KEY_MESSAGE)
        if (ss != null){
            tvMsg.text = ss
        }

        bAction.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.lastingsales.agent"))
            startActivity(intent)
        }
//        bCancel.setOnClickListener {
//            System.exit(0)
//        }
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        System.exit(0)
//    }
}
