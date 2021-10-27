package ms.ralph.android.glidesample.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.InputStream

@GlideModule
class MyAppGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val okHttpClient = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor()).build()
        registry.prepend(
            DroidKaigiIconRequest::class.java,
            InputStream::class.java,
            RemoteModelLoader.Factory(context, okHttpClient)
        )
        registry.prepend(
            DroidKaigiIconRequest::class.java,
            InputStream::class.java,
            LocalModelLoader.Factory(context)
        )
    }
}
