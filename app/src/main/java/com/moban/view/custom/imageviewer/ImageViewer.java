package com.moban.view.custom.imageviewer;

/**
 * Created by LenVo on 6/12/18.
 */


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.StyleRes;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Created by Alexander Krol (troy379) on 29.08.16.
 */
public class ImageViewer implements OnDismissListener, DialogInterface.OnKeyListener {

    private static final String TAG = ImageViewer.class.getSimpleName();

    private ImageViewer.Builder builder;
    private AlertDialog dialog;
    private ImageViewerView viewer;

    protected ImageViewer(Builder builder) {
        this.builder = builder;
        createDialog();
    }

    /**
     * Displays the built viewer if passed images list isn't empty
     */
    public void show() {
        if (!builder.dataSet.data.isEmpty()) {
            dialog.show();
        } else {
            Log.w(TAG, "Images list cannot be empty! Viewer ignored.");
        }
    }

    public String getUrl() {
        return viewer.getUrl();
    }

    private void createDialog() {
        viewer = new ImageViewerView(builder.context);
        viewer.setCustomImageRequestBuilder(builder.customImageRequestBuilder);
        viewer.setCustomDraweeHierarchyBuilder(builder.customHierarchyBuilder);
        viewer.allowZooming(builder.isZoomingAllowed);
        viewer.allowSwipeToDismiss(builder.isSwipeToDismissAllowed);
        viewer.setOnDismissListener(this);
        viewer.setBackgroundColor(builder.backgroundColor);
        viewer.setOverlayView(builder.overlayView);
        viewer.setImageMargin(builder.imageMarginPixels);
        viewer.setContainerPadding(builder.containerPaddingPixels);
        viewer.setUrls(builder.dataSet, builder.startPosition);
        viewer.setPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (builder.imageChangeListener != null) {
                    builder.imageChangeListener.onImageChange(position);
                }
            }
        });

        viewer.setOnTouchImageListener(new OnTouchImageListener() {
            @Override
            public void onTouchImage() {
                if (builder.onTouchImageListener != null) {
                    builder.onTouchImageListener.onTouchImage();
                }
            }
        });

        viewer.setOnLongTouchImageListener(new OnLongTouchImageListener(){
            @Override
            public void onLongTouchImage() {
                if (builder.onLongTouchImageListener != null) {
                    builder.onLongTouchImageListener.onLongTouchImage();
                }
            }
        });

        dialog = new AlertDialog.Builder(builder.context, getDialogStyle())
                .setView(viewer)
                .setOnKeyListener(this)
                .create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (builder.onDismissListener != null) {
                    builder.onDismissListener.onDismiss();
                }
            }
        });
    }

    /**
     * Fires when swipe to dismiss was initiated
     */
    @Override
    public void onDismiss() {
        dialog.dismiss();
    }

    /**
     * Resets image on {@literal KeyEvent.KEYCODE_BACK} to normal scale if needed, otherwise - hide the viewer.
     */
    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP &&
                !event.isCanceled()) {
            if (viewer.isScaled()) {
                viewer.resetScale();
            } else {
                dialog.cancel();
            }
        }
        return true;
    }

    /**
     * Creates new {@code ImageRequestBuilder}.
     */
    public static ImageRequestBuilder createImageRequestBuilder() {
        return ImageRequestBuilder.newBuilderWithSource(Uri.parse(""));
    }

    /**
     * Interface definition for a callback to be invoked when image was changed
     */
    public interface OnImageChangeListener {
        void onImageChange(int position);
    }

    /**
     * Interface definition for a callback to be invoked when viewer was dismissed
     */
    public interface OnDismissListener {
        void onDismiss();
    }

    private @StyleRes int getDialogStyle() {
        return builder.shouldStatusBarHide
                ? android.R.style.Theme_Translucent_NoTitleBar_Fullscreen
                : android.R.style.Theme_Translucent_NoTitleBar;
    }

    /**
     * Interface used to format custom objects into an image url.
     */
    public interface Formatter<T> {

        /**
         * Formats an image url representation of the object.
         *
         * @param t The object that needs to be formatted into url.
         * @return An url of image.
         */
        String format(T t);
    }

    static class DataSet<T> {

        private List<T> data;
        private ImageViewer.Formatter<T> formatter;

        DataSet(List<T> data) {
            this.data = data;
        }

        String format(int position) {
            return format(data.get(position));
        }

        String format(T t) {
            if (formatter == null) return t.toString();
            else return formatter.format(t);
        }

        public List<T> getData() {
            return data;
        }
    }

    /**
     * Builder class for {@link com.stfalcon.frescoimageviewer.ImageViewer}
     */
    public static class Builder<T> {

        private Context context;
        private ImageViewer.DataSet<T> dataSet;
        private @ColorInt int backgroundColor = Color.BLACK;
        private int startPosition;
        private ImageViewer.OnImageChangeListener imageChangeListener;
        private ImageViewer.OnDismissListener onDismissListener;
        private OnTouchImageListener onTouchImageListener;
        private OnLongTouchImageListener onLongTouchImageListener;
        private View overlayView;
        private int imageMarginPixels;
        private int[] containerPaddingPixels = new int[4];
        private ImageRequestBuilder customImageRequestBuilder;
        private GenericDraweeHierarchyBuilder customHierarchyBuilder;
        private boolean shouldStatusBarHide = true;
        private boolean isZoomingAllowed = true;
        private boolean isSwipeToDismissAllowed = true;

        /**
         * Constructor using a context and images urls array for this builder and the {@link com.stfalcon.frescoimageviewer.ImageViewer} it creates.
         */
        public Builder(Context context, T[] images) {
            this(context, new ArrayList<>(Arrays.asList(images)));
        }

        /**
         * Constructor using a context and images urls list for this builder and the {@link com.stfalcon.frescoimageviewer.ImageViewer} it creates.
         */
        public Builder(Context context, List<T> images) {
            this.context = context;
            this.dataSet = new ImageViewer.DataSet<>(images);
        }

        /**
         * If you use an non-string collection, you can use custom {@link com.stfalcon.frescoimageviewer.ImageViewer.Formatter} to represent it as url.
         */
        public ImageViewer.Builder setFormatter(ImageViewer.Formatter<T> formatter) {
            this.dataSet.formatter = formatter;
            return this;
        }

        /**
         * Set background color resource for viewer
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        @SuppressWarnings("deprecation")
        public ImageViewer.Builder setBackgroundColorRes(@ColorRes int color) {
            return this.setBackgroundColor(context.getResources().getColor(color));
        }

        /**
         * Set background color int for viewer
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder setBackgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }

        /**
         * Set background color int for viewer
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder setStartPosition(int position) {
            this.startPosition = position;
            return this;
        }

        /**
         * Set {@link com.stfalcon.frescoimageviewer.ImageViewer.OnImageChangeListener} for viewer.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder setImageChangeListener(ImageViewer.OnImageChangeListener imageChangeListener) {
            this.imageChangeListener = imageChangeListener;
            return this;
        }

        /**
         * Set overlay view
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder setOverlayView(View view) {
            this.overlayView = view;
            return this;
        }

        /**
         * Set space between the images in px.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder setImageMarginPx(int marginPixels) {
            this.imageMarginPixels = marginPixels;
            return this;
        }

        /**
         * Set space between the images using dimension.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder setImageMargin(Context context, @DimenRes int dimen) {
            this.imageMarginPixels = Math.round(context.getResources().getDimension(dimen));
            return this;
        }

        /**
         * Set {@code start}, {@code top}, {@code end} and {@code bottom} padding for zooming and scrolling area in px.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder setContainerPaddingPx(int start, int top, int end, int bottom) {
            this.containerPaddingPixels = new int[]{start, top, end, bottom};
            return this;
        }

        /**
         * Set {@code start}, {@code top}, {@code end} and {@code bottom} padding for zooming and scrolling area using dimension.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder setContainerPadding(Context context,
                                                                                      @DimenRes int start, @DimenRes int top,
                                                                                      @DimenRes int end, @DimenRes int bottom) {
            setContainerPaddingPx(
                    Math.round(context.getResources().getDimension(start)),
                    Math.round(context.getResources().getDimension(top)),
                    Math.round(context.getResources().getDimension(end)),
                    Math.round(context.getResources().getDimension(bottom))
            );
            return this;
        }

        /**
         * Set common padding for zooming and scrolling area in px.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder setContainerPaddingPx(int padding) {
            this.containerPaddingPixels = new int[]{padding, padding, padding, padding};
            return this;
        }

        /**
         * Set common padding for zooming and scrolling area using dimension.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder setContainerPadding(Context context, @DimenRes int padding) {
            int paddingPx = Math.round(context.getResources().getDimension(padding));
            setContainerPaddingPx(paddingPx, paddingPx, paddingPx, paddingPx);
            return this;
        }

        /**
         * Set status bar visibility. By default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder hideStatusBar(boolean shouldHide) {
            this.shouldStatusBarHide = shouldHide;
            return this;
        }

        /**
         * Allow or disallow zooming. By default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder allowZooming(boolean value) {
            this.isZoomingAllowed = value;
            return this;
        }

        /**
         * Allow or disallow swipe to dismiss gesture. By default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder allowSwipeToDismiss(boolean value) {
            this.isSwipeToDismissAllowed = value;
            return this;
        }

        /**
         * Set {@link com.stfalcon.frescoimageviewer.ImageViewer.OnDismissListener} for viewer.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder setOnDismissListener(ImageViewer.OnDismissListener onDismissListener) {
            this.onDismissListener = onDismissListener;
            return this;
        }

        public ImageViewer.Builder setOnLongTouchImageListener(OnLongTouchImageListener onLongTouchImageListener) {
            this.onLongTouchImageListener = onLongTouchImageListener;
            return this;
        }

        public ImageViewer.Builder setOnTouchImageListener(OnTouchImageListener onTouchImageListener) {
            this.onTouchImageListener = onTouchImageListener;
            return this;
        }

        /**
         * Set @{@code ImageRequestBuilder} for drawees. Use it for post-processing, custom resize options etc.
         * Use {@link com.stfalcon.frescoimageviewer.ImageViewer#createImageRequestBuilder()} to create its new instance.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder setCustomImageRequestBuilder(ImageRequestBuilder customImageRequestBuilder) {
            this.customImageRequestBuilder = customImageRequestBuilder;
            return this;
        }

        /**
         * Set {@link GenericDraweeHierarchyBuilder} for drawees inside viewer.
         * Use it for drawee customizing (e.g. failure image, placeholder, progressbar etc.)
         * N.B.! Due to zoom logic there is limitation of scale type which always equals FIT_CENTER. Other values will be ignored
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public ImageViewer.Builder setCustomDraweeHierarchyBuilder(GenericDraweeHierarchyBuilder customHierarchyBuilder) {
            this.customHierarchyBuilder = customHierarchyBuilder;
            return this;
        }

        /**
         * Creates a {@link com.stfalcon.frescoimageviewer.ImageViewer} with the arguments supplied to this builder. It does not
         * {@link com.stfalcon.frescoimageviewer.ImageViewer#show()} the dialog. This allows the user to do any extra processing
         * before displaying the dialog. Use {@link #show()} if you don't have any other processing
         * to do and want this to be created and displayed.
         */
        public ImageViewer build() {
            return new ImageViewer(this);
        }

        /**
         * Creates a {@link com.stfalcon.frescoimageviewer.ImageViewer} with the arguments supplied to this builder and
         * {@link com.stfalcon.frescoimageviewer.ImageViewer#show()}'s the dialog.
         */
        public ImageViewer show() {
            ImageViewer dialog = build();
            dialog.show();
            return dialog;
        }
    }
}
