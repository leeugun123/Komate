package org.techtown.kormate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import org.techtown.kormate.Fragment.BoardFragment
import org.techtown.kormate.Fragment.HomeFragment
import org.techtown.kormate.Fragment.MyFragment
import org.techtown.kormate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() , BottomNavigationView.OnNavigationItemSelectedListener{

    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        supportFragmentManager.beginTransaction().add(R.id.linearLayout,HomeFragment()).commit()

        val bottomNavigationView = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView

        bottomNavigationView.setOnNavigationItemSelectedListener(this)


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {


        when(item.itemId) {
            R.id.page_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout , HomeFragment()).commitAllowingStateLoss()
                return true
            }
            R.id.page_board -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, BoardFragment()).commitAllowingStateLoss()
                return true
            }
            R.id.page_my -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, MyFragment()).commitAllowingStateLoss()
                return true
            }
        }


        return false
    }
}