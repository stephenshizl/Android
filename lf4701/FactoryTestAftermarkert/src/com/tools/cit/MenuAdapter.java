package com.tools.customercit;

import java.util.ArrayList;

import com.tools.customercit.BaseMenu.MenuData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MenuData> arrayMenu = new ArrayList<MenuData> ();
    

    public MenuAdapter(Context mContext, ArrayList<MenuData> menuData) {
        super();
        this.mContext = mContext;
        this.arrayMenu = menuData;
    }

    public int getCount() {
        return arrayMenu.size();
    }

    public MenuData getItem(int position) {
        return arrayMenu.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({ "InlinedApi", "ResourceAsColor" })
    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.listmenu, null);   
        TextView stateText = (TextView) layout.findViewById(R.id.state);
        TextView nameText = (TextView) layout.findViewById(R.id.name);
        MenuData menuData = arrayMenu.get(position);
        if(!menuData.getActivityString().equals("")) {//childmenu
            nameText.setText(menuData.getMenuName());
            nameText.setTextColor(0xffffffff);
            
        }
        else {//parent menu
            nameText.setText(menuData.getMenuName());
            nameText.setTextColor(0xffffffff); 
            //layout.setBackgroundColor(0xff00313e);
            nameText.getPaint().setFakeBoldText(true);
            nameText.setPadding(8, 0, 0, 0);
            layout.setBackgroundResource(R.drawable.parent_menu_bg);
        }
        
        int state = arrayMenu.get(position).getState();
        if (state == -1) {
            stateText.setText("");
            }
        else if (state == 0) {
            stateText.setText(R.string.failresult);
            stateText.setTextColor(0xffff0000);
        }
        else {
            stateText.setText(R.string.successresult);
            stateText.setTextColor(0xff00ff00);
        }
//        ImageView iv = (ImageView) layout.findViewById(R.id.phonepic);
//        iv.setImageResource(arrayPic.get(position));
        return layout;
    }
    
}
