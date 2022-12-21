package com.example.btl.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.btl.Models.phieuMuon;
import com.example.btl.R;

import java.util.List;

public class phieuMuonAdapter extends BaseAdapter {
    Context context;
    int resource;
    List<phieuMuon> listPM;

    public phieuMuonAdapter(Context context, int resource, List<phieuMuon> listPM) {
        this.context = context;
        this.resource = resource;
        this.listPM = listPM;
    }

    @Override
    public int getCount() {
        return listPM.size();
    }

    @Override
    public Object getItem(int i) {
        return listPM.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(this.resource, null);
        TextView tvMaPhieu      = customView.findViewById(R.id.tvMaPhieu);
        TextView tvNguoiMuon    = customView.findViewById(R.id.tvNguoiMuon);
        TextView tvSDT          = customView.findViewById(R.id.tvSDT);
        TextView tvTenSach      = customView.findViewById(R.id.tvTenSach);
        TextView tvTrangThai    = customView.findViewById(R.id.tvTrangThai);
        TextView tvNgayMuon     = customView.findViewById(R.id.tvNgayMuon);
        TextView tvNgayTra      = customView.findViewById(R.id.tvNgayTra);

        phieuMuon p = (phieuMuon) getItem(position);
        tvMaPhieu.setText(p.getId());
        tvNguoiMuon.setText(p.getNguoiMuon());
        tvSDT.setText(p.getSDT());
        tvTenSach.setText(p.getTenSachMuon());
        tvNgayMuon.setText(p.getNgayMuon());

        if (p.getTrangThai() == 1){
            tvTrangThai.setText("Đã trả sách");
            tvTrangThai.setTextColor(Color.parseColor("#3D5AFE"));
            tvNgayTra.setText(p.getNgayTra() + "");
        }

        return customView;
    }
}
