package org.techtown.kormate.presentation.ui.home

import android.os.Bundle
import android.view.View
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentHomeBinding
import org.techtown.kormate.presentation.BaseFragment
import org.techtown.kormate.presentation.ui.home.board.BoardFragment
import org.techtown.kormate.presentation.ui.home.myprofile.MyFragment
import org.techtown.kormate.presentation.ui.home.preview.PreviewFragment


class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val fa by lazy { PreviewFragment() }
    private val fb by lazy { BoardFragment() }
    private val fc by lazy { MyFragment() }
    private val fragmentManager by lazy { childFragmentManager }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addFragment()
        initNavigationBar()
    }

    private fun initNavigationBar() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.page_home -> {
                    showFa()
                }

                R.id.page_board -> {
                    showFb()
                }

                R.id.page_my -> {
                    showFc()
                }

                else -> throw IllegalArgumentException("유효하지 않습니다.")
            }
            true
        }
    }

    private fun showFc() {
        fragmentManager.beginTransaction().hide(fa).commit()
        fragmentManager.beginTransaction().hide(fb).commit()
        fragmentManager.beginTransaction().show(fc).commit()
    }

    private fun showFb() {
        fragmentManager.beginTransaction().hide(fa).commit()
        fragmentManager.beginTransaction().show(fb).commit()
        fragmentManager.beginTransaction().hide(fc).commit()
    }

    private fun showFa() {
        fragmentManager.beginTransaction().show(fa).commit()
        fragmentManager.beginTransaction().hide(fb).commit()
        fragmentManager.beginTransaction().hide(fc).commit()
    }

    private fun addFragment() {
        fragmentManager.beginTransaction().add(R.id.main_frame, fa).commit()
        fragmentManager.beginTransaction().add(R.id.main_frame, fb).commit()
        fragmentManager.beginTransaction().add(R.id.main_frame, fc).commit()
    }
}
