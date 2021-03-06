package com.mfh.litecashier.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mfh.comn.bean.TimeCursor;
import com.mfh.framework.core.utils.TimeUtil;
import com.mfh.litecashier.R;
import com.mfh.framework.uikit.recyclerview.RegularAdapter;
import com.manfenjiayuan.business.bean.InvCheckOrder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 库存－－库存盘点
 * Created by Nat.ZZN on 15/8/5.
 */
public class StockCheckOrderAdapter
        extends RegularAdapter<InvCheckOrder, StockCheckOrderAdapter.ProductViewHolder> {

    private InvCheckOrder curOrder = null;

    public StockCheckOrderAdapter(Context context, List<InvCheckOrder> entityList) {
        super(context, entityList);
    }

    public interface OnAdapterListener {
        void onItemClick(View view, int position);

        void onDataSetChanged();
    }

    private OnAdapterListener adapterListener;

    public void setOnAdapterListener(OnAdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductViewHolder(mLayoutInflater.inflate(R.layout.itemview_stockcheck_order, parent, false));
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        InvCheckOrder entity = entityList.get(position);

        if (curOrder != null && curOrder.getId().compareTo(entity.getId()) == 0) {
            holder.rootView.setSelected(true);
        } else {
            holder.rootView.setSelected(false);
        }

        holder.tvOrderName.setText(entity.getOrderName());
        holder.tvCreateDate.setText(String.format("盘点时间：%s", TimeUtil.format(entity.getCreatedDate(), TimeCursor.FORMAT_YYYYMMDDHHMM)));
        holder.tvOrderStatus.setText(String.format("状态: %s", entity.getStatusCaption()));
        if (entity.getStatus().equals(InvCheckOrder.INVCHECK_ORDERSTATUS_PROCESSING)){
            holder.tvOrderStatus.setTextColor(mContext.getResources().getColor(R.color.mfh_colorPrimary));
        }
        else{
            holder.tvOrderStatus.setTextColor(Color.BLACK);
        }
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.rootview)
        View rootView;
        @Bind(R.id.tv_orderName)
        TextView tvOrderName;
        @Bind(R.id.tv_createDate)
        TextView tvCreateDate;
        @Bind(R.id.tv_orderstatus)
        TextView tvOrderStatus;

        public ProductViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    getLayoutPosition()
                    int position = getAdapterPosition();
                    if (entityList == null || position < 0 || position >= entityList.size()) {
//                        ZLogger.d(String.format("do nothing because posiion is %d when dataset changed.", position));
                        return;
                    }

                    curOrder = entityList.get(position);
                    notifyDataSetChanged();

                    if (adapterListener != null) {
                        adapterListener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }

    @Override
    public void setEntityList(List<InvCheckOrder> entityList) {
        this.entityList = entityList;

        if (this.entityList != null && this.entityList.size() > 0){
            curOrder = this.entityList.get(0);
        }
        else{
            curOrder = null;
        }

        notifyDataSetChanged();
        if (adapterListener != null) {
            adapterListener.onDataSetChanged();
        }
    }

    public InvCheckOrder getCurOrder() {
        return curOrder;
    }
}
