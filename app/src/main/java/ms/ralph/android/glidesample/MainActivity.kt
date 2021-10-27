package ms.ralph.android.glidesample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ms.ralph.android.glidesample.databinding.ActivityMainBinding
import ms.ralph.android.glidesample.glide.DroidKaigiIconRequest
import ms.ralph.android.glidesample.glide.GlideApp

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.button2015.setOnClickListener {
            loadImage(2015)
        }
        binding.button2016.setOnClickListener {
            loadImage(2016)
        }
        binding.button2017.setOnClickListener {
            loadImage(2017)
        }
        binding.button2018.setOnClickListener {
            loadImage(2018)
        }
        binding.button2019.setOnClickListener {
            loadImage(2019)
        }
        binding.button2020.setOnClickListener {
            loadImage(2020)
        }
        binding.buttonClearCache.setOnClickListener { clearLocalCache() }
    }

    private fun loadImage(year: Int) {
        GlideApp.with(this)
            .load(DroidKaigiIconRequest(year))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(binding.checkboxSkipMemoryCache.isChecked)
            .into(binding.image)
    }

    private fun clearLocalCache() {
        lifecycle.coroutineScope.launch {
            deleteAllFiles()
            Toast.makeText(this@MainActivity, "Deleted!", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun deleteAllFiles() = withContext(Dispatchers.IO) {
        filesDir.listFiles()?.forEach { it.delete() }
    }
}
