package actionbar.com.huway.actionbardemo;

import android.support.v4.app.Fragment;

/**
 * Created by ChenZhining on 15/12/2.
 */
public class PhotoGalleryActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new PhotoGalleryFragment();
    }
}
