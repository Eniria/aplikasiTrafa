package com.example.trafa.ui.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import com.example.trafa.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingBinding
    private val viewModel by viewModels<SettingViewModel> {
        SettingViewModel.Factory(SettingPreferences(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.getTheme().observe(this){
            if (it) {
                binding.switchThema.text = "Dark Theme"
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                binding.switchThema.text="Light Theme"
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            binding.switchThema.isChecked = it
        }

        binding.switchThema.setOnCheckedChangeListener{ _, isChaked ->
            viewModel.saveTheme(isChaked)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}