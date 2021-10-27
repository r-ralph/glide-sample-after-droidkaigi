package ms.ralph.android.glidesample.glide

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import okhttp3.OkHttpClient
import java.io.InputStream

class RemoteModelLoader(
    private val context: Context,
    private val okHttpClient: OkHttpClient
) : ModelLoader<DroidKaigiIconRequest, InputStream> {
    override fun buildLoadData(
        model: DroidKaigiIconRequest,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream> {
        val url = DroidKaigiIconUrlGenerator.getUrl(model.year)
        val file = DroidKaigiIconLocalCacheFileGenerator.getFile(context, model.year)
        return ModelLoader.LoadData(ObjectKey(model), RemoteFetcher(okHttpClient, url, file))
    }

    override fun handles(model: DroidKaigiIconRequest): Boolean = true

    class Factory(
        private val context: Context,
        private val okHttpClient: OkHttpClient
    ) : ModelLoaderFactory<DroidKaigiIconRequest, InputStream> {

        override fun build(
            multiFactory: MultiModelLoaderFactory
        ): ModelLoader<DroidKaigiIconRequest, InputStream> =
            RemoteModelLoader(context, okHttpClient)

        override fun teardown() = Unit
    }
}
