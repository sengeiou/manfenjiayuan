package com.mfh.litecashier.ui.fragment.goods;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bingshanguxue.cashier.database.entity.PosProductEntity;
import com.bingshanguxue.cashier.database.service.PosProductService;
import com.mfh.comn.bean.PageInfo;
import com.mfh.framework.core.logger.ZLogger;
import com.mfh.framework.core.utils.StringUtils;
import com.mfh.framework.network.NetWorkUtil;
import com.mfh.framework.uikit.base.BaseListFragment;
import com.mfh.framework.uikit.recyclerview.GridItemDecoration2;
import com.mfh.framework.uikit.recyclerview.RecyclerViewEmptySupport;
import com.mfh.litecashier.CashierApp;
import com.mfh.litecashier.R;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * POS-本地前台类目商品
 * Created by Nat.ZZN(bingshanguxue) on 15/8/31.
 */
public class LocalFrontCategoryGoodsFragment extends BaseListFragment<ScGoodsSkuWrapper> {

    @Bind(R.id.swiperefreshlayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.order_list)
    RecyclerViewEmptySupport mRecyclerView;
    @Bind(R.id.empty_view)
    View emptyView;

    GridLayoutManager linearLayoutManager;
    private LocalFrontCategoryGoodsAdapter adapter;

    private Long categoryId;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_goods_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

    }

    @Override
    protected void createViewInner(View rootView, ViewGroup container, Bundle savedInstanceState) {
        //for Fragment.instantiate
        Bundle args = getArguments();
        if (args != null) {
            categoryId = args.getLong("categoryId");
        }

        initRecyclerView();
        setupSwipeRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    /**
     * 在主线程接收CashierEvent事件，必须是public void
     */
    public void onEventMainThread(PosCategoryGoodsEvent event) {
        ZLogger.d(String.format("PosCategoryGoodsEvent(%d/%s)",
                event.getEventId(), StringUtils.decodeBundle(event.getArgs())));

        Bundle args = event.getArgs();
        Long categoryId = args.getLong("categoryId");

        if (categoryId.compareTo(this.categoryId) != 0) {
            return;
        }
        if (event.getEventId() == PosCategoryGoodsEvent.EVENT_ID_RELOAD_DATA) {
            reload();
        }
        else if (event.getEventId() == PosCategoryGoodsEvent.EVENT_ID_SORT_RESET) {
            mRecyclerView.scrollToPosition(0);
        }
        else if (event.getEventId() == PosCategoryGoodsEvent.EVENT_ID_SORT_UPDATE) {
            String sortLetter = args.getString("sortLetter");
            if (!StringUtils.isEmpty(sortLetter)){
                // 该字母首次出现的位置
                int position = adapter.getPositionForSelection(sortLetter.charAt(0));

                if (position != -1) {
                    // TODO: 8/2/16 滚动到顶部显示 
                    mRecyclerView.scrollToPosition(position);
                }
            }

        }
    }

    private void initRecyclerView() {
        linearLayoutManager = new GridLayoutManager(getContext(), 6);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //enable optimizations if all item views are of the same height and width for
        //signficantly smoother scrolling
        mRecyclerView.setHasFixedSize(true);
//        menuRecyclerView.setScrollViewCallbacks(mScrollViewScrollCallbacks);
        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //设置列表为空时显示的视图
        mRecyclerView.setEmptyView(emptyView);
//        mRecyclerView.setWrapperView(mSwipeRefreshLayout);
        //添加分割线
//        mRecyclerView.addItemDecoration(new LineItemDecoration(
//                getActivity(), LineItemDecoration.VERTICAL_LIST));
        mRecyclerView.addItemDecoration(new GridItemDecoration2(getActivity(), 1,
                ContextCompat.getColor(getActivity(), R.color.mf_dividerColorPrimary), 0,
                ContextCompat.getColor(getActivity(), R.color.mf_dividerColorPrimary), 0.1f,
                ContextCompat.getColor(getActivity(), R.color.mf_dividerColorPrimary), 0f));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                //lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
//                ZLogger.d(String.format("%s %d(%d)", (dy > 0 ? "向上滚动" : "向下滚动"), lastVisibleItem, totalItemCount));
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        loadMore();
                    }
                } else if (dy < 0) {
                    isLoadingMore = false;
                }
            }
        });

        adapter = new LocalFrontCategoryGoodsAdapter(CashierApp.getAppContext(), null);
        adapter.setOnAdapterLitener(new LocalFrontCategoryGoodsAdapter.AdapterListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, final int position) {

            }

            @Override
            public void onDataSetChanged() {
                onLoadFinished();
            }
        });

        mRecyclerView.setAdapter(adapter);
    }

    /**
     * 重新加载数据
     */
    @OnClick(R.id.empty_view)
    public synchronized void reload() {
        if (bSyncInProgress) {
//            onLoadFinished();
            return;
        }

        if (!NetWorkUtil.isConnect(CashierApp.getAppContext())) {
            ZLogger.d("网络未连接，暂停加载类目商品。");
            onLoadFinished();
            return;
        }

        onLoadStart();

        mPageInfo = new PageInfo(-1, 200);
        if (adapter != null) {
            adapter.setEntityList(null);
        }
        //从第一页开始请求，每页最多50条记录
        load(mPageInfo);
        mPageInfo.setPageNo(1);
    }



    /**
     * 翻页加载更多数据
     */
    public void loadMore() {
        if (bSyncInProgress) {
//            onLoadFinished();
            return;
        }
        if (!NetWorkUtil.isConnect(CashierApp.getAppContext())) {
            ZLogger.d("网络未连接，暂停加载类目商品。");
            onLoadFinished();
            return;
        }

        if (mPageInfo.hasNextPage() && mPageInfo.getPageNo() <= MAX_PAGE) {
            mPageInfo.moveToNext();

            onLoadStart();
            load(mPageInfo);
        } else {
            ZLogger.d("加载类目商品，已经是最后一页。");
            onLoadFinished();
        }
    }

    private void load(PageInfo pageInfo) {
        String sqlWhere = String.format("frontCategoryId = '%d'", categoryId);
        List<PosProductEntity> entities = PosProductService.get().queryAll(sqlWhere, null);
        if (adapter != null) {
            adapter.setEntityList(entities);
        }

        onLoadFinished();
    }


    /**
     * 设置刷新
     */
    private void setupSwipeRefresh() {
//        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefreshlayout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(
                    R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                    R.color.swiperefresh_color3, R.color.swiperefresh_color4);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (mState == STATE_REFRESH) {
                        ZLogger.d("正在刷新");
                        return;
                    }

                    reload();
                }
            });
        }
        mState = STATE_NONE;
    }

    /**
     * 设置刷新状态
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing) {
            setSwipeRefreshLoadingState();
        } else {
            setSwipeRefreshLoadedState();
        }
    }

    /**
     * 设置顶部正在加载的状态
     */
    private void setSwipeRefreshLoadingState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);


            mState = STATE_REFRESH;
        }
    }

    /**
     * 设置顶部加载完毕的状态
     */
    private void setSwipeRefreshLoadedState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);

            mState = STATE_NONE;
        }
    }

}