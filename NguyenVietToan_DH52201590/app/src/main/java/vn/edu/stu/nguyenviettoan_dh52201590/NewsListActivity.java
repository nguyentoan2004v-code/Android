package vn.edu.stu.nguyenviettoan_dh52201590;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

import vn.edu.stu.nguyenviettoan_dh52201590.Model.News;

public class NewsListActivity extends AppCompatActivity {
    private ListView listView;
    private FloatingActionButton btnAdd;
    private DatabaseHelper dbHelper;
    private ArrayList<News> newsList;
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        getSupportActionBar().setTitle(R.string.news_management);

        listView = findViewById(R.id.listViewNews);
        btnAdd = findViewById(R.id.btnAddNews);

        dbHelper = new DatabaseHelper(this);
        newsList = new ArrayList<>();

        loadNews();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsListActivity.this, NewsFormActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNews();
    }

    private void loadNews() {
        newsList.clear();
        Cursor cursor = dbHelper.getAllNews();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                int categoryId = cursor.getInt(2);
                String image = cursor.getString(3);
                String author = cursor.getString(4);
                String date = cursor.getString(5);
                String categoryName = cursor.getString(6);

                News news = new News(id, title, categoryId, categoryName, image, author, date);
                newsList.add(news);
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new NewsAdapter(this, newsList, new NewsAdapter.OnNewsActionListener() {
            @Override
            public void onEdit(News news) {
                editNews(news);
            }

            @Override
            public void onDelete(News news) {
                deleteNews(news);
            }
        });
        listView.setAdapter(adapter);
    }

    private void editNews(News news) {
        Intent intent = new Intent(NewsListActivity.this, NewsFormActivity.class);
        intent.putExtra("news_id", news.getId());
        intent.putExtra("news_title", news.getTitle());
        intent.putExtra("news_category_id", news.getCategoryId());
        intent.putExtra("news_image", news.getImage());
        intent.putExtra("news_author", news.getAuthor());
        intent.putExtra("news_date", news.getDate());
        startActivity(intent);
    }

    private void deleteNews(final News news) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_delete);
        builder.setMessage(R.string.delete_news_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteNews(news.getId());
                Toast.makeText(NewsListActivity.this, R.string.delete_success,
                        Toast.LENGTH_SHORT).show();
                loadNews();
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
            startActivity(new Intent(this, CategoryActivity.class));
            return true;
        } else if (id == R.id.menu_news) {
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
