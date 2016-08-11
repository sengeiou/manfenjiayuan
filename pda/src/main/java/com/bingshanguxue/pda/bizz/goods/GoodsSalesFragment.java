package com.bingshanguxue.pda.bizz.goods;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bingshanguxue.pda.R;
import com.mfh.comn.bean.EntityWrapper;
import com.mfh.comn.net.data.RspQueryResult;
import com.mfh.framework.MfhApplication;
import com.mfh.framework.api.ProductAggDate;
import com.mfh.framework.api.ScApi;
import com.mfh.framework.core.logger.ZLogger;
import com.mfh.framework.net.NetCallBack;
import com.mfh.framework.net.NetProcessor;
import com.mfh.framework.uikit.base.BaseFragment;
import com.mfh.framework.uikit.recyclerview.RecyclerViewEmptySupport;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * 商品－－销量
 * Created by Nat.ZZN(bingshanguxue) on 15/8/30.
 */
public class GoodsSalesFragment extends BaseFragment {

//    @Bind(R.id.goods_list)
    RecyclerViewEmptySupport salesRecyclerView;
    private GoodsSalesAdapter goodsAdapter;
//    @Bind(R.id.animProgress)
    ProgressBar animProgress;
//    @Bind(R.id.empty_view)
    View emptyView;

    private Long proSkuId = null;
//    protected Typeface mTfLight;

    public static GoodsSalesFragment newInstance(Bundle args) {
        GoodsSalesFragment fragment = new GoodsSalesFragment();

        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_goods_sales;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void createViewInner(View rootView, ViewGroup container, Bundle savedInstanceState) {
        salesRecyclerView = (RecyclerViewEmptySupport) rootView.findViewById(R.id.goods_list);
        animProgress = (ProgressBar) rootView.findViewById(R.id.animProgress);
        emptyView = rootView.findViewById(R.id.empty_view);

        initRecyclerView();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    /**
     * 验证
     */
    public void onEventMainThread(ScGoodsSkuEvent event) {
        int eventId = event.getEventId();
        Bundle args = event.getArgs();

        ZLogger.d(String.format("ScGoodsSkuEvent(%d)", eventId));
        switch (eventId) {
            case ScGoodsSkuEvent.EVENT_ID_SKU_UPDATE: {
                Long proSkuId = args.getLong(ScGoodsSkuEvent.EXTRA_KEY_PROSKUID);
                refresh(proSkuId);
            }
            break;

        }
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        salesRecyclerView.setLayoutManager(linearLayoutManager);
        //enable optimizations if all item views are of the same height and width for
        //signficantly smoother scrolling
        salesRecyclerView.setHasFixedSize(true);
        //添加分割线
//        salesRecyclerView.addItemDecoration(new LineItemDecoration(
//                getActivity(), LineItemDecoration.VERTICAL_LIST));
        //设置列表为空时显示的视图
        salesRecyclerView.setEmptyView(emptyView);

        goodsAdapter = new GoodsSalesAdapter(getActivity(), null);
        goodsAdapter.setOnAdapterListener(new GoodsSalesAdapter.OnAdapterListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, final int position) {
            }

            @Override
            public void onDataSetChanged() {
//                isLoadingMore = false;
                animProgress.setVisibility(View.GONE);
            }
        });

        salesRecyclerView.setAdapter(goodsAdapter);
    }


    /**
     * 刷新信息
     */
    private void refresh(Long proSkuId) {
        this.proSkuId = proSkuId;
        if (proSkuId == null) {
            goodsAdapter.setEntityList(null);
            return;
        }

        NetCallBack.QueryRsCallBack queryRsCallBack = new NetCallBack.QueryRsCallBack<>(
                new NetProcessor.QueryRsProcessor<ProductAggDate>(null) {
            @Override
            public void processQueryResult(RspQueryResult<ProductAggDate> rs) {
                //此处在主线程中执行。
                List<ProductAggDate> entityList = new ArrayList<>();
                if (rs != null) {
                    for (EntityWrapper<ProductAggDate> wrapper : rs.getRowDatas()) {
                        entityList.add(wrapper.getBean());
                    }
                }

                goodsAdapter.setEntityList(entityList);
            }

            @Override
            protected void processFailure(Throwable t, String errMsg) {
                super.processFailure(t, errMsg);
                ZLogger.d("加载商品销量数据失败:" + errMsg);
                goodsAdapter.setEntityList(null);
            }
        }, ProductAggDate.class, MfhApplication.getAppContext());

        ScApi.productAggDateList(proSkuId, queryRsCallBack);
    }


}