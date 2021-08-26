package codeafifudin.fatakhul.projectta.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.Display
import androidx.core.content.res.ResourcesCompat
import codeafifudin.fatakhul.projectta.R
import kotlinx.android.synthetic.main.custom_progress.view.*

class CustomProgress {

    lateinit var dialog: CustomDialog

    fun show(context: Context): Dialog {
        return show(context, null)
    }

    fun hide(context: Context): Dialog {
        dialog.hide()
        return dialog
    }

    fun show(context: Context, title: CharSequence?): Dialog {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.custom_progress,null)

        setColorFilter(view.cp_pbar.indeterminateDrawable, ResourcesCompat.getColor(context.resources, R.color.white, null))
        dialog = CustomDialog(context)
        view.textViewLoading.setText(title)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }

    private fun setColorFilter(drawable: Drawable, color: Int){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        }else{
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            window?.decorView?.rootView?.setBackgroundResource(R.color.blacktransparent)
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }
        }
    }

}