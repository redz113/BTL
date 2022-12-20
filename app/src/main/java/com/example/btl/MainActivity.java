package com.example.btl;

import static android.provider.MediaStore.Images.Media.getBitmap;

import static com.example.btl.R.layout.dialog_sua_xoa_sach;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl.Adapter.thongTinSachAdapter;
import com.example.btl.Models.thongTinSach;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        addViews();
        listSach    = new ArrayList<>();

        adapter = new thongTinSachAdapter(MainActivity.this, R.layout.thong_tin_sach, listSach);
        lvSach.setAdapter(adapter);

        lvSach.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                thongTinSach sach = listSach.get(i);
                //Toast.makeText(MainActivity.this, sach.getMoTa(), Toast.LENGTH_SHORT).show();
                showSuaXoa(sach);
            }
        });

        LoadData();
    }

    public void addViews(){
        lvSach      = findViewById(R.id.lvSach);
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
            case R.id.search:
                showSeachDialog();
//            case R.id.btnInfoED:
//                showSuaXoa();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSuaXoa(thongTinSach sach) {
        Dialog dialogSuaXoa = new Dialog(MainActivity.this);
        dialogSuaXoa.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialogSuaXoa.setContentView(dialog_sua_xoa_sach);
        dialogSuaXoa.setCanceledOnTouchOutside(false);

        //Ánh xạ
        TextView tvTenSachED, tvViTriED, tvMoTaED;
        Button btnSua, btnXoa, btnTroLai;

        tvTenSachED = dialogSuaXoa.findViewById(R.id.tvTenSachED);
        tvMoTaED    = dialogSuaXoa.findViewById(R.id.tvMoTaED);
        tvViTriED   = dialogSuaXoa.findViewById(R.id.tvViTriED);
        btnSua      = dialogSuaXoa.findViewById(R.id.btnSua);
        btnXoa      = dialogSuaXoa.findViewById(R.id.btnXoa);
        btnTroLai      = dialogSuaXoa.findViewById(R.id.btnTroLai);

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

        btnTroLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSuaXoa.dismiss();
            }
        });
        dialogSuaXoa.show();
    }

    private void showSuaDialog(thongTinSach sach) {
        Dialog dialogSua = new Dialog(MainActivity.this);
        dialogSua.requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);
        dialogSua.setContentView(R.layout.dialog_sua_sach);
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

                    if (tenSach.length() > 0 && theLoai.length() > 0 && moTa.length() > 0
                            && tacGia.length() > 0 && soLuong > 0 ) {

                        thongTinSach sachUpdate = new thongTinSach(sach.getId(), tenSach, theLoai, tacGia, moTa, viTri, sach.getPath(), soLuong);
                        mData.child( "thongTinSach").child(sach.getId()).setValue(sachUpdate, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                try {
                                    if (error == null){
                                        LoadData();
                                        Toast.makeText(MainActivity.this, "Cập Nhập Thành Công", Toast.LENGTH_SHORT).show();
                                        dialogSua.dismiss();
                                    }
                                }catch (Exception e){
                                    Toast.makeText(MainActivity.this, "Cập Nhập Thất Bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
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
//                                    Log.d("AAA", path);

                                    try {
                                        String tenSach      = edtTenSach.getText().toString().trim();
                                        String theLoai      = edtTheLoai.getText().toString().trim();
                                        String tacGia       = edtTG.getText().toString().trim();
                                        String moTa         = edtMoTa.getText().toString().trim();
                                        String viTri        = edtViTri.getText().toString().trim();
                                        int soLuong         = Integer.parseInt(edtSL.getText().toString());

                                        if (tenSach.length() > 0 && theLoai.length() > 0 && moTa.length() > 0
                                                && tacGia.length() > 0 && path.length() > 0 && soLuong > 0 ) {

                                            thongTinSach sach = new thongTinSach(getTime, tenSach, theLoai, tacGia, moTa, viTri, path, soLuong);
                                            mData.child( "thongTinSach").child(getTime).setValue(sach, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                    try {
                                                        if (error == null){
                                                            Toast.makeText(MainActivity.this, "Thêm Thành Công", Toast.LENGTH_SHORT).show();
                                                            dialogThemSach.dismiss();
                                                        }
                                                    }catch (Exception e){
                                                        Toast.makeText(MainActivity.this, "Thêm Thất Bại", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }catch (Exception e){
                                        Toast.makeText(MainActivity.this, "Error: Thông Tin Không Hợp Lệ", Toast.LENGTH_SHORT).show();
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