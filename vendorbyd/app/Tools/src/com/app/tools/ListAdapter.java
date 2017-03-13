package com.app.tools;

import java.util.ArrayList;
import java.util.List;

import com.app.tools.R;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/*
*
*    Date             Author         CR/PR ID             Headline
*
*
*
*/
public class ListAdapter extends BaseAdapter {
    List<Program> list = new ArrayList<Program>();
    LayoutInflater la;
    Context context;
    PackageManager pm;

    public ListAdapter(List<Program> list ,Context context) {
        this.list = list;
        this.context = context;
    }
    public int getCount() {
        return list.size();
    }
    public Object getItem(int position) {
        return list.get(position);
    }
    public long getItemId(int position) {
        return position;
    }
    public String getPackageName(int position) {
        String packageName = pm.getNameForUid(position);
        return packageName;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            la = LayoutInflater.from(context);
            convertView=la.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.imgage=(ImageView) convertView.findViewById(R.id.image);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Program pr = (Program)list.get(position);
        //get icon
        holder.imgage.setImageDrawable(pr.getIcon());
        //get program name
        holder.text.setText(pr.getName());
        return convertView;
    }
}
class ViewHolder {
    TextView text;
    ImageView imgage;
}
