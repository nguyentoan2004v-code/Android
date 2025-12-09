package vn.edu.stu.nguyenviettoan_dh52201590;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "NewsManagement.db";
    private static final int DATABASE_VERSION = 1;

    // Table Category
    private static final String TABLE_CATEGORY = "Category";
    private static final String CAT_ID = "id";
    private static final String CAT_NAME = "name";

    // Table News
    private static final String TABLE_NEWS = "News";
    private static final String NEWS_ID = "id";
    private static final String NEWS_TITLE = "title";
    private static final String NEWS_CATEGORY_ID = "category_id";
    private static final String NEWS_IMAGE = "image";
    private static final String NEWS_AUTHOR = "author";
    private static final String NEWS_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Category table
        String createCategoryTable = "CREATE TABLE " + TABLE_CATEGORY + "("
                + CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CAT_NAME + " TEXT NOT NULL)";
        db.execSQL(createCategoryTable);

        // Create News table
        String createNewsTable = "CREATE TABLE " + TABLE_NEWS + "("
                + NEWS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NEWS_TITLE + " TEXT NOT NULL,"
                + NEWS_CATEGORY_ID + " INTEGER,"
                + NEWS_IMAGE + " TEXT,"
                + NEWS_AUTHOR + " TEXT,"
                + NEWS_DATE + " TEXT,"
                + "FOREIGN KEY(" + NEWS_CATEGORY_ID + ") REFERENCES "
                + TABLE_CATEGORY + "(" + CAT_ID + "))";
        db.execSQL(createNewsTable);

        // Insert sample categories
        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        String[] categories = {"Technology", "Sports", "Entertainment", "Business", "Health"};
        for (String cat : categories) {
            ContentValues values = new ContentValues();
            values.put(CAT_NAME, cat);
            db.insert(TABLE_CATEGORY, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(db);
    }

    // Category CRUD operations
    public long insertCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CAT_NAME, name);
        return db.insert(TABLE_CATEGORY, null, values);
    }

    public Cursor getAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CATEGORY, null);
    }

    public int updateCategory(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CAT_NAME, name);
        return db.update(TABLE_CATEGORY, values, CAT_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public boolean deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Check if category has news
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NEWS +
                " WHERE " + NEWS_CATEGORY_ID + "=?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count > 0) {
            return false; // Cannot delete category with news
        }
        db.delete(TABLE_CATEGORY, CAT_ID + "=?", new String[]{String.valueOf(id)});
        return true;
    }

    // News CRUD operations
    public long insertNews(String title, int categoryId, String image,
                           String author, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NEWS_TITLE, title);
        values.put(NEWS_CATEGORY_ID, categoryId);
        values.put(NEWS_IMAGE, image);
        values.put(NEWS_AUTHOR, author);
        values.put(NEWS_DATE, date);
        return db.insert(TABLE_NEWS, null, values);
    }

    public Cursor getAllNews() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT n.*, c." + CAT_NAME + " FROM " + TABLE_NEWS +
                " n LEFT JOIN " + TABLE_CATEGORY + " c ON n." + NEWS_CATEGORY_ID +
                "=c." + CAT_ID, null);
    }

    public int updateNews(int id, String title, int categoryId, String image,
                          String author, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NEWS_TITLE, title);
        values.put(NEWS_CATEGORY_ID, categoryId);
        values.put(NEWS_IMAGE, image);
        values.put(NEWS_AUTHOR, author);
        values.put(NEWS_DATE, date);
        return db.update(TABLE_NEWS, values, NEWS_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public void deleteNews(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NEWS, NEWS_ID + "=?", new String[]{String.valueOf(id)});
    }

    public String getCategoryName(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + CAT_NAME + " FROM " + TABLE_CATEGORY +
                " WHERE " + CAT_ID + "=?", new String[]{String.valueOf(categoryId)});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            cursor.close();
            return name;
        }
        cursor.close();
        return "";
    }
}
