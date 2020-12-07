package za.co.rundun.openweather.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import za.co.rundun.openweather.R


@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        val options: RequestOptions = RequestOptions()
            .fitCenter()
            .placeholder(R.mipmap.ic_launcher_round)
            .error(R.mipmap.ic_launcher)

        Glide.with(view.context)
            .load(imageUrl)
            .apply(options)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}