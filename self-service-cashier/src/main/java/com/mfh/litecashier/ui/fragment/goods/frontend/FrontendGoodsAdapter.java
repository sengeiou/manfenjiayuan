package com.mfh.litecashier.ui.fragment.goods.frontend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.manfenjiayuan.business.utils.MUtils;
import com.mfh.litecashier.R;
import com.mfh.litecashier.bean.wrapper.LocalFrontCategoryGoods;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 前台类目商品
 * Created by bingshanguxue on 15/8/5.
 */
public class FrontendGoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum ITEM_TYPE {
        ITEM_TYPE_GOODS,
        ITEM_TYPE_ACTION
    }

    private final LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<LocalFrontCategoryGoods> entityList;

    public interface OnAdapterListener {
        void onDataSetChanged();

        void onClickGoods(LocalFrontCategoryGoods goods);
        void onLongClickGoods(int position, LocalFrontCategoryGoods goods);
        void onClickAction();
    }

    private OnAdapterListener adapterListener;

    public void setOnAdapterListener(OnAdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }

    public FrontendGoodsAdapter(Context context, List<LocalFrontCategoryGoods> entityList) {
        this.entityList = entityList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_GOODS.ordinal()){
            return new GoodsViewHolder(mLayoutInflater.inflate(R.layout.itemview_localcategory_goods, parent, false));
        }
        else if (viewType == ITEM_TYPE.ITEM_TYPE_ACTION.ordinal()){
            return new ActionViewHolder(mLayoutInflater.inflate(R.layout.itemview_action, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        LocalFrontCategoryGoods entity = entityList.get(position);

        if (holder.getItemViewType() == ITEM_TYPE.ITEM_TYPE_GOODS.ordinal()){
            ((GoodsViewHolder)holder).tvName.setText(String.format("%s/%s", entity.getSkuName(), entity.getShortName()));
            ((GoodsViewHolder)holder).tvCostPrice
                    .setText(MUtils.formatDouble(null, null,
                            entity.getCostPrice(), "", "/", entity.getUnit()));
            if (entity.getStatus().equals(0)){
                ((GoodsViewHolder)holder).overlayView.setVisibility(View.VISIBLE);
            }
            else {
                ((GoodsViewHolder)holder).overlayView.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return (entityList == null ? 0 : entityList.size());
    }

    @Override
    public int getItemViewType(int position) {
        LocalFrontCategoryGoods entity = entityList.get(position);
        if (entity.getType() == 0){
            return ITEM_TYPE.ITEM_TYPE_GOODS.ordinal();
        }
        return ITEM_TYPE.ITEM_TYPE_ACTION.ordinal();
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public LocalFrontCategoryGoods getEntity(int position) {
        if (this.entityList == null || position < 0 || position >= entityList.size()){
            return null;
        }

        return entityList.get(position);
    }

    public class GoodsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_costprice)
        TextView tvCostPrice;
        @BindView(R.id.overlay)
        View overlayView;

        public GoodsViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    LocalFrontCategoryGoods goods = getEntity(position);

                    if (goods != null && adapterListener != null) {
                        adapterListener.onClickGoods(goods);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    LocalFrontCategoryGoods goods = getEntity(position);

                    if (goods != null && adapterListener != null) {
                        adapterListener.onLongClickGoods(position, goods);
                    }
                    return false;
                }
            });
        }
    }

    public class ActionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ib_action)
        ImageButton ibAction;

        public ActionViewHolder(final View itemView) {
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

                    if (adapterListener != null) {
                        adapterListener.onClickAction();
                    }
                }
            });
        }

        @OnClick(R.id.ib_action)
        public void clickAction(){
            if (adapterListener != null) {
                adapterListener.onClickAction();
            }
        }
    }

    public void setEntityList(List<LocalFrontCategoryGoods> goodsList) {
        if (this.entityList == null){
            this.entityList = new ArrayList<>();
        }
        else{
            this.entityList.clear();
        }
        if (goodsList != null){
            this.entityList.addAll(goodsList);
        }

        LocalFrontCategoryGoods action = new LocalFrontCategoryGoods();
        action.setType(1);
        this.entityList.add(action);
//        ZLogger.d("entityList.size=" + entityList.size());

        notifyDataSetChanged();
        if (adapterListener != null) {
            adapterListener.onDataSetChanged();
        }
    }


}