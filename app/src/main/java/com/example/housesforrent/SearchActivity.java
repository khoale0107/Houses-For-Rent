package com.example.housesforrent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.housesforrent.Adapters.Post;
import com.example.housesforrent.Adapters.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    RangeSlider discreteRangeSlider;
    TextView tvPrice;
    EditText etThanhPho;
    EditText etQuan;
    Button btnSearch;

    Dialog dialogSelectCity;
    Dialog dialogSelectDistrict;

    RecyclerView rvRV;
    PostAdapter postAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    List<String> bookmarksList;

    int fromPrice = 0;
    int toPrice = 10000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tìm trọ");
        initDialogThanhPho();
        loadBookMarks();

        discreteRangeSlider = findViewById(R.id.discreteRangeSlider);
        tvPrice = findViewById(R.id.tvPrice);
        tvPrice.setText(fromPrice + " - " + toPrice + " đồng");

        etThanhPho = findViewById(R.id.etThanhPho);
        etQuan = findViewById(R.id.etQuan);
        btnSearch = findViewById(R.id.btnSearch);

        rvRV = findViewById(R.id.rvRV);
        rvRV.setLayoutManager(new LinearLayoutManager(this));


        discreteRangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                fromPrice = Math.round(slider.getValues().get(0));
                toPrice = Math.round(slider.getValues().get(1));

                String value1 = String.format("%,d", fromPrice);
                String value2 = String.format("%,d", toPrice);

                tvPrice.setText(value1 + " - " + value2 + " đồng");
            }
        });

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

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAndLoad();
            }
        });
    }

    private void loadBookMarks() {
        //load bookmarks
        db.collection("users").document(user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                bookmarksList = (List<String>) document.get("bookmarks");

                                postAdapter = new PostAdapter(new ArrayList<>(), bookmarksList);
                                rvRV.setAdapter(postAdapter);
                            } else {

                            }
                        } else {

                        }
                    }
                });
    }

    //############################# Search ##############################################
    private void searchAndLoad() {
        db.collection("posts")
                .whereEqualTo("trangthai", 1)
//                .whereGreaterThanOrEqualTo("gia", fromPrice)
//                .whereLessThanOrEqualTo("gia", 10000000)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Post post = new Post(
                                        doc.getId(),
                                        doc.getString("tieude"),
                                        String.format("%,d", doc.getLong("gia")),
                                        doc.getLong("dientich").toString(),
                                        doc.getString("quan") + ", " + doc.getString("thanhpho"),
                                        doc.getString("owner")
                                );

                                post.setBookMarked(bookmarksList.contains(doc.getId()));

                                postAdapter.postList.add(0, post);
//                                postAdapter.notifyItemInserted(0);
                            }
                            postAdapter.notifyDataSetChanged();
                        } else {

                        }
                    }
                });
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

    private void showSelectDistrictDialog() {
        if (etThanhPho.getText().toString().isEmpty()) {
            Toast.makeText(SearchActivity.this, "Hãy chọn thành phố", Toast.LENGTH_SHORT).show();
            return;
        }
        dialogSelectDistrict.show();
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean enterFocus) {
            if (enterFocus) {
                if (view.getId() == R.id.etThanhPho) dialogSelectCity.show();
                if (view.getId() == R.id.etQuan) showSelectDistrictDialog();
            }
        }
    };

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search_activity, menu);
//
//        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search_activity_icon).getActionView();
//        searchView.setIconified(false);
//        searchView.setQueryHint("Tìm kiếm");
//
//        searchView.setOnQueryTextListener(onQueryTextListener);
//
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Toast.makeText(SearchActivity.this, s, Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };
}