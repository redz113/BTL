package com.example.btl.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.btl.Models.thongTinSach;
import com.example.btl.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public  class thongTinSachAdapter extends BaseAdapter {
    Context context;
    int layout;
    List<thongTinSach> arrSach;

    public thongTinSachAdapter(Context context, int layout, List<thongTinSach> arrSach) {
        this.context = context;
        this.layout = layout;
        this.arrSach = arrSach;
    }

    @Override
    public int getCount() {
        return arrSach.size();
    }

    @Override
    public Object getItem(int i) {
        return arrSach.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private  class ViewHolder{
        TextView tenSach, tacGia, theLoai, soLuong;
        ImageView imgSach;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rView = inflater.inflate(this.layout, null);

        TextView tenSach = rView.findViewById(R.id.tvTenSach);
        TextView tacGia = rView.findViewById(R.id.tvTenTG);
        TextView theLoai = rView.findViewById(R.id.tvTheLoai);
        TextView soLuong = rView.findViewById(R.id.tvSL);
        ImageView imgSach = rView.findViewById(R.id.imgSP);

        thongTinSach tt = (thongTinSach) getItem(i);
        //Gán
        tenSach.setText(tt.getTenSach());
        tacGia.setText(tt.getTacGia());
        theLoai.setText(tt.getTheLoai());
        soLuong.setText("SL: " + tt.getSoLuong());
        if (tt.getSoLuong() == 0){
            soLuong.setTextColor(Color.parseColor("#FF1744"));
        };
        Picasso.with(this.context).load(tt.getPath()).into(imgSach);
        return rView;
    }
}