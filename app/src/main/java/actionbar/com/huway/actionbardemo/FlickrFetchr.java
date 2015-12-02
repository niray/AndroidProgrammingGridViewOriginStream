package actionbar.com.huway.actionbardemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ChenZhining on 15/12/2.
 */
public class FlickrFetchr {

    byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public ArrayList<GalleryItem> fetchItems() {
        ArrayList<GalleryItem> items = new ArrayList<>();
        String url = "http://www.duitang.com/category/avatar/?page=1&_type";
        try {
            String xmlString = getUrl(url);
            parseItem(items, xmlString);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    void parseItem(ArrayList<GalleryItem> items, String result) throws JSONException {

        JSONObject jo = new JSONObject(result);
        JSONObject joData = jo.getJSONObject("data");
        JSONArray jaBlog = joData.getJSONArray("blogs");

        for (int i = 0; i < jaBlog.length(); i++) {
            JSONObject blog = jaBlog.getJSONObject(i);
            String url = blog.getString("isrc");
            String id = blog.getString("photo_id");
            GalleryItem item = new GalleryItem();
            item.setImgUrl(url);
            item.setId(id);
            items.add(item);
        }
    }

}
