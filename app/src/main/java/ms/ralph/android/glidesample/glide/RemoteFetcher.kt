package ms.ralph.android.glidesample.glide

import android.util.Log
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.HttpException
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.util.ContentLengthInputStream
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.io.InputStream

class RemoteFetcher(
    private val okHttpClient: OkHttpClient,
    private val url: String,
    private val file: File
) : DataFetcher<InputStream>, okhttp3.Callback {

    @Volatile
    private var originalCallback: DataFetcher.DataCallback<in InputStream>? = null

    @Volatile
    private var call: Call? = null

    @Volatile
    private var openedInputStream: InputStream? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        originalCallback = callback
        val localCall = okHttpClient.newCall(Request.Builder().url(url).build())
        Log.d(TAG, "loadData: $url, $file")
        localCall.enqueue(this)
        call = localCall
    }

    override fun onResponse(call: Call, response: Response) {
        Log.d(TAG, "onResponse: $url")
        response.body.use { body ->
            if (!response.isSuccessful || body == null) {
                originalCallback?.onLoadFailed(HttpException(response.message, response.code))
                return
            }
            val stream = ContentLengthInputStream.obtain(body.byteStream(), body.contentLength())
            file.parentFile?.mkdirs()
            val isSavedSuccessfully = stream.copyToFileAtomically(file)
            if (!isSavedSuccessfully) {
                originalCallback?.onLoadFailed(IOException("Failed to copy remote data to file."))
            }
            try {
                val inputStream = file.inputStream().buffered()
                openedInputStream = inputStream
                originalCallback?.onDataReady(inputStream)
            } catch (e: IOException) {
                originalCallback?.onLoadFailed(e)
            }
        }
    }

    override fun onFailure(call: Call, e: IOException) {
        Log.d(TAG, "onFailure: $url")
        originalCallback?.onLoadFailed(e)
    }

    override fun cleanup() {
        try {
            openedInputStream?.close()
        } catch (_: IOException) {
        }
        originalCallback = null
        openedInputStream = null
    }

    override fun cancel() {
        call?.cancel()
    }

    override fun getDataSource(): DataSource = DataSource.REMOTE

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    companion object {

        private const val TAG = "RemoteFetcher"

        private fun InputStream.copyToFileAtomically(
            destinationFile: File
        ): Boolean {
            val temporaryFile = try {
                File.createTempFile("remote-fetcher-", ".downloading", destinationFile.parentFile)
            } catch (_: IOException) {
                return false
            }

            try {
                temporaryFile.outputStream().buffered().use { copyTo(it) }
            } catch (e: IOException) {
                temporaryFile.delete()
                return false
            }

            val isDestinationFileCleaned = !destinationFile.exists() || destinationFile.delete()
            if (!isDestinationFileCleaned) {
                temporaryFile.delete()
                return false
            }
            val isRenamedSuccessfully = temporaryFile.renameTo(destinationFile)
            if (!isRenamedSuccessfully) {
                temporaryFile.delete()
                return false
            }
            return true
        }
    }
}
