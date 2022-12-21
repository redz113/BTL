package com.example.btl;

import static android.provider.MediaStore.Images.Media.getBitmap;

import static com.example.btl.R.layout.dialog_sua_xoa_sach;
import static com.example.btl.R.layout.dialog_them_muon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl.Adapter.thongTinSachAdapter;
import com.example.btl.Models.phieuMuon;
import com.example.btl.Models.thongTinSach;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView lvSach;
    List<thongTinSach> listSach;
    thongTinSachAdapter adapter;

    ImageView imgSach;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mData       = FirebaseDatabase.getInstance().getReference();
        lvSach      = findViewById(R.id.lvSach);

        listSach    = new ArrayList<>();

        adapter = new thongTinSachAdapter(MainActivity.this, R.layout.thong_tin_sach, listSach);
        lvSach.setAdapter(adapter);

        lvSach.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                thongTinSach sach = listSach.get(i);
                showSuaXoa(sach);
            }
        });
        LoadData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sach, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.addSach:
                showThemSachDialog();
                break;
            case R.id.search:
                showSeachDialog();
                break;
            case R.id.QLPM:
                showQLPM();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showQLPM() {
        Intent intent = new Intent(MainActivity.this, MainActivityPhieuMuon.class);
        startActivity(intent);
    }

    private void showSuaXoa(thongTinSach sach) {
        Dialog dialogSuaXoa = new Dialog(MainActivity.this);
        dialogSuaXoa.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSuaXoa.setContentView(dialog_sua_xoa_sach);
        Window window = dialogSuaXoa.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogSuaXoa.setCanceledOnTouchOutside(false);

        //Ánh xạ
        TextView tvTenSachED, tvViTriED, tvMoTaED;
        Button btnSua, btnXoa, btnThemMuon ,btnTroLai;

        tvTenSachED = dialogSuaXoa.findViewById(R.id.tvTenSachED);
        tvMoTaED    = dialogSuaXoa.findViewById(R.id.tvMoTaED);
        tvViTriED   = dialogSuaXoa.findViewById(R.id.tvViTriED);
        btnSua      = dialogSuaXoa.findViewById(R.id.btnSua);
        btnXoa      = dialogSuaXoa.findViewById(R.id.btnXoa);
        btnThemMuon = dialogSuaXoa.findViewById(R.id.btnThemMuon);
        btnTroLai   = dialogSuaXoa.findViewById(R.id.btnTroLai);

        tvTenSachED.setText(sach.getTenSach());
        tvMoTaED.setText(sach.getMoTa());
        tvViTriED.setText(sach.getViTri() + "");

        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSuaDialog(sach);
                dialogSuaXoa.dismiss();
            }
        });

        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete");
                builder.setMessage("Ban thực sự muốn xóa?");
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StorageReference storageRef = storage.getReference("imagesSach/image" + sach.getId() + ".jpeg");
                        storageRef.delete();
                        mData.child("thongTinSach").child(sach.getId()).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(MainActivity.this, "Xóa Thành Công", Toast.LENGTH_SHORT).show();
                            }
                        });
                        LoadData();
                        dialogSuaXoa.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        btnThemMuon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (sach.getSoLuong() == 0){
                    Toast.makeText(MainActivity.this, "Sách đã mượn hết!", Toast.LENGTH_SHORT).show();
                }else {
                    showThemPhieuMuon(sach);
                    dialogSuaXoa.dismiss();
                }
            }
        });

        btnTroLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSuaXoa.dismiss();
            }
        });
        dialogSuaXoa.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showThemPhieuMuon(thongTinSach sach) {
        Dialog dialogThem = new Dialog(MainActivity.this);
        dialogThem.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogThem.setContentView(dialog_them_muon);
        Window window = dialogThem.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogThem.setCanceledOnTouchOutside(false);

        //Ánh xạ
        TextView tvTenSach;
        EditText edtNguoiMuon, edtSDT, edtNgayMuon, edtNgayTra, edtGhiChu;
        Button btnThem, btnHuy;

        tvTenSach       = dialogThem.findViewById(R.id.tvTenSach);
        edtNguoiMuon    = dialogThem.findViewById(R.id.edtNguoiMuon);
        edtSDT          = dialogThem.findViewById(R.id.edtSDT);
        edtNgayMuon     = dialogThem.findViewById(R.id.edtNgayMuon);
//        edtNgayTra      = dialogThem.findViewById(R.id.edtNgayTra);
        edtGhiChu       = dialogThem.findViewById(R.id.edtGhiChu);
        btnThem         = dialogThem.findViewById(R.id.btnThem);
        btnHuy          = dialogThem.findViewById(R.id.btnHuy);

        String date = LocalDate.now() + "";
        edtNgayMuon.setText(date);
        tvTenSach.setText(sach.getTenSach());

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar time = Calendar.getInstance();
                String id, tenSachMuon, nguoiMuon, SDT, ngayMuon, ngayTra, ghiChu;
                int trangThai = 0;

                id = time.getTimeInMillis() + "";
                tenSachMuon = tvTenSach.getText().toString().trim();
                nguoiMuon   = edtNguoiMuon.getText().toString().trim();
                SDT         = edtSDT.getText().toString().trim();
                ngayMuon    = edtNgayMuon.getText().toString().trim();
                ngayTra     = "null";
                ghiChu      = edtGhiChu.getText().toString().trim();

                if (nguoiMuon.length() > 0 && SDT.length() > 0
                        && ngayTra.length() > 0){
                    phieuMuon p = new phieuMuon(id, sach.getId(), tenSachMuon, nguoiMuon, SDT, ngayMuon, ngayTra, ghiChu, trangThai);
                    mData.child("phieuMuon").child(id).setValue(p);
                    int sl = sach.getSoLuong() - 1;
                    mData.child("thongTinSach").child(sach.getId()).child("soLuong").setValue(sl);
                    LoadData();
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    dialogThem.dismiss();
                }else {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogThem.dismiss();
            }
        });
        dialogThem.show();
    }

    private void showSuaDialog(thongTinSach sach) {
        Dialog dialogSua = new Dialog(MainActivity.this);
        dialogSua.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSua.setContentView(R.layout.dialog_sua_sach);
        Window window = dialogSua.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogSua.setCanceledOnTouchOutside(false);

        //Ánh Xạ
        EditText edtTenSach = dialogSua.findViewById(R.id.edtTenSach);
        EditText edtTG      = dialogSua.findViewById(R.id.edtTacGia);
        EditText edtTheLoai = dialogSua.findViewById(R.id.edtTheLoai);
        EditText edtSL      = dialogSua.findViewById(R.id.edtSoLuong);
        EditText edtMoTa    = dialogSua.findViewById(R.id.edtMoTa);
        EditText edtViTri   = dialogSua.findViewById(R.id.edtViTri);
        Button btnCapNhap      = dialogSua.findViewById(R.id.btnCapNhap);
        Button btnHuy       = dialogSua.findViewById(R.id.btnHuy);

        edtTenSach.setText(sach.getTenSach());
        edtTG.setText(sach.getTacGia());
        edtTheLoai.setText(sach.getTheLoai());
        edtMoTa.setText(sach.getMoTa());
        edtSL.setText(sach.getSoLuong() + "");
        edtViTri.setText(sach.getViTri());

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSua.dismiss();
            }
        });

        btnCapNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String tenSach      = edtTenSach.getText().toString().trim();
                    String theLoai      = edtTheLoai.getText().toString().trim();
                    String tacGia       = edtTG.getText().toString().trim();
                    String moTa         = edtMoTa.getText().toString().trim();
                    String viTri        = edtViTri.getText().toString().trim();
                    int soLuong         = Integer.parseInt(edtSL.getText().toString());

                        thongTinSach sachUpdate = new thongTinSach(sach.getId(), tenSach, theLoai, tacGia, moTa, viTri, sach.getPath(), soLuong);
                        mData.child( "thongTinSach").child(sach.getId()).setValue(sachUpdate, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error == null){
                                        LoadData();
                                        Toast.makeText(MainActivity.this, "Cập Nhập Thành Công", Toast.LENGTH_SHORT).show();
                                        dialogSua.dismiss();
                                    }else {
                                    Toast.makeText(MainActivity.this, "Cập Nhập Thất Bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "Error: Thông Tin Không Hợp Lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogSua.show();
    }

    private void showSeachDialog() {
    }

    private void showThemSachDialog() {
        Dialog dialogThemSach = new Dialog(this);
        dialogThemSach.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialogThemSach.setContentView(R.layout.dialog_them_sach);
        Window window = dialogThemSach.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogThemSach.setCanceledOnTouchOutside(false);

        mData = FirebaseDatabase.getInstance().getReference();
        StorageReference storageRef = storage.getReference();

//        Ánh xạ
        Button btnThem      = dialogThemSach.findViewById(R.id.btnThem);
        Button btnHuy       = dialogThemSach.findViewById(R.id.btnHuy);
        EditText edtTenSach = dialogThemSach.findViewById(R.id.edtTenSach);
        EditText edtTG      = dialogThemSach.findViewById(R.id.edtTacGia);
        EditText edtTheLoai = dialogThemSach.findViewById(R.id.edtTheLoai);
        EditText edtSL      = dialogThemSach.findViewById(R.id.edtSoLuong);
        EditText edtMoTa    = dialogThemSach.findViewById(R.id.edtMoTa);
        EditText edtViTri   = dialogThemSach.findViewById(R.id.edtViTri);
        imgSach             = dialogThemSach.findViewById(R.id.imgSach);

        imgSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xuLyLayAnh();
            }
        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Calendar time = Calendar.getInstance();
                    StorageReference mountainsRef = storageRef.child("imagesSach/image" + time.getTimeInMillis() + ".jpeg");

                    imgSach.setDrawingCacheEnabled(true);
                    imgSach.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imgSach.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(MainActivity.this, "Error: Tải Ảnh Thất Bại", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String getTime = time.getTimeInMillis() + "";
                                    String path = uri.toString();

                                    try {
                                        String tenSach      = edtTenSach.getText().toString().trim();
                                        String theLoai      = edtTheLoai.getText().toString().trim();
                                        String tacGia       = edtTG.getText().toString().trim();
                                        String moTa         = edtMoTa.getText().toString().trim();
                                        String viTri        = edtViTri.getText().toString().trim();
                                        int soLuong         = Integer.parseInt(edtSL.getText().toString());

                                            thongTinSach sach = new thongTinSach(getTime, tenSach, theLoai, tacGia, moTa, viTri, path, soLuong);
                                            mData.child( "thongTinSach").child(getTime).setValue(sach, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                    try {
                                                        if (error == null){
                                                            LoadData();
                                                            Toast.makeText(MainActivity.this, "Thêm Thành Công", Toast.LENGTH_SHORT).show();
                                                            dialogThemSach.dismiss();
                                                        }
                                                    }catch (Exception e){
                                                        Toast.makeText(MainActivity.this, "Thêm Thất Bại", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
//                                        }
                                    }catch (Exception e){
                                        Toast.makeText(MainActivity.this, "Error: Hãy nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "Error: Chọn Ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogThemSach.dismiss();
            }
        });

        dialogThemSach.show();
    }


    public void xuLyLayAnh() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Chọn ảnh"), 113);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 113 && resultCode == RESULT_OK && data != null){
            try {
                Bitmap bitmap = getBitmap(getContentResolver(), data.getData());
                imgSach.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void LoadData(){
        listSach.clear();
        mData.child("thongTinSach").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                thongTinSach tt = snapshot.getValue(thongTinSach.class);
                listSach.add(tt);
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
}