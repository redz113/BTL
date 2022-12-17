package com.example.btl;

import static android.provider.MediaStore.Images.Media.getBitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
        lvSach      = findViewById(R.id.lvSach);
        listSach    = new ArrayList<>();

        adapter = new thongTinSachAdapter(MainActivity.this, R.layout.thong_tin_sach);
        lvSach.setAdapter(adapter);

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
            case R.id.search:
                showSeachDialog();
        }
        return super.onOptionsItemSelected(item);
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
                                    String path = uri.toString();
                                    Log.d("AAA", path);

                                    try {
                                        String tenSach      = edtTenSach.getText().toString().trim();
                                        String theLoai      = edtTheLoai.getText().toString().trim();
                                        String tacGia       = edtTG.getText().toString().trim();
                                        int soLuong         = Integer.parseInt(edtSL.getText().toString());

                                        if (tenSach.length() > 0 && theLoai.length() > 0 &&
                                                tacGia.length() > 0 && path.length() > 0 && soLuong > 0 ) {

                                            thongTinSach sach = new thongTinSach(tenSach, theLoai, tacGia, path, soLuong);
                                            mData.child("thongTinSach").push().setValue(sach, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                    try {
                                                        if (error == null){
                                                            Toast.makeText(MainActivity.this, "Thêm Thành Công", Toast.LENGTH_SHORT).show();
                                                            dialogThemSach.dismiss();
//                                                            LoadData();
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
        mData.child("thongTinSach").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                thongTinSach tt = snapshot.getValue(thongTinSach.class);
                Toast.makeText(MainActivity.this, tt.toString(), Toast.LENGTH_SHORT).show();
                listSach.add(tt);
//                Toast.makeText(MainActivity.this, "112", Toast.LENGTH_SHORT).show();
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