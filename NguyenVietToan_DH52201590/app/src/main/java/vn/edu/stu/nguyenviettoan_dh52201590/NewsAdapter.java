package vn.edu.stu.nguyenviettoan_dh52201590;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import vn.edu.stu.nguyenviettoan_dh52201590.Model.News;

public class NewsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<News> newsList;
    private OnNewsActionListener listener;

    public interface OnNewsActionListener {
        void onEdit(News news);
        void onDelete(News news);
    }

    public NewsAdapter(Context context, ArrayList<News> newsList, OnNewsActionListener listener) {
        this.context = context;
        this.newsList = newsList;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
            holder = new ViewHolder();
            holder.imgNews = convertView.findViewById(R.id.imgNews);
            holder.tvNewsId = convertView.findViewById(R.id.tvNewsId);
            holder.tvNewsTitle = convertView.findViewById(R.id.tvNewsTitle);
            holder.tvCategoryName = convertView.findViewById(R.id.tvCategoryName);
            holder.btnEdit = convertView.findViewById(R.id.btnEdit);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final News news = newsList.get(position);

        holder.tvNewsId.setText("ID: " + news.getId());
        holder.tvNewsTitle.setText(news.getTitle());
        holder.tvCategoryName.setText(news.getCategoryName());

        // Load image from Base64
        if (news.getImage() != null && !news.getImage().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(news.getImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imgNews.setImageBitmap(decodedByte);
            } catch (Exception e) {
                holder.imgNews.setImageResource(R.drawable.ic_news_placeholder);
            }
        } else {
            holder.imgNews.setImageResource(R.drawable.ic_news_placeholder);
        }

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEdit(news);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete(news);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView imgNews;
        TextView tvNewsId;
        TextView tvNewsTitle;
        TextView tvCategoryName;
        Button btnEdit;
        Button btnDelete;
    }
}
