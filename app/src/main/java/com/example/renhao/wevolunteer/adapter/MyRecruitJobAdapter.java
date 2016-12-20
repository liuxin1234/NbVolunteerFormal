package com.example.renhao.wevolunteer.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.model.items.MyProjectItem;
import com.example.renhao.wevolunteer.R;
import com.example.renhao.wevolunteer.utils.Util;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/10/16.
 */

public class MyRecruitJobAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<MyProjectItem> lists;

    public MyRecruitJobAdapter(Context context, List<MyProjectItem> lists) {
        mContext = context;
        this.lists = lists;
        this.mInflater = LayoutInflater.from(context);

    }

    public void setDate(List<MyProjectItem> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public final class ViewHolder {
        public TextView name;
        public TextView TIME;
        public TextView STATE;
        public ImageView PIC;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyRecruitJobAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_myproject, null);//根据布局产生一个view
            holder = new MyRecruitJobAdapter.ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tv_myProject_projectName);
            holder.STATE = (TextView) convertView.findViewById(R.id.tv_myProject_state);
            holder.PIC = (ImageView) convertView.findViewById(R.id.iv_myProject_item);
            holder.TIME = (TextView) convertView.findViewById(R.id.tv_myProject_projectTime);
            convertView.setTag(holder);//绑定ViewHolder对象
            //对于listview，注意添加这一行，即可在item上使用高度
            AutoUtils.autoSize(convertView);
        } else {
            holder = (MyRecruitJobAdapter.ViewHolder) convertView.getTag();//取出ViewHolder对象
        }
        holder.name.setText(lists.get(position).getNeme());
        holder.TIME.setText(lists.get(position).getTime());
        holder.STATE.setText("取消"+"\n"+"报名");
        if (lists.get(position).getAuditStatus() == 0){
            System.out.println(1);
            holder.STATE.setVisibility(View.VISIBLE);
            holder.STATE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemRemoveClickListener != null) {
                        mOnItemRemoveClickListener.onItemRemoveClickListener(lists, position);
                    }
                }
            });
        }else {
            System.out.println(2);
            holder.STATE.setVisibility(View.GONE);
        }


        if (TextUtils.isEmpty(lists.get(position).getPic())) {
            holder.PIC.setImageResource(R.drawable.img_unload);
        } else {
            Picasso.with(mContext).load(Util.getRealUrl(lists.get(position).getPic()))
                    .fit().tag("Ptr")
                    .placeholder(R.drawable.img_unload)
                    .error(R.drawable.img_unload)
                    .into(holder.PIC);
        }

        return convertView;
    }

    public interface OnItemRemoveClickListener{
        public void onItemRemoveClickListener(List<MyProjectItem> data,int position);
    }
    private OnItemRemoveClickListener mOnItemRemoveClickListener;

    public void setOnItemRemoveClickListener(OnItemRemoveClickListener mOnItemRemoveClickListener){
        this.mOnItemRemoveClickListener = mOnItemRemoveClickListener;
    }

}
