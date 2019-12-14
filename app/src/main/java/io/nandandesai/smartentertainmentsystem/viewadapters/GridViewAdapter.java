package io.nandandesai.smartentertainmentsystem.viewadapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;

import io.nandandesai.smartentertainmentsystem.R;
import io.nandandesai.smartentertainmentsystem.utils.ContentFetcher;
import io.nandandesai.smartentertainmentsystem.utils.ImageItem;
import io.nandandesai.smartentertainmentsystem.utils.Settings;
import okhttp3.OkHttpClient;

public class GridViewAdapter extends ArrayAdapter {
    private static final String TAG = "GridViewAdapter";
    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItem> imageItems = new ArrayList();
    private Settings settings;
    public GridViewAdapter(Context context, int layoutResourceId, ArrayList imageItems) {
        super(context, layoutResourceId, imageItems);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.imageItems = imageItems;
        this.settings=new Settings(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
            To understand what's going on in this method, watch: https://www.youtube.com/watch?v=N6YdwzAvwOA
        */

        View row = convertView;
        ViewHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ImageItem item = imageItems.get(position);
        holder.image.setAdjustViewBounds(true);
        //holder.image.setImageBitmap(item.getImage());


        if(settings.getUseProxyForYTS()) {
            Log.d(TAG, "getView: using proxy for Picasso image loading");
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(settings.getTorProxyAddress(), settings.getTorProxyPort()));
            OkHttpClient client= ContentFetcher.getUnsafeOkHttpClient(proxy);
            Picasso picasso = new Picasso.Builder(context)
                    .downloader(new OkHttp3Downloader(client))
                    .build();
            picasso.load(item.getImageURL().toString()).into(holder.image, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }else {
            Log.d(TAG, "getView: not using proxy for picasso image loading");
            Picasso.get().load(item.getImageURL().toString()).into(holder.image, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return row;
    }


    /*
        To know why we created this class, skip to 10:00 on the timeline of the aforementioned Youtube video
    */
    static class ViewHolder {
        ImageView image;
    }
}
