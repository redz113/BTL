package com.example.btl.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.btl.Models.thongTinSach;
import com.example.btl.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class thongTinSachAdapter extends ArrayAdapter<thongTinSach> {
    Activity context;
    int resource;

    public thongTinSachAdapter(Activity context, int resource){
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View custumView = inflater.inflate(this.resource, null);

        TextView tenSach = custumView.findViewById(R.id.tvTenSach);
        TextView theLoai = custumView.findViewById(R.id.tvTheLoai);
        TextView tenTacGia = custumView.findViewById(R.id.tvTenTG);
        TextView soLuong = custumView.findViewById(R.id.tvSL);
        ImageView imgSach = custumView.findViewById(R.id.imgSP);

        thongTinSach tt = getItem(position);

        tenSach.setText(tt.getTenSach());
        theLoai.setText(tt.getTheLoai());
        tenTacGia.setText(tt.getTacGia());
        soLuong.setText("SL: " + tt.getSoLuong());
        imgSach.setImageURI(Uri.parse(tt.getPath()));
//        Picasso.with(this.context).load(tt.getPath()).into(imgSach);

        return custumView;
    }
}
