package com.manfenjiayuan.mixicook_vip.ui.shopcart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.manfenjiayuan.business.utils.MUtils;
import com.manfenjiayuan.mixicook_vip.MainEvent;
import com.manfenjiayuan.mixicook_vip.R;
import com.manfenjiayuan.mixicook_vip.database.PurchaseShopcartEntity;
import com.manfenjiayuan.mixicook_vip.database.PurchaseShopcartService;
import com.mfh.framework.uikit.recyclerview.RegularAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 订单明细
 * Created by Nat.ZZN(bingshanguxue) on 15/6/5.
 */
public class OrderGoodsAdapter
        extends RegularAdapter<PurchaseShopcartEntity, OrderGoodsAdapter.CategoryViewHolder> {

    public OrderGoodsAdapter(Context context, List<PurchaseShopcartEntity> entityList) {
        super(context, entityList);
    }

    public interface OnAdapterListener {
        void onItemClick(View view, int position);
        void onDataSetChanged();
    }

    private OnAdapterListener adapterListener;

    public void setOnAdapterListsner(OnAdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(mLayoutInflater.inflate(R.layout.itemview_order_goods, parent, false));
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, final int position) {
        final PurchaseShopcartEntity entity = entityList.get(position);

        Glide.with(mContext).load(entity.getImgUrl()).error(R.mipmap.ic_image_error)
                .into(holder.tvHeader);
        holder.tvName.setText(entity.getName());
        holder.tvPrice.setText(MUtils.formatDouble(null, null,
                entity.getPrice(), "", "/", entity.getUnit()));
        holder.tvQuantity.setText(String.format("x %.0f", entity.getQuantity()));
    }

    @Override
    public void setEntityList(List<PurchaseShopcartEntity> entityList) {
        super.setEntityList(entityList);
        if (adapterListener != null) {
            adapterListener.onDataSetChanged();
        }
    }

    @Override
    public void removeEntity(int position) {
//        super.removeEntity(position);

        PurchaseShopcartEntity entity = getEntity(position);
        if (entity == null){
            return;
        }

        String sqlWhere = String.format("purchaseType = '%d' and providerId = '%d' and barcode = '%s'",
                PurchaseShopcartEntity.PURCHASE_TYPE_FRESH,
                entity.getProviderId(), entity.getBarcode());
        PurchaseShopcartService.getInstance().deleteBy(sqlWhere);
        //刷新列表
        entityList.remove(position);
        notifyItemRemoved(position);

        EventBus.getDefault().post(new MainEvent(MainEvent.EID_SHOPCART_DATASET_CHANGED));

        if (adapterListener != null) {
            adapterListener.onDataSetChanged();
        }
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_header)
        ImageView tvHeader;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_price)
        TextView tvPrice;
        @Bind(R.id.tv_quantity)
        TextView tvQuantity;

        public CategoryViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position < 0 || position >= entityList.size()) {
//                        ZLogger.d(String.format("do nothing because posiion is %d when dataset changed.", position));
                        return;
                    }

                    notifyDataSetChanged();

                    if (adapterListener != null) {
                        adapterListener.onItemClick(itemView, position);
                    }
                }
            });



        }
    }
}
