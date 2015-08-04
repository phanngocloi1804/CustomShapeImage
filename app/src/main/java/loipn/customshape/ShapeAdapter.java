/*
 * Copyright (C) 2014 Lucas Rocha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package loipn.customshape;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.TwoWayView;
import org.lucasr.twowayview.widget.SpannableGridLayoutManager;
import org.lucasr.twowayview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class ShapeAdapter extends RecyclerView.Adapter<ShapeAdapter.SimpleViewHolder> {
    private static final int COUNT = 100;

    private final Context mContext;
    private final TwoWayView mRecyclerView;
    private final List<Integer> mItems;
    private int mCurrentItemId = 0;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imv;

        public SimpleViewHolder(View view) {
            super(view);
            imv = (ImageView) view.findViewById(R.id.imvShape);
        }
    }

    public ShapeAdapter(Context context, TwoWayView recyclerView, List<Integer> mItems) {
        mContext = context;
        this.mItems = mItems;

        mRecyclerView = recyclerView;
    }

    public void addItem(int position) {
        final int id = mCurrentItemId++;
        mItems.add(position, id);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.row_shape, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
//        holder.imv.setText(mItems.get(position).toString());
        holder.imv.setImageResource(mItems.get(position));

        boolean isVertical = (mRecyclerView.getOrientation() == TwoWayLayoutManager.Orientation.VERTICAL);
        final View itemView = holder.itemView;

        final int itemId = mItems.get(position);


    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
