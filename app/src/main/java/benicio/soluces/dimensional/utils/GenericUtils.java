package benicio.soluces.dimensional.utils;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class GenericUtils {

    public static boolean isSameDrawable(ImageView imageView, int drawableResource) {
        Drawable currentDrawable = imageView.getDrawable();
        Drawable targetDrawable = ContextCompat.getDrawable(imageView.getContext(), drawableResource);

        return currentDrawable != null && currentDrawable.getConstantState() != null &&
                targetDrawable != null && targetDrawable.getConstantState() != null &&
                currentDrawable.getConstantState().equals(targetDrawable.getConstantState());
    }
}
