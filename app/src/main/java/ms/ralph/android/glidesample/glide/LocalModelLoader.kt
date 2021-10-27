package ms.ralph.android.glidesample.glide

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import java.io.InputStream

class LocalModelLoader(
    private val context: Context
) : ModelLoader<DroidKaigiIconRequest, InputStream> {
    override fun buildLoadData(
        model: DroidKaigiIconRequest,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream> {
        val file = DroidKaigiIconLocalCacheFileGenerator.getFile(context, model.year)
        return ModelLoader.LoadData(ObjectKey(model), LocalFetcher(file))
    }

    override fun handles(model: DroidKaigiIconRequest): Boolean =
        DroidKaigiIconLocalCacheFileGenerator.getFile(context, model.year).exists()

    class Factory(
        private val context: Context
    ) : ModelLoaderFactory<DroidKaigiIconRequest, InputStream> {

        override fun build(
            multiFactory: MultiModelLoaderFactory
        ): ModelLoader<DroidKaigiIconRequest, InputStream> = LocalModelLoader(context)

        override fun teardown() = Unit
    }
}
