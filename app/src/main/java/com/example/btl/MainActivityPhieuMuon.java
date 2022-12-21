package com.example.btl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.btl.Adapter.phieuMuonAdapter;
import com.example.btl.Models.phieuMuon;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivityPhieuMuon extends AppCompatActivity{
    int layoutDuyet = 0;
    List<phieuMuon> listPM;
    ListView lvPM;
    phieuMuonAdapter adapter;

    DatabaseReference mData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_phieu_muon);

        mData = FirebaseDatabase.getInstance().getReference();
        lvPM = findViewById(R.id.lvPhieuMuon);
        listPM = new ArrayList<phieuMuon>();
        adapter = new phieuMuonAdapter(this, R.layout.thong_tin_phieu_muon, listPM);
        lvPM.setAdapter(adapter);


        lvPM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                phieuMuon p = listPM.get(i);
                showDialogDuyet(p);
            }
        });

        lvPM.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                phieuMuon p = listPM.get(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityPhieuMuon.this);
                builder.setTitle("Xóa phiếu mượn?");
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (p.getTrangThai() == 0){
                            Toast.makeText(MainActivityPhieuMuon.this, "Không thể xóa do phiếu mượn chưa được duyệt", Toast.LENGTH_SHORT).show();
                        }else {
                            mData.child("phieuMuon").child(p.getId()).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error == null){
                                        LoadData();
                                        Toast.makeText(MainActivityPhieuMuon.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });

                builder.create().show();
                return true;
            }
        });

        LoadData();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDialogDuyet(phieuMuon pm) {
        Dialog dialog = new Dialog(MainActivityPhieuMuon.this);
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.dialog_duyet_phieu_muon);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);

        // Ánh xạ
        Button btnDuyet, btnHuyDuyet;
        ImageButton btnClose;
        EditText maPhieu, nguoiTra, ngayMuon, ngayTra, tenSach, viTri;

        maPhieu     = dialog.findViewById(R.id.edtMaPhieu);
        nguoiTra    = dialog.findViewById(R.id.edtNguoiTra);
        ngayMuon    = dialog.findViewById(R.id.edtNgayMuon);
        ngayTra     = dialog.findViewById(R.id.edtNgayTra);
        tenSach     = dialog.findViewById(R.id.edtTenSach);
        viTri       = dialog.findViewById(R.id.edtViTri);
        btnClose    = dialog.findViewById(R.id.btnClose);
        btnDuyet    = dialog.findViewById(R.id.btnDuyet);
        btnHuyDuyet = dialog.findViewById(R.id.btnHuyDuyet);


        //set value
        String date = LocalDate.now().toString();
        ngayTra.setText(date);
        tenSach.setText(pm.getTenSachMuon());
        maPhieu.setText(pm.getId());
        nguoiTra.setText(pm.getNguoiMuon());
        ngayMuon.setText(pm.getNgayMuon());

        mData.child("thongTinSach").child(pm.getIdSach()).child("viTri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String vt = snapshot.getValue(String.class);
                viTri.setText(vt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (pm.getTrangThai() == 1){
            setDuyet(btnDuyet, btnHuyDuyet);
        }



        btnDuyet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pm.getTrangThai() == 0){
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("trangThai", 1);
                    map.put("ngayTra", date);
                    mData.child("phieuMuon").child(pm.getId()).updateChildren(map);

                    mData.child("thongTinSach").child(pm.getIdSach()).child("soLuong").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int sl = snapshot.getValue(int.class);
                            mData.child("thongTinSach").child(pm.getIdSach()).child("soLuong").setValue(sl + 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if (layoutDuyet == 0){
                        LoadData();
                    }else if (layoutDuyet == 1){
                        showPhieuDaDuyet();
                    }else  if (layoutDuyet == -1){
                        showPhieuChuaDuyet();
                    }
                    setDuyet(btnDuyet, btnHuyDuyet);
                    Toast.makeText(MainActivityPhieuMuon.this, "Duyệt thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnHuyDuyet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("trangThai", 0);
                map.put("ngayTra", "null");

                mData.child("thongTinSach").child(pm.getIdSach()).child("soLuong").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int sl = snapshot.getValue(int.class);
                        mData.child("thongTinSach").child(pm.getIdSach()).child("soLuong").setValue(sl - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                mData.child("phieuMuon").child(pm.getId()).updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null){

                            if (layoutDuyet == 0){
                                LoadData();
                            }else if (layoutDuyet == 1){
                                showPhieuDaDuyet();
                            }else  if (layoutDuyet == -1){
                                showPhieuChuaDuyet();
                            }
                            setHuyDuyet(btnDuyet, btnHuyDuyet);
                            Toast.makeText(MainActivityPhieuMuon.this, "Hủy duyệt thành công", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void setHuyDuyet(Button btnDuyet, Button btnHuyDuyet) {
        btnDuyet.setEnabled(true);
        btnDuyet.setText("duyệt");
        btnDuyet.setBackgroundColor(Color.parseColor("#0091EA"));


        btnHuyDuyet.setEnabled(false);
        btnHuyDuyet.setText("");
    }

    private void setDuyet(Button btnDuyet, Button btnHuyDuyet) {
        btnDuyet.setEnabled(false);
        btnDuyet.setText("");
        btnDuyet.setBackgroundColor(Color.parseColor("#00C853"));


        btnHuyDuyet.setEnabled(true);
        btnHuyDuyet.setText("Hủy duyệt");
    }


    private void LoadData() {
        layoutDuyet = 0;
        listPM.clear();
        mData.child("phieuMuon").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                phieuMuon p = snapshot.getValue(phieuMuon.class);
                listPM.add(p);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_phieu_muon, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                showSearch();
                break;
            case R.id.QLS:
                showQLS();
                break;
            case R.id.allPhieuDuyet:
                LoadData();
                break;
            case R.id.phieuDaDuyet:
                showPhieuDaDuyet();
                break;
            case R.id.phieuChuaDuyet:
                showPhieuChuaDuyet();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPhieuChuaDuyet() {
        layoutDuyet = -1;
        listPM.clear();
        Query query = mData.child("phieuMuon").orderByChild("trangThai").equalTo(0);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                phieuMuon p = snapshot.getValue(phieuMuon.class);
                listPM.add(p);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showPhieuDaDuyet() {
        layoutDuyet = 1;
        listPM.clear();
        Query query = mData.child("phieuMuon").orderByChild("trangThai").equalTo(1);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                phieuMuon p = snapshot.getValue(phieuMuon.class);
                listPM.add(p);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        Intent intent = new Intent(MainActivityPhieuMuon.this, MainActivityPhieuDaDuyet.class);
//        startActivity(intent);
    }

    private void showQLS() {
        Intent intent = new Intent(MainActivityPhieuMuon.this, MainActivity.class);
        startActivity(intent);
    }

    private void showSearch() {
    }
}