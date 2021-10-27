package ms.ralph.android.glidesample.glide

import android.util.Log
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import java.io.File
import java.io.IOException
import java.io.InputStream

class LocalFetcher(
    private val file: File
) : DataFetcher<InputStream> {

    private var openedInputStream: InputStream? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        Log.d(TAG, "loadData: $file")
        try {
            val inputStream = file.inputStream().buffered()
            openedInputStream = inputStream
            callback.onDataReady(inputStream)
        } catch (e: IOException) {
            callback.onLoadFailed(e)
        }
    }

    override fun cleanup() {
        try {
            openedInputStream?.close()
        } catch (_: IOException) {
        }
        openedInputStream = null
    }

    override fun cancel() = Unit

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    companion object {
        private const val TAG = "LocalFetcher"
    }
}
