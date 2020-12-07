package za.co.rundun.openweather.di

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule


@GlideModule
class GlideApp: AppGlideModule() {

    // TODO DI glide with kotlin, still need to investigate
    // This was implemented to suppress a warning
    // https://bumptech.github.io/glide/doc/configuration.html

//    override fun applyOptions(context: Context?, builder: GlideBuilder) {
//        builder.setLogLevel(Log.DEBUG);
//        val calculator = MemorySizeCalculator.Builder(context)
//            .setMemoryCacheScreens(2f)
//            .build()
//        builder.setMemoryCache(LruResourceCache(calculator.memoryCacheSize.toLong()))
//    }
}