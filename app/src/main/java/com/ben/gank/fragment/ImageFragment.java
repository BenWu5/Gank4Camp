package com.ben.gank.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.ben.gank.R;
import com.ben.gank.activity.base.AppSwipeBackActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import butterknife.Bind;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageFragment extends BaseFragment {
    private static final int SYSTEM_UI_BASE_VISIBILITY = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

    private static final int SYSTEM_UI_IMMERSIVE = View.SYSTEM_UI_FLAG_IMMERSIVE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;

    public static final String KEY_URL = "key_url";
    @Bind(R.id.image)
    ImageView mImageView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private String mUrl;

    PhotoViewAttacher mAttacher;

//    public static ImageFragment newFragment(String url) {
//        Bundle bundle = new Bundle();
//        bundle.putString(KEY_URL, url);
//        ImageFragment fragment = new ImageFragment();
//        fragment.setArguments(bundle);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString(KEY_URL, "");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_image;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Glide.with(getActivity())
//                .load(mUrl)
//                .into(mImageView);

    }

    @Override
    public void onResume() {
        super.onResume();
        Glide.with(this).load(mUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (null != resource) {
                    mImageView.setImageBitmap(resource);
                    mAttacher = new PhotoViewAttacher(mImageView);
                    //maybeStartPostponedEnterTransition();
                } else {
                    //getActivity().supportFinishAfterTransition();
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
            }
        });
    }



    public void toggleToolbar() {
        if (mToolbar.getTranslationY() == 0) {
            hideSystemUi();
        } else {
            showSystemUi();
        }
    }


    private void showSystemUi() {
        mImageView.setSystemUiVisibility(SYSTEM_UI_BASE_VISIBILITY);
        mToolbar.animate()
                .translationY(0)
                .setDuration(400)
                .start();
    }

    private void hideSystemUi() {
        mImageView.setSystemUiVisibility(SYSTEM_UI_BASE_VISIBILITY | SYSTEM_UI_IMMERSIVE);
        mToolbar.animate()
                .translationY(-mToolbar.getHeight())
                .setDuration(400)
                .start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            ((AppSwipeBackActivity) getActivity()).scrollToFinishActivity();
        }
        //保存照片未完成
//        } else if (id == R.id.action_save) {
//            final File SAVE_PATH = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "gank");
//            final File file = new File(SAVE_PATH, FileKit.getFileRealName(mUrl));
//            Glide.with(getActivity()).load(mUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                    FileKit.saveBitmapFile(resource, file);
//                    showSnackbar(SAVE_PATH.getPath());
//                }
//            });
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_image, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
