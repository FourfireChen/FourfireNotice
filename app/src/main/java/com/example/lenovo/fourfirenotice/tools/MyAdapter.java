package com.example.lenovo.fourfirenotice.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.lenovo.fourfirenotice.R;
import com.example.lenovo.fourfirenotice.activity.EditActivity;
import com.example.lenovo.fourfirenotice.db.Notice;

import java.util.List;

/**
 * Created by lenovo on 2017/12/1.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{
    private List<Notice> noticeList;
    public static boolean isDeleteMore = false;
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View noticeView;
        CardView cardView;
        TextView textView;
        Button button;
        public ViewHolder(View itemView)
        {
            super(itemView);
            noticeView = itemView;
            cardView = (CardView)itemView.findViewById(R.id.cardview);
            textView = (TextView)itemView.findViewById(R.id.text);
            button = (Button)itemView.findViewById(R.id.outDelete);
        }
        public TextView getTextView()
        {
            return textView;
        }
        public Button getButton()
        {
            return button;
        }
    }

    public MyAdapter(List<Notice> noticeList)
    {
        this.noticeList = noticeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.getTextView().setText(noticeList.get(position).getText());
        holder.noticeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(holder.noticeView.getContext(),EditActivity.class);
                intent.putExtra("position",position);
                holder.noticeView.getContext().startActivity(intent);
            }
        });
        if(isDeleteMore)    holder.button.setVisibility(View.VISIBLE);
        else    holder.button.setVisibility(View.INVISIBLE);
        holder.button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                delete(position,v.getContext());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return noticeList.size();
    }

    public void updataList(List<Notice> noticeList)
    {
        this.noticeList = noticeList;
        notifyDataSetChanged();
    }

    public void delete(final int position, Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定删除?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                noticeList.get(position).delete();
                noticeList.remove(position);
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {}
        });
        builder.show();
    }

    public void showDelete()
    {
        if(!isDeleteMore)
            isDeleteMore = true;
        else
            isDeleteMore = false;
        notifyDataSetChanged();
    }
}
