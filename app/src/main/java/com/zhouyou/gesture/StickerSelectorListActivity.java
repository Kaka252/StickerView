package com.zhouyou.gesture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：ZhouYou
 * 日期：2016/9/3.
 */
public class StickerSelectorListActivity extends Activity implements AdapterView.OnItemClickListener {

    private GridView gv;

    private List<Integer> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_selector);
        gv = (GridView) findViewById(R.id.gv);
        gv.setOnItemClickListener(this);
        data = new ArrayList<>();
        data.add(R.mipmap.ic_avatar_1);
        data.add(R.mipmap.ic_avatar_2);
        data.add(R.mipmap.ic_avatar_3);

        StickerAdapter adapter = new StickerAdapter(this, data);
        gv.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int resource = (int) parent.getItemAtPosition(position);
        if (resource > 0) {
            Intent intent = getIntent();
            intent.putExtra("res", resource);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private static class StickerAdapter extends BaseAdapter {

        private Context context;
        private List<Integer> data;

        public StickerAdapter(Context context, List<Integer> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Integer getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_sticker, null);
            }
            ImageView ivSticker = (ImageView) convertView.findViewById(R.id.iv_sticker);
            ivSticker.setImageResource(getItem(position));
            return convertView;
        }
    }
}
