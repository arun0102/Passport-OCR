package arun.pkg.passportdemo.main

import android.widget.*
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableList
import arun.pkg.passportdemo.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.io.File

/**
 * Data Binding Adapter class for various UI elements
 */
@BindingAdapter("picture")
fun bindPicture(view: ImageView, imagePath: String?) {
    if (!imagePath.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(File(imagePath))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            //.transform(RoundedCorners(25)) // Round the corners of the image
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("spinner_data")
fun bindSpinnerData(spinner: Spinner, items: ObservableList<String>) {
    val adapter = ArrayAdapter(
        spinner.context,
        R.layout.row_template_spinner,
        R.id.txt_spinner_item,
        items
    )
    spinner.adapter = adapter
}

@BindingAdapter("onItemSelected")
fun bindSpinnerData(spinner: Spinner, listener: AdapterView.OnItemSelectedListener) {
    spinner.onItemSelectedListener = listener
}

/*
@BindingAdapter("auto_complete_data")
fun bindAutoCompleteTextViewData(autoCompleteTextView: AutoCompleteTextView, items: ObservableList<String>) {
    val adapter = ArrayAdapter(
        autoCompleteTextView.context,
        R.layout.row_template_spinner,
        R.id.txt_spinner_item,
        items
    )
    autoCompleteTextView.setAdapter(adapter)
    autoCompleteTextView.threshold = 1
    autoCompleteTextView.showDropDown()
}

@BindingAdapter("auto_onItemSelected")
fun bindAutoCompleteTextViewData(autoCompleteTextView: AutoCompleteTextView, listener: AdapterView.OnItemSelectedListener) {
    autoCompleteTextView.onItemSelectedListener = listener
}*/
