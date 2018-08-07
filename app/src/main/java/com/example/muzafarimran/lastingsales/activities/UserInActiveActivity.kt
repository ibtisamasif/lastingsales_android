//package com.example.muzafarimran.lastingsales.activities
//
//import android.content.Intent
//import android.os.Bundle
//import android.support.v7.app.AppCompatActivity
//import com.example.muzafarimran.lastingsales.R
//import com.example.muzafarimran.lastingsales.SessionManager
//import kotlinx.android.synthetic.main.activity_company_inactive.*
//
//class UserInActiveActivity : AppCompatActivity() {
//    companion object {
//        const val KEY_MESSAGE = "message"
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_user_inactive)
////        supportActionBar!!.title = "About"
//
////        val intent = intent
////        val ss: String = intent.getStringExtra(KEY_MESSAGE)
////        if (ss != null) {
//////            tvMsg.text = ss
////        } else {
//////            tvMsg.text = "Calling"
////        }
//
//        bAction.setOnClickListener {
//            var sessionManager = SessionManager(baseContext)
//            sessionManager.logoutUser()
//            startActivity(Intent(baseContext, LogInActivity::class.java))
//            finish()
//
//////        tvMsg.text = "Calling";
////            val number = "03111308308"
////            val intent = Intent(Intent.ACTION_CALL)
////            intent.data = Uri.parse("tel:$number")
////            startActivity(intent)
//        }
////        bCancel.setOnClickListener {
////            System.exit(0)
////        }
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        System.exit(0)
//    }
//}
