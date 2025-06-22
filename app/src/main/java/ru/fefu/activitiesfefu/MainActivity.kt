package ru.fefu.activitiesfefu

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.fefu.activitiesfefu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    internal lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Регистрируем колбэки для управления видимостью фрагментов


        // Первый запуск: показываем фрагмент "Активность"
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container, ActivityFragment(), "ACTIVITY_TAG")
                .commit()
        }

        // Обработка нажатий в BottomNavigationView
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_activity -> {
                    switchFragment(ActivityFragment(), "ACTIVITY_TAG")
                    binding.fabNewActivity.show()
                    true
                }
                R.id.nav_profile -> {
                    switchFragment(ProfileEditFragment(), "PROFILE_TAG")
                    binding.fabNewActivity.hide()
                    true
                }
                else -> false
            }
        }

        binding.fabNewActivity.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, NewActivityFragment(), "NEW_ACTIVITY_TAG")
                .addToBackStack(null)
                .commit()
            binding.fabNewActivity.hide()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun switchFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }
}