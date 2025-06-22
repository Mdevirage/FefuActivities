package ru.fefu.activitiesfefu

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.fefu.activitiesfefu.databinding.ActivityWelcomeBinding
import android.content.Intent
import android.graphics.Color
import android.text.TextPaint

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        val txt = "Уже есть аккаунт?"
        val sp = SpannableString(txt)
        sp.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }, 0, txt.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvLogin.text = sp
        binding.tvLogin.movementMethod = LinkMovementMethod.getInstance()
        binding.tvLogin.highlightColor = Color.TRANSPARENT
    }
}