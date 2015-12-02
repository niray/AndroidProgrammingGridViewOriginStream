package actionbar.com.huway.actionbardemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.LruCache;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ChenZhining on 15/12/2.
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = ThumbnailDownloader.class.getSimpleName();

    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_CACHING = 1;

    Handler mHandler;

    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandle;
    Listener<Token> mListener;
    LruCache<String, Bitmap> mBitmapCache;
    Map<String, String> requestMapCache = Collections.synchronizedMap(new HashMap<String, String>());

    public ThumbnailDownloader(Handler responseHandler, LruCache<String, Bitmap> bitmapCache) {
        super(TAG);
        mResponseHandle = responseHandler;
        mBitmapCache = bitmapCache;
    }

    public void queueThumbnailCache(String id, String url) {
        requestMapCache.put(id, url);
        mHandler.obtainMessage(MESSAGE_CACHING, id).sendToTarget();
    }

    public void setmListener(Listener<Token> listener) {
        mListener = listener;
    }

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    Token token = (Token) msg.obj;
                    handleRequest(token);
                }
            }
        };

    }

    public void queueThumbnail(Token token, String url) {
        requestMap.put(token, url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();

    }

    private void handleRequest(final Token token) {

        final String url = requestMap.get(token);
        if (url == null) return;
        final Bitmap bitmap;
        try {
            if (mBitmapCache.get(url) != null) {
                bitmap = mBitmapCache.get(url);
            } else {
                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                mBitmapCache.put(url, bitmap);
            }

            mResponseHandle.post(new Runnable() {
                @Override
                public void run() {
                    if (requestMap.get(token) != url) {
                        return;
                    }
                    requestMap.remove(token);

                    if (mListener != null) {
                        mListener.onThumbnailDownLoaded(token, bitmap);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public interface Listener<Token> {
        void onThumbnailDownLoaded(Token token, Bitmap bitmap);
    }
}
