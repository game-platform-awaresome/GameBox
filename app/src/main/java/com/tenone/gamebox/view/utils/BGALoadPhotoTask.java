package com.tenone.gamebox.view.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.tenone.gamebox.R;
import com.tenone.gamebox.mode.mode.BGAImageFolderModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class BGALoadPhotoTask extends BGAAsyncTask<Void, ArrayList<BGAImageFolderModel>> {
    private Context mContext;
    private boolean mTakePhotoEnabled;
    private boolean isGif= false;

    public BGALoadPhotoTask(Callback<ArrayList<BGAImageFolderModel>> callback, Context context, boolean takePhotoEnabled) {
        super( callback );
        mContext = context.getApplicationContext();
        mTakePhotoEnabled = takePhotoEnabled;
    }


    public void setGif(boolean gif) {
        isGif = gif;
    }

    private static boolean isImageFile(String path) {
        // 获取图片的宽和高，但不把图片加载到内存中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile( path, options );
        return options.outMimeType != null;
    }

    @Override
    protected ArrayList<BGAImageFolderModel> doInBackground(Void... voids) {
        ArrayList<BGAImageFolderModel> imageFolderModels = new ArrayList();
        BGAImageFolderModel allImageFolderModel = new BGAImageFolderModel( mTakePhotoEnabled );
        HashMap<String, BGAImageFolderModel> imageFolderModelMap = new HashMap<>();

        ArrayList<BGAImageFolderModel> gifFolderModels = new ArrayList();
        HashMap<String, BGAImageFolderModel> gifFolderModelMap = new HashMap<>();
        BGAImageFolderModel allGifImageFolderModel = new BGAImageFolderModel( mTakePhotoEnabled );
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA}, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC" );
            BGAImageFolderModel otherImageFolderModel;
            if (cursor != null && cursor.getCount() > 0) {
                boolean firstInto = true;
                while (cursor.moveToNext()) {
                    String imagePath = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.DATA ) );
                    int dot = imagePath.lastIndexOf( "." );
                    String type = imagePath.substring( dot, imagePath.length() );
                    if (".gif".equals( type )) {
                        if (!TextUtils.isEmpty( imagePath ) && isImageFile( imagePath )) {
                            if (firstInto) {
                                allGifImageFolderModel.name = mContext.getString( R.string.bga_pp_all_image );
                                allGifImageFolderModel.coverPath = imagePath;
                                firstInto = false;
                            }
                            // 所有图片目录每次都添加
                            allGifImageFolderModel.addLastImage( imagePath );
                            String folderPath = null;
                            // 其他图片目录
                            File folder = new File( imagePath ).getParentFile();
                            if (folder != null) {
                                folderPath = folder.getAbsolutePath();
                            }
                            if (TextUtils.isEmpty( folderPath )) {
                                int end = imagePath.lastIndexOf( File.separator );
                                if (end != -1) {
                                    folderPath = imagePath.substring( 0, end );
                                }
                            }
                            if (!TextUtils.isEmpty( folderPath )) {
                                if (gifFolderModelMap.containsKey( folderPath )) {
                                    otherImageFolderModel = gifFolderModelMap.get( folderPath );
                                } else {
                                    String folderName = folderPath.substring( folderPath.lastIndexOf( File.separator ) + 1 );
                                    if (TextUtils.isEmpty( folderName )) {
                                        folderName = "/";
                                    }
                                    otherImageFolderModel = new BGAImageFolderModel( folderName, imagePath );
                                    gifFolderModelMap.put( folderPath, otherImageFolderModel );
                                }
                                otherImageFolderModel.addLastImage( imagePath );
                            }
                        }

                        // 添加所有图片目录
                        gifFolderModels.add( allGifImageFolderModel );
                        Iterator<Map.Entry<String, BGAImageFolderModel>> iterator = gifFolderModelMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                           gifFolderModels.add( iterator.next().getValue() );
                        }
                    }else {
                        if (!TextUtils.isEmpty( imagePath ) && isImageFile( imagePath )) {
                            if (firstInto) {
                                allImageFolderModel.name = mContext.getString( R.string.bga_pp_all_image );
                                allImageFolderModel.coverPath = imagePath;
                                firstInto = false;
                            }
                            // 所有图片目录每次都添加
                            allImageFolderModel.addLastImage( imagePath );
                            String folderPath = null;
                            // 其他图片目录
                            File folder = new File( imagePath ).getParentFile();
                            if (folder != null) {
                                folderPath = folder.getAbsolutePath();
                            }
                            if (TextUtils.isEmpty( folderPath )) {
                                int end = imagePath.lastIndexOf( File.separator );
                                if (end != -1) {
                                    folderPath = imagePath.substring( 0, end );
                                }
                            }
                            if (!TextUtils.isEmpty( folderPath )) {
                                if (imageFolderModelMap.containsKey( folderPath )) {
                                    otherImageFolderModel = imageFolderModelMap.get( folderPath );
                                } else {
                                    String folderName = folderPath.substring( folderPath.lastIndexOf( File.separator ) + 1 );
                                    if (TextUtils.isEmpty( folderName )) {
                                        folderName = "/";
                                    }
                                    otherImageFolderModel = new BGAImageFolderModel( folderName, imagePath );
                                    imageFolderModelMap.put( folderPath, otherImageFolderModel );
                                }
                                otherImageFolderModel.addLastImage( imagePath );
                            }
                        }

                        imageFolderModels.add( allImageFolderModel );
                        Iterator<Map.Entry<String, BGAImageFolderModel>> iterator = imageFolderModelMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            imageFolderModels.add( iterator.next().getValue() );
                        }


                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (isGif){
            return gifFolderModels;
        }
        return imageFolderModels;
    }

    public BGALoadPhotoTask perform() {
        if (Build.VERSION.SDK_INT >= 11) {
            executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
        } else {
            execute();
        }
        return this;
    }
}
