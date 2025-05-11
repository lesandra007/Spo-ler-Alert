package edu.sjsu.sase.android.spoleralert.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import edu.sjsu.sase.android.spoleralert.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private int[] imageIds;
    private final String[] avatarNames;
    private final LayoutInflater inflater;

    public ImageAdapter(Context context, int[] ids, String[] avatarNames) {
        mContext = context;
        imageIds = ids;
        this.avatarNames = avatarNames;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageIds.length;
    }

    @Override
    public Object getItem(int position) {
        return imageIds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView avatarImage;
        TextView avatarName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_avatar, parent, false);
            holder = new ViewHolder();
            holder.avatarImage = convertView.findViewById(R.id.avatarImage);
            holder.avatarName = convertView.findViewById(R.id.avatarName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.avatarImage.setImageResource(imageIds[position]);
        holder.avatarName.setText(avatarNames[position]);

        return convertView;
    }
}
