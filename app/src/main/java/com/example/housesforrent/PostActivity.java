package com.example.housesforrent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.housesforrent.Adapters.PostImageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {
    Button btnSelectImages;
    ImageView ivImage;
    RecyclerView rvRV;
    PostImageAdapter postImageAdapter;
    EditText etTieuDe;
    EditText etGia;
    EditText etMota;
    EditText etThanhPho;
    EditText etQuan;
    EditText etDienTich;

    Dialog dialogSelectCity;
    Dialog dialogSelectDistrict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cho thuê trọ");
        initDialogThanhPho();

        btnSelectImages = findViewById(R.id.btnSelectImages);
        rvRV = findViewById(R.id.rvRV);
        etTieuDe = findViewById(R.id.etTieuDe);
        etGia = findViewById(R.id.etGia);
        etMota = findViewById(R.id.etMota);
        etThanhPho = findViewById(R.id.etThanhPho);
        etQuan = findViewById(R.id.etQuan);
        etDienTich = findViewById(R.id.etDienTich);

        etThanhPho.setOnFocusChangeListener(onFocusChangeListener);
        etThanhPho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSelectCity.show();
            }
        });

        etQuan.setOnFocusChangeListener(onFocusChangeListener);
        etQuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectDistrictDialog();
            }
        });

        List<Uri> imageList = new ArrayList<>();
        postImageAdapter = new PostImageAdapter(new ArrayList<Uri>());
        rvRV.setAdapter(postImageAdapter);
        rvRV.setLayoutManager(new GridLayoutManager(this, 3));

        btnSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
                pickFromGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                pickFromGallery.setType("image/*");
                startActivityForResult(pickFromGallery, 1);
            }
        });
    }



//############################################################################################################################################

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean enterFocus) {
            if (enterFocus) {
                if (view.getId() == R.id.etThanhPho) dialogSelectCity.show();
                if (view.getId() == R.id.etQuan) showSelectDistrictDialog();
            }
        }
    };

    private void showSelectDistrictDialog() {
        if (etThanhPho.getText().toString().isEmpty()) {
            Toast.makeText(PostActivity.this, "Hãy chọn thành phố", Toast.LENGTH_SHORT).show();
            return;
        }
        dialogSelectDistrict.show();
    }

    //################################# CREATE SELECT CITY DIALOG #############################################
    private void initDialogThanhPho() {
        dialogSelectCity = new Dialog(this);
        dialogSelectCity.setContentView(R.layout.dialog_select_city);

        CompoundButton.OnCheckedChangeListener onSelectedPlaceListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    etThanhPho.setText(compoundButton.getText());
                    etThanhPho.clearFocus();
                    etThanhPho.setError(null);
                    etQuan.setText("");
                    dialogSelectCity.dismiss();

                    initDialogDistrict();
                }
            }
        };

        RadioButton rd1 = dialogSelectCity.findViewById(R.id.rd1);
        RadioButton rd2 = dialogSelectCity.findViewById(R.id.rd2);

        rd1.setOnCheckedChangeListener(onSelectedPlaceListener);
        rd2.setOnCheckedChangeListener(onSelectedPlaceListener);
    }

    //################################# CREATE SELECT DISTRICT DIALOG #############################################
    private void initDialogDistrict() {
        dialogSelectDistrict = new Dialog(this);

        String city = etThanhPho.getText().toString();

        if (city.equals("Hồ Chí Minh")) {
            dialogSelectDistrict.setContentView(R.layout.dialog_select_district_1);
        }
        else if (city.equals("Hà Nội")) {
            dialogSelectDistrict.setContentView(R.layout.dialog_select_district_2);
        }
        else {
            Toast.makeText(this, "Có lỗi 111", Toast.LENGTH_SHORT).show();
            return;
        }

        CompoundButton.OnCheckedChangeListener onSelectedDistrictListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    etQuan.setText(compoundButton.getText());
                    etQuan.clearFocus();
                    etQuan.setError(null);
                    dialogSelectDistrict.dismiss();
                }
            }
        };

        RadioButton rd1 = dialogSelectDistrict.findViewById(R.id.rd1);
        RadioButton rd2 = dialogSelectDistrict.findViewById(R.id.rd2);
        RadioButton rd3 = dialogSelectDistrict.findViewById(R.id.rd3);
        RadioButton rd4 = dialogSelectDistrict.findViewById(R.id.rd4);
        RadioButton rd5 = dialogSelectDistrict.findViewById(R.id.rd5);
        RadioButton rd6 = dialogSelectDistrict.findViewById(R.id.rd6);
        RadioButton rd7 = dialogSelectDistrict.findViewById(R.id.rd7);
        RadioButton rd8 = dialogSelectDistrict.findViewById(R.id.rd8);
        RadioButton rd9 = dialogSelectDistrict.findViewById(R.id.rd9);
        RadioButton rd10 = dialogSelectDistrict.findViewById(R.id.rd10);

        rd1.setOnCheckedChangeListener(onSelectedDistrictListener);
        rd2.setOnCheckedChangeListener(onSelectedDistrictListener);
        rd3.setOnCheckedChangeListener(onSelectedDistrictListener);
        rd4.setOnCheckedChangeListener(onSelectedDistrictListener);
        rd5.setOnCheckedChangeListener(onSelectedDistrictListener);
        rd6.setOnCheckedChangeListener(onSelectedDistrictListener);
        rd7.setOnCheckedChangeListener(onSelectedDistrictListener);
        rd8.setOnCheckedChangeListener(onSelectedDistrictListener);
        rd9.setOnCheckedChangeListener(onSelectedDistrictListener);
        rd10.setOnCheckedChangeListener(onSelectedDistrictListener);
    }

    //############################# OPTION MENU #####################################################################################################################
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //############################# OPTION MENU SELECTED #################################################
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add_new_post) {

            if (etTieuDe.getText().toString().isEmpty()) {
                etTieuDe.setError("Hãy nhập tiêu đề.");
                return false;
            }
            if (etThanhPho.getText().toString().isEmpty()) {
                etThanhPho.setError("Hãy nhập tiêu đề.");
                return false;
            }
            if (etQuan.getText().toString().isEmpty()) {
                etQuan.setError("Hãy nhập tiêu đề.");
                return false;
            }
            if (etGia.getText().toString().isEmpty()) {
                etGia.setError("Hãy nhập tiêu đề.");
                return false;
            }
            if (etDienTich.getText().toString().isEmpty()) {
                etDienTich.setError("Hãy nhập tiêu đề.");
                return false;
            }
            if (postImageAdapter.imagesList.isEmpty()) {
                Toast.makeText(this, "Hãy chọn ảnh", Toast.LENGTH_SHORT).show();
                return false;
            }

            insertNewPost();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && data != null) {
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    postImageAdapter.imagesList.add(imageUri);
                }
                postImageAdapter.notifyDataSetChanged();
            }
            else {
                Uri imageUri = data.getData();
                postImageAdapter.imagesList.add(imageUri);
                postImageAdapter.notifyItemInserted(postImageAdapter.imagesList.size());
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //######################################## UPLOAD NEW POST #####################################################################
    public void insertNewPost() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        List<Integer> imageList = new ArrayList<>();
        for (int i = 0; i < postImageAdapter.imagesList.size(); i++) {
            imageList.add(i);
        }

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("tieude", etTieuDe.getText().toString());
        newUser.put("gia", Long.parseLong(etGia.getText().toString()));
        newUser.put("mota", etMota.getText().toString());
        newUser.put("dientich", Long.parseLong(etDienTich.getText().toString()));
        newUser.put("owner", user.getEmail());
        newUser.put("thanhpho", etThanhPho.getText().toString());
        newUser.put("quan", etQuan.getText().toString());
        newUser.put("valid", 0);
        newUser.put("trangthai", 0);
        newUser.put("images", imageList);
        newUser.put("time", Timestamp.now());

        db.collection("posts")
            .add(newUser)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    for (int i = 0; i < postImageAdapter.imagesList.size(); i++) {
                        StorageReference imageRef = storageRef.child("post-resources/" + documentReference.getId() + "/" + i);
                        imageRef.putFile(postImageAdapter.imagesList.get(i));
                    }

                    finish();
                    Toast.makeText(PostActivity.this, "Đã thêm phòng trọ", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("", "Error adding document", e);
                }
            });


    }

}