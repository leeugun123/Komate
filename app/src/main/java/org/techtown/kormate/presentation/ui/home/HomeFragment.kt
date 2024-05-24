package org.techtown.kormate.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import org.techtown.kormate.R
import org.techtown.kormate.databinding.FragmentHomeBinding
import org.techtown.kormate.presentation.BaseFragment
import org.techtown.kormate.presentation.ui.home.board.BoardFragment
import org.techtown.kormate.presentation.ui.home.board.detail.BoardViewModel
import org.techtown.kormate.presentation.ui.home.myprofile.MyFragment
import org.techtown.kormate.presentation.ui.home.preview.PreviewFragment


class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val previewFragment by lazy { PreviewFragment() }
    private val boardFragment by lazy { BoardFragment() }
    private val myProfileFragment by lazy { MyFragment() }
    private val homeFragmentManager by lazy { childFragmentManager }
    private val boardViewModel: BoardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addFragment()
        initNavigationBar()
        boardViewModel
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
        homeFragmentManager.beginTransaction().hide(previewFragment).commit()
        homeFragmentManager.beginTransaction().hide(boardFragment).commit()
        homeFragmentManager.beginTransaction().show(myProfileFragment).commit()
    }

    private fun showFb() {
        homeFragmentManager.beginTransaction().hide(previewFragment).commit()
        homeFragmentManager.beginTransaction().show(boardFragment).commit()
        homeFragmentManager.beginTransaction().hide(myProfileFragment).commit()
    }

    private fun showFa() {
        homeFragmentManager.beginTransaction().show(previewFragment).commit()
        homeFragmentManager.beginTransaction().hide(boardFragment).commit()
        homeFragmentManager.beginTransaction().hide(myProfileFragment).commit()
    }

    private fun addFragment() {
        homeFragmentManager.beginTransaction().add(R.id.main_frame, previewFragment).commit()
        homeFragmentManager.beginTransaction().add(R.id.main_frame, boardFragment).commit()
        homeFragmentManager.beginTransaction().add(R.id.main_frame, myProfileFragment).commit()
    }
}
