package com.tenone.gamebox.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.tenone.gamebox.R;
import com.tenone.gamebox.mode.mode.BGAImage;
import com.tenone.gamebox.view.adapter.BGAPhotoPageAdapter;
import com.tenone.gamebox.view.custom.PhotoViewAttacher;
import com.tenone.gamebox.view.custom.bga.BGAHackyViewPager;
import com.tenone.gamebox.view.utils.BGAAsyncTask;
import com.tenone.gamebox.view.utils.BGAImageLoader;
import com.tenone.gamebox.view.utils.BGAPhotoPickerUtil;
import com.tenone.gamebox.view.utils.BGASavePhotoTask;

import java.io.File;
import java.util.ArrayList;

/**
 * 描述:图片预览界面
 */
public class BGAPhotoPreviewActivity extends BGAPPToolbarActivity implements PhotoViewAttacher.OnViewTapListener, BGAAsyncTask.Callback<Void> {
    private static final String EXTRA_SAVE_IMG_DIR = "EXTRA_SAVE_IMG_DIR";
    private static final String EXTRA_PREVIEW_IMAGES = "EXTRA_PREVIEW_IMAGES";
    private static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";
    private static final String EXTRA_IS_SINGLE_PREVIEW = "EXTRA_IS_SINGLE_PREVIEW";
    private static final String EXTRA_PHOTO_PATH = "EXTRA_PHOTO_PATH";

    private TextView mTitleTv;
    private ImageView mDownloadIv;
    private BGAHackyViewPager mContentHvp;
    private BGAPhotoPageAdapter mPhotoPageAdapter;

    private boolean mIsSinglePreview;

    private File mSaveImgDir;

    private boolean mIsHidden = false;
    private BGASavePhotoTask mSavePhotoTask;

    private long mLastShowHiddenTime;

    public static Intent newIntent(Context context, File saveImgDir, ArrayList<String> previewImages, int currentPosition) {
        Intent intent = new Intent(context, BGAPhotoPreviewActivity.class);
        intent.putExtra(EXTRA_SAVE_IMG_DIR, saveImgDir);
        intent.putStringArrayListExtra(EXTRA_PREVIEW_IMAGES, previewImages);
        intent.putExtra(EXTRA_CURRENT_POSITION, currentPosition);
        intent.putExtra(EXTRA_IS_SINGLE_PREVIEW, false);
        return intent;
    }

    public static Intent newIntent(Context context, File saveImgDir, String photoPath) {
        Intent intent = new Intent(context, BGAPhotoPreviewActivity.class);
        intent.putExtra(EXTRA_SAVE_IMG_DIR, saveImgDir);
        intent.putExtra(EXTRA_PHOTO_PATH, photoPath);
        intent.putExtra(EXTRA_CURRENT_POSITION, 0);
        intent.putExtra(EXTRA_IS_SINGLE_PREVIEW, true);
        return intent;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setNoLinearContentView( R.layout.bga_pp_activity_photo_preview);
        mContentHvp = getViewById(R.id.hvp_photo_preview_content);
    }

    @Override
    protected void setListener() {
        mContentHvp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                renderTitleTv();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mSaveImgDir = (File) getIntent().getSerializableExtra(EXTRA_SAVE_IMG_DIR);
        if (mSaveImgDir != null && !mSaveImgDir.exists()) {
            mSaveImgDir.mkdirs();
        }

        ArrayList<String> previewImages = getIntent().getStringArrayListExtra(EXTRA_PREVIEW_IMAGES);

        mIsSinglePreview = getIntent().getBooleanExtra(EXTRA_IS_SINGLE_PREVIEW, false);
        if (mIsSinglePreview) {
            previewImages = new ArrayList<>();
            previewImages.add(getIntent().getStringExtra(EXTRA_PHOTO_PATH));
        }

        int currentPosition = getIntent().getIntExtra(EXTRA_CURRENT_POSITION, 0);

        mPhotoPageAdapter = new BGAPhotoPageAdapter(this, this, previewImages);
        mContentHvp.setAdapter(mPhotoPageAdapter);
        mContentHvp.setCurrentItem(currentPosition);

        mToolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                hiddenTitlebar();
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_pp_menu_photo_preview, menu);
        MenuItem menuItem = menu.findItem(R.id.item_photo_preview_title);
        View actionView = menuItem.getActionView();

        mTitleTv = actionView.findViewById(R.id.tv_photo_preview_title);
        mDownloadIv = actionView.findViewById(R.id.iv_photo_preview_download);
        mDownloadIv.setOnClickListener(this);

        if (mSaveImgDir == null) {
            mDownloadIv.setVisibility(View.INVISIBLE);
        }

        renderTitleTv();

        return true;
    }

    private void renderTitleTv() {
        if (mTitleTv == null || mPhotoPageAdapter == null) {
            return;
        }

        if (mIsSinglePreview) {
            mTitleTv.setText(R.string.bga_pp_view_photo);
        } else {
            mTitleTv.setText((mContentHvp.getCurrentItem() + 1) + "/" + mPhotoPageAdapter.getCount());
        }
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        if (System.currentTimeMillis() - mLastShowHiddenTime > 500) {
            mLastShowHiddenTime = System.currentTimeMillis();
            if (mIsHidden) {
                showTitlebar();
            } else {
                hiddenTitlebar();
            }
        }
    }

    private void showTitlebar() {
        if (mToolbar != null) {
            ViewCompat.animate(mToolbar).translationY(0).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(View view) {
                    mIsHidden = false;
                }
            }).start();
        }
    }

    private void hiddenTitlebar() {
        if (mToolbar != null) {
            ViewCompat.animate(mToolbar).translationY(-mToolbar.getHeight()).setInterpolator(new DecelerateInterpolator(2)).setListener(new ViewPropertyAnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(View view) {
                    mIsHidden = true;
                }
            }).start();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_photo_preview_download) {
            if (mSavePhotoTask == null) {
                savePic();
            }
        }
    }

    private synchronized void savePic() {
        if (mSavePhotoTask != null) {
            return;
        }

        final String url = mPhotoPageAdapter.getItem(mContentHvp.getCurrentItem());
        File file;
        if (url.startsWith("file")) {
            file = new File(url.replace("file://", ""));
            if (file.exists()) {
                BGAPhotoPickerUtil.showSafe(BGAPhotoPreviewActivity.this, getString(R.string.bga_pp_save_img_success_folder, file.getParentFile().getAbsolutePath()));
                return;
            }
        }

        file = new File(mSaveImgDir, BGAPhotoPickerUtil.md5(url) + ".png");
        if (file.exists()) {
            BGAPhotoPickerUtil.showSafe(this, getString(R.string.bga_pp_save_img_success_folder, mSaveImgDir.getAbsolutePath()));
            return;
        }

        mSavePhotoTask = new BGASavePhotoTask(this, this, file);
        BGAImage.downloadImage(this, url, new BGAImageLoader.DownloadDelegate() {
            @Override
            public void onSuccess(String url, Bitmap bitmap) {
                mSavePhotoTask.setBitmapAndPerform(bitmap);
            }

            @Override
            public void onFailed(String url) {
                mSavePhotoTask = null;
                BGAPhotoPickerUtil.show(BGAPhotoPreviewActivity.this, R.string.bga_pp_save_img_failure);
            }
        });
    }

    @Override
    public void onPostExecute(Void aVoid) {
        mSavePhotoTask = null;
    }

    @Override
    public void onTaskCancelled() {
        mSavePhotoTask = null;
    }

    @Override
    protected void onDestroy() {
        if (mSavePhotoTask != null) {
            mSavePhotoTask.cancelTask();
            mSavePhotoTask = null;
        }

        mTitleTv = null;
        mDownloadIv = null;
        mContentHvp = null;
        mPhotoPageAdapter = null;
        mSaveImgDir = null;

        super.onDestroy();
    }
}