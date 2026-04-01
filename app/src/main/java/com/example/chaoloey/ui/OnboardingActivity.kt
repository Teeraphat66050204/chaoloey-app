package com.example.chaoloey.ui

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Space
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.chaoloey.LoginActivity
import com.example.chaoloey.R
import com.example.chaoloey.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val fragments = mutableListOf<Fragment>()
    private lateinit var adapter: FragmentStateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupDotIndicators()
    }

    private fun setupViewPager() {
        fragments.add(OnboardingPage1Fragment())
        fragments.add(OnboardingPage2Fragment())

        adapter = OnboardingPagerAdapter(this, fragments)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDotIndicators(position)
            }
        })
    }

    private fun setupDotIndicators() {
        val dotsLayout = binding.dotsLayout
        val dots = arrayOfNulls<Space>(fragments.size)

        for (i in dots.indices) {
            dots[i] = Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(12, 12).apply {
                    setMargins(8, 0, 8, 0)
                }
                setBackgroundColor(ContextCompat.getColor(this@OnboardingActivity, android.R.color.darker_gray))
            }
            dotsLayout.addView(dots[i])
        }

        // Set first dot as active
        if (dots.isNotEmpty()) {
            dots[0]?.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    private fun updateDotIndicators(position: Int) {
        for (i in 0 until binding.dotsLayout.childCount) {
            val dot = binding.dotsLayout.getChildAt(i)
            if (i == position) {
                dot.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            } else {
                dot.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            }
        }
    }

    fun goToNextPage() {
        val nextPage = binding.viewPager.currentItem + 1
        if (nextPage < fragments.size) {
            binding.viewPager.setCurrentItem(nextPage, true)
        }
    }

    fun finishOnboarding() {

        // Go to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

class OnboardingPagerAdapter(
    activity: AppCompatActivity,
    private val fragments: List<Fragment>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}

