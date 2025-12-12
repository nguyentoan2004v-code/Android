package vn.edu.stu.nguyenviettoan_dh52201590;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    private ListView listView;
    private EditText edtCategoryName;
    private Button btnAdd, btnUpdate, btnDelete;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> categoryList;
    private ArrayList<Integer> categoryIds;
    private int selectedPosition = -1;
    private int selectedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        getSupportActionBar().setTitle(R.string.category_management);

        listView = findViewById(R.id.listViewCategory);
        edtCategoryName = findViewById(R.id.edtCategoryName);
        btnAdd = findViewById(R.id.btnAddCategory);
        btnUpdate = findViewById(R.id.btnUpdateCategory);
        btnDelete = findViewById(R.id.btnDeleteCategory);

        dbHelper = new DatabaseHelper(this);
        categoryList = new ArrayList<>();
        categoryIds = new ArrayList<>();

        loadCategories();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCategory();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                selectedId = categoryIds.get(position);
                edtCategoryName.setText(categoryList.get(position));
            }
        });
    }

    private void loadCategories() {
        categoryList.clear();
        categoryIds.clear();
        Cursor cursor = dbHelper.getAllCategories();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                categoryIds.add(id);
                categoryList.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoryList);
        listView.setAdapter(adapter);
    }

    private void addCategory() {
        String name = edtCategoryName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.empty_category_name, Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbHelper.insertCategory(name);
        if (result > 0) {
            Toast.makeText(this, R.string.add_success, Toast.LENGTH_SHORT).show();
            edtCategoryName.setText("");
            loadCategories();
        } else {
            Toast.makeText(this, R.string.add_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCategory() {
        if (selectedId == -1) {
            Toast.makeText(this, R.string.select_category, Toast.LENGTH_SHORT).show();
            return;
        }

        String name = edtCategoryName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.empty_category_name, Toast.LENGTH_SHORT).show();
            return;
        }

        int result = dbHelper.updateCategory(selectedId, name);
        if (result > 0) {
            Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
            edtCategoryName.setText("");
            selectedId = -1;
            selectedPosition = -1;
            loadCategories();
        } else {
            Toast.makeText(this, R.string.update_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteCategory() {
        if (selectedId == -1) {
            Toast.makeText(this, R.string.select_category, Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_delete);
        builder.setMessage(R.string.delete_category_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean result = dbHelper.deleteCategory(selectedId);
                if (result) {
                    Toast.makeText(CategoryActivity.this, R.string.delete_success,
                            Toast.LENGTH_SHORT).show();
                    edtCategoryName.setText("");
                    selectedId = -1;
                    selectedPosition = -1;
                    loadCategories();
                } else {
                    Toast.makeText(CategoryActivity.this, R.string.cannot_delete_category,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.no, null);
        builder.show();
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
            return true;
        } else if (id == R.id.menu_news) {
            startActivity(new Intent(this, NewsListActivity.class));
            return true;
        } else if (id == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        else if (id == R.id.menu_exit) {
            showExitDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit);
        builder.setMessage(R.string.exit_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        builder.setNegativeButton(R.string.no, null);
        builder.show();
    }
}