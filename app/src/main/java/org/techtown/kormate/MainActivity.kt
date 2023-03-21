package org.techtown.kormate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.techtown.kormate.Fragment.BoardFragment
import org.techtown.kormate.Fragment.CalendarFragment
import org.techtown.kormate.Fragment.HomeFragment

class MainActivity : AppCompatActivity() , BottomNavigationView.OnNavigationItemSelectedListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        supportFragmentManager.beginTransaction().add(R.id.linearLayout,HomeFragment()).commit()


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
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, CalendarFragment()).commitAllowingStateLoss()
                return true
            }
        }


        return false
    }
}