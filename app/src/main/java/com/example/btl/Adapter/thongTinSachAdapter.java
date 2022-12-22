package com.example.btl.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.btl.Models.thongTinSach;
import com.example.btl.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public  class thongTinSachAdapter extends BaseAdapter implements Filterable {
    Context context;
    int layout;
    List<thongTinSach> arrSach, arrSachOld;

    public thongTinSachAdapter(Context context, int layout, List<thongTinSach> arrSach) {
        this.context = context;
        this.layout = layout;
        this.arrSach = arrSach;
        this.arrSachOld = arrSach;
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String search = charSequence.toString().trim();
                if (!search.isEmpty()){
                    List<thongTinSach> list = new ArrayList<>();
                    for (thongTinSach tt : arrSachOld){
                        if (tt.getTenSach().toLowerCase().contains(search.toLowerCase())
                        || tt.getTacGia().toLowerCase().contains(search.toLowerCase())
                        || tt.getTheLoai().toLowerCase().contains(search.toLowerCase())){
                            list.add(tt);
                        }
                    }
                    arrSach = list;
                }else {
                    arrSach = arrSachOld;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = arrSach;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                arrSach = (List<thongTinSach>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}