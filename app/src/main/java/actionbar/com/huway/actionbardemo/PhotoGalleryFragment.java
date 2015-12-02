package actionbar.com.huway.actionbardemo;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by ChenZhining on 15/12/2.
 */
public class PhotoGalleryFragment extends Fragment {
    public static final String TAG = PhotoGalleryFragment.class.getSimpleName();
    ThumbnailDownloader mThumbnailThread;
    LruCache<String, Bitmap> mBitmapCache;
    private GridView mGridView;
    private ArrayList<GalleryItem> mItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
        int cacheSize = 4 * 1024 * 1024; // 4MB
        mBitmapCache = new LruCache<String, Bitmap>(cacheSize);
        mThumbnailThread = new ThumbnailDownloader<ImageView>(new Handler(), mBitmapCache);
        mThumbnailThread.start();
        mThumbnailThread.getLooper();
        mThumbnailThread.setmListener(new ThumbnailDownloader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownLoaded(ImageView o, Bitmap bitmap) {
                o.setImageBitmap(bitmap);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_photo_gallery, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView);

        setupAdapter();
        return v;
    }

    private void setupAdapter() {
        if (getActivity() == null || mGridView == null) {
            return;
        }
        if (mItems != null) {
            mGridView.setAdapter(new GalleryItemAdapter(mItems));
        } else {
            mGridView.setAdapter(null);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailThread.quit();
    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {

        public GalleryItemAdapter(ArrayList<GalleryItem> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
            }
            ImageView iv = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
            iv.setImageResource(R.drawable.background_tab);

            GalleryItem item = getItem(position);
            String url = item.getImgUrl();

            mThumbnailThread.queueThumbnail(iv, url);


            if (mItems.size() > 1) {
                int endPos = position - 10;
                if (endPos <= 0)
                    endPos = 0;
                if (endPos > 0) {
                    for (int i = position - 1; i >= endPos; i--) {
                        if (i < mItems.size()) {
                            url = mItems.get(i).getImgUrl();
                            String id = mItems.get(i).getId();
                            if (url != null) {
                                mThumbnailThread.queueThumbnailCache(id, url);
                            }
                        }
                    }
                }
                for (int i = position + 1; i <= position + 10; i++) {
                    if (i < mItems.size()) {
                        url = mItems.get(i).getImgUrl();
                        String id = mItems.get(i).getId();
                        if (url != null) {
                            mThumbnailThread.queueThumbnailCache(id, url);
                        }
                    }
                }
            }


            return convertView;
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {

            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }

}
