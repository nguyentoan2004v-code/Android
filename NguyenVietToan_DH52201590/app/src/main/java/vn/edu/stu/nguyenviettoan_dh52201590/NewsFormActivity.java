package vn.edu.stu.nguyenviettoan_dh52201590;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import vn.edu.stu.nguyenviettoan_dh52201590.Model.Category;

public class NewsFormActivity extends AppCompatActivity {
    private ImageView imgPreview;
    private EditText edtNewsTitle, edtAuthor, edtDate;
    private Spinner spinnerCategory;
    private Button btnChooseImage, btnSave, btnCancel;

    private DatabaseHelper dbHelper;
    private ArrayList<Category> categoryList;
    private ArrayAdapter<Category> categoryAdapter;
    private String imageBase64 = "";
    private int newsId = -1;
    private Calendar calendar;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_form);

        imgPreview = findViewById(R.id.imgPreview);
        edtNewsTitle = findViewById(R.id.edtNewsTitle);
        edtAuthor = findViewById(R.id.edtAuthor);
        edtDate = findViewById(R.id.edtDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();

        loadCategories();
        loadNewsData();

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNews();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadCategories() {
        categoryList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllCategories();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                categoryList.add(new Category(id, name));
            } while (cursor.moveToNext());
        }
        cursor.close();

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void loadNewsData() {
        Intent intent = getIntent();
        newsId = intent.getIntExtra("news_id", -1);

        if (newsId != -1) {
            getSupportActionBar().setTitle(R.string.edit_news);

            String title = intent.getStringExtra("news_title");
            int categoryId = intent.getIntExtra("news_category_id", 0);
            imageBase64 = intent.getStringExtra("news_image");
            String author = intent.getStringExtra("news_author");
            String date = intent.getStringExtra("news_date");

            edtNewsTitle.setText(title);
            edtAuthor.setText(author);
            edtDate.setText(date);

            // Set spinner selection
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getId() == categoryId) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }

            // Load image
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imgPreview.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            getSupportActionBar().setTitle(R.string.add_news);
            // Set current date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            edtDate.setText(sdf.format(calendar.getTime()));
        }
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                // Resize bitmap to reduce size
                bitmap = resizeBitmap(bitmap, 800);
                imgPreview.setImageBitmap(bitmap);
                imageBase64 = bitmapToBase64(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratio = Math.min((float) maxSize / width, (float) maxSize / height);

        int newWidth = Math.round(ratio * width);
        int newHeight = Math.round(ratio * height);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        edtDate.setText(sdf.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveNews() {
        String title = edtNewsTitle.getText().toString().trim();
        String author = edtAuthor.getText().toString().trim();
        String date = edtDate.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerCategory.getSelectedItem() == null) {
            Toast.makeText(this, R.string.select_category, Toast.LENGTH_SHORT).show();
            return;
        }

        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        int categoryId = selectedCategory.getId();

        long result;
        if (newsId == -1) {
            // Add new news
            result = dbHelper.insertNews(title, categoryId, imageBase64, author, date);
        } else {
            // Update news
            result = dbHelper.updateNews(newsId, title, categoryId, imageBase64, author, date);
        }

        if (result > 0) {
            Toast.makeText(this, newsId == -1 ? R.string.add_success : R.string.update_success,
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, newsId == -1 ? R.string.add_failed : R.string.update_failed,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_category) {
            startActivity(new Intent(this, CategoryActivity.class));
            return true;
        } else if (id == R.id.menu_news) {
            startActivity(new Intent(this, NewsListActivity.class));
            return true;
        } else if (id == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}