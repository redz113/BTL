package com.example.btl.Adapter;

import android.content.Context;
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
        View rView = view;
        ViewHolder holder = new ViewHolder();

        if (rView == null){
            rView = inflater.inflate(layout, null);
            holder.tenSach = rView.findViewById(R.id.tvTenSach);
            holder.tacGia = rView.findViewById(R.id.tvTenTG);
            holder.theLoai = rView.findViewById(R.id.tvTheLoai);
            holder.soLuong = rView.findViewById(R.id.tvSL);
            holder.imgSach = rView.findViewById(R.id.imgSP);
            rView.setTag(holder);
        }else {
            holder = (ViewHolder) rView.getTag();
        }

        //Gán
        holder.tenSach.setText(arrSach.get(i).getTenSach());
        holder.tacGia.setText(arrSach.get(i).getTacGia());
        holder.theLoai.setText(arrSach.get(i).getTheLoai());
        holder.soLuong.setText("SL: " + arrSach.get(i).getSoLuong());
//        holder.imgSach.setImageURI(Uri.parse(arrSach.get(i).getPath()));
        Picasso.with(this.context).load(arrSach.get(i).getPath()).into(holder.imgSach);
        return rView;
    }
}