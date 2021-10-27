package ms.ralph.android.glidesample.glide

import android.content.Context
import java.io.File

object DroidKaigiIconLocalCacheFileGenerator {

    fun getFile(context: Context, year: Int): File {
        return context.filesDir.resolve("$year.png")
    }
}
