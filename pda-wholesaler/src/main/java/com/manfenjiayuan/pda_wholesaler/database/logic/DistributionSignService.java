package com.manfenjiayuan.pda_wholesaler.database.logic;

import com.manfenjiayuan.business.bean.ChainGoodsSku;
import com.manfenjiayuan.business.bean.InvSendIoOrderItem;
import com.manfenjiayuan.business.bean.InvSendOrderItem;
import com.manfenjiayuan.business.bean.InvSkuProvider;
import com.manfenjiayuan.pda_wholesaler.database.dao.DistributionSignDao;
import com.manfenjiayuan.pda_wholesaler.database.entity.DistributionSignEntity;
import com.mfh.comn.bean.PageInfo;
import com.mfh.framework.api.constant.IsPrivate;
import com.mfh.framework.core.logger.ZLogger;
import com.mfh.framework.core.logic.ServiceFactory;
import com.mfh.framework.core.service.BaseService;
import com.mfh.framework.core.service.DataSyncStrategy;
import com.mfh.framework.core.utils.ObjectsCompact;
import com.mfh.framework.core.utils.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * POS--商品--签收
 * Created by Nat.ZZN(bingshanguxue) on 15-09-06..
 */
public class DistributionSignService extends BaseService<DistributionSignEntity, String, DistributionSignDao> {
    @Override
    protected Class<DistributionSignDao> getDaoClass() {
        return DistributionSignDao.class;
    }

    @Override
    public DataSyncStrategy getDataSyncStrategy() {
        return null;
    }

    private static DistributionSignService instance = null;
    /**
     * 返回 IMConversationService 实例
     * @return
     */
    public static DistributionSignService get() {
        String lsName = DistributionSignService.class.getName();
        if (ServiceFactory.checkService(lsName))
            instance = ServiceFactory.getService(lsName);
        else {
            instance = new DistributionSignService();//初始化登录服务
        }
        return instance;
    }

    public boolean entityExistById(String id) {
        try {
            return getDao().entityExistById(id);
        } catch (Exception e) {
            ZLogger.e(e.toString());
            return false;
        }
    }

    public DistributionSignEntity getEntityById(String id) {
        try {
            return getDao().getEntityById(id);
        } catch (Exception e) {
            ZLogger.e(e.toString());
            return null;
        }
    }

//    public void save(DistributionSignEntity entity) {
//        getDao().save(entity);
//    }

    public void update(DistributionSignEntity entity) {
        getDao().update(entity);
    }

    public void saveOrUpdate(DistributionSignEntity entity) {
        getDao().saveOrUpdate(entity);
    }

    /**
     * 清空历史记录
     */
    public void clear() {
        getDao().deleteAll();
    }

    /**
     * 查询指定session下的消息类比，按照逆序
     *
     * @param pageInfo
     * @return
     */
    public List<DistributionSignEntity> queryAll(PageInfo pageInfo) {
        return getDao().queryAll(pageInfo);
    }

    public List<DistributionSignEntity> queryAll() {
        return getDao().queryAll();
    }

    public List<DistributionSignEntity> queryAllBy(String strWhere) {
        return getDao().queryAllBy(strWhere);
    }

    public List<DistributionSignEntity> queryAllByDesc(String strWhere) {
        return getDao().queryAllByDesc(strWhere);
    }

    public void deleteById(String id) {
        try {
            getDao().deleteById(id);
        } catch (Exception e) {
            ZLogger.e(e.toString());
        }
    }

    public void deleteBy(String strWhere) {
        try {
            getDao().deleteBy(strWhere);
        } catch (Exception e) {
            ZLogger.e(e.toString());
        }
    }

    /**
     * 根据条码查商品
     */
    public DistributionSignEntity queryEntityBy(String barcode) {
        if (StringUtils.isEmpty(barcode)) {
            return null;
        }

        List<DistributionSignEntity> entityList = queryAllBy(String.format("barcode = '%s'", barcode));
        if (entityList != null && entityList.size() > 0) {
            return entityList.get(0);
        }

        return null;
    }

    /**
     * 保存采购订单明细
     */
    private void saveInvSendOrderItem(InvSendOrderItem orderItem) {
        DistributionSignEntity entity = new DistributionSignEntity();
        entity.setCreatedDate(new Date());//使用当前日期，表示加入购物车信息
        entity.setUpdatedDate(new Date());

        entity.setOrderId(orderItem.getOrderId());
        entity.setProductId(orderItem.getId());
        entity.setProSkuId(orderItem.getProSkuId());
        entity.setChainSkuId(orderItem.getChainSkuId());
        entity.setProductName(orderItem.getProductName());
        entity.setUnitSpec(orderItem.getUnit());

        entity.setPrice(orderItem.getPrice());
        // TODO: 5/13/16 PC上还是用的totalCount字段
        entity.setTotalCount(orderItem.getTotalCount() != null ? orderItem.getTotalCount() : 0D);
//        if (ObjectsCompact.equals(orderItem.getUnit(), orderItem.getBuyUnit()) &&
//                ObjectsCompact.equals(orderItem.getPriceType(), orderItem.getBuyPriceType())) {
//            entity.setTotalCount(orderItem.getAskTotalCount());
//        } else {
//            entity.setTotalCount(0D);
//        }
        if (entity.getTotalCount() == null || entity.getPrice() == null){
            entity.setAmount(0D);
        }
        else{
            entity.setAmount(entity.getPrice() * entity.getTotalCount());
        }

        entity.setBarcode(orderItem.getBarcode());
        entity.setProviderId(orderItem.getProviderId());
        entity.setIsPrivate(orderItem.getIsPrivate());

        entity.setQuantityCheck(entity.getTotalCount());
        entity.setInspectStatus(DistributionSignEntity.INSPECT_STATUS_NONE);

        saveOrUpdate(entity);
    }

    /**
     * 保存发货单明细
     */
    private void saveInvSendIoOrderItem(InvSendIoOrderItem orderItem) {
        DistributionSignEntity entity = new DistributionSignEntity();
        entity.setCreatedDate(new Date());//使用当前日期，表示加入购物车信息
        entity.setUpdatedDate(new Date());

//        entity.setOrderId(orderItem.getOrderId());
        entity.setProductId(orderItem.getId());
        entity.setProSkuId(orderItem.getProSkuId());
        entity.setChainSkuId(orderItem.getChainSkuId());
        entity.setProductName(orderItem.getProductName());
        entity.setTotalCount(orderItem.getQuantityCheck());
        entity.setPrice(orderItem.getPrice());
        entity.setAmount(orderItem.getAmount());
        entity.setUnitSpec(orderItem.getUnitSpec());
        entity.setBarcode(orderItem.getBarcode());
        entity.setProviderId(orderItem.getProviderId());
        entity.setIsPrivate(orderItem.getIsPrivate());

        entity.setQuantityCheck(entity.getTotalCount());
        entity.setInspectStatus(DistributionSignEntity.INSPECT_STATUS_NONE);

        saveOrUpdate(entity);
    }

    /**
     * 保存扫描商品信息
     */
    public void saveChainGoodsSku(ChainGoodsSku goods) {
        if (goods == null) {
            return;
        }

        DistributionSignEntity entity = queryEntityBy(goods.getBarcode());
        if (entity != null) {
            //不更新信息，读取到商品后自动显示到最新
//            entity.setTotalCount(entity.getTotalCount() + 1);
//            entity.setQuantityCheck(entity.getQuantityCheck() + 1);
//            entity.setInspectStatus(DistributionSignEntity.INSPECT_STATUS_NONE);
//
//            //设置金额
//            if (entity.getQuantityCheck() == null || entity.getPrice() == null) {
//                entity.setAmount(0D);
//            } else {
//                entity.setAmount(entity.getQuantityCheck() * entity.getPrice());
//            }
            entity.setUpdatedDate(new Date());
        } else {
            entity = new DistributionSignEntity();
            entity.setCreatedDate(new Date());//使用当前日期，表示加入购物车信息

//        entity.setOrderId(orderItem.getOrderId());
            entity.setProductId(goods.getId());
            entity.setProSkuId(goods.getProSkuId());
            entity.setChainSkuId(goods.getId());
            entity.setProductName(goods.getSkuName());
            entity.setPrice(goods.getSingleCostPrice());
            entity.setUnitSpec(goods.getUnit());
            entity.setBarcode(goods.getBarcode());
            entity.setProviderId(goods.getTenantId());
            entity.setIsPrivate(IsPrivate.PLATFORM);

            entity.setTotalCount(1D);
            entity.setQuantityCheck(entity.getTotalCount());
            entity.setInspectStatus(DistributionSignEntity.INSPECT_STATUS_NONE);

            //设置金额
            if (entity.getQuantityCheck() == null || entity.getPrice() == null) {
                entity.setAmount(0D);
            } else {
                entity.setAmount(entity.getQuantityCheck() * entity.getPrice());
            }
            entity.setUpdatedDate(new Date());
        }

        saveOrUpdate(entity);
    }

    /**
     * 保存扫描商品信息
     */
    public void saveInvSkuProvider(InvSkuProvider goods) {
        if (goods == null) {
            return;
        }

        DistributionSignEntity entity = queryEntityBy(goods.getBarcode());
        if (entity != null) {
            //不更新信息，读取到商品后自动显示到最新
//            entity.setTotalCount(entity.getTotalCount() + 1);
//            entity.setQuantityCheck(entity.getQuantityCheck() + 1);
//            entity.setInspectStatus(DistributionSignEntity.INSPECT_STATUS_NONE);
//
//            //设置金额
//            if (entity.getQuantityCheck() == null || entity.getPrice() == null) {
//                entity.setAmount(0D);
//            } else {
//                entity.setAmount(entity.getQuantityCheck() * entity.getPrice());
//            }
            entity.setUpdatedDate(new Date());
        } else {
            entity = new DistributionSignEntity();
            entity.setCreatedDate(new Date());//使用当前日期，表示加入购物车信息

//        entity.setOrderId(orderItem.getOrderId());
            entity.setProductId(goods.getProductId());
            entity.setProSkuId(goods.getProSkuId());
            entity.setChainSkuId(goods.getTenantSkuId());
            entity.setProductName(goods.getSkuName());
            entity.setPrice(goods.getCostPrice());
            entity.setUnitSpec(goods.getUnit());
            entity.setBarcode(goods.getBarcode());
            entity.setProviderId(goods.getProviderId());
            entity.setIsPrivate(IsPrivate.PLATFORM);

            entity.setTotalCount(1D);
            entity.setQuantityCheck(entity.getTotalCount());
            entity.setInspectStatus(DistributionSignEntity.INSPECT_STATUS_NONE);

            //设置金额
            if (entity.getQuantityCheck() == null || entity.getPrice() == null) {
                entity.setAmount(0D);
            } else {
                entity.setAmount(entity.getQuantityCheck() * entity.getPrice());
            }
            entity.setUpdatedDate(new Date());
        }

        saveOrUpdate(entity);
    }

    /**
     * 保存发货单明细
     */
    public void saveSendOrderItems(List<InvSendOrderItem> entityList) {
        clear();
        if (entityList != null && entityList.size() > 0) {
            for (InvSendOrderItem entity : entityList) {
                saveInvSendOrderItem(entity);
            }
        }
    }

    /**
     * 保存采购单明细
     */
    public void saveSendIoOrdersItems(List<InvSendIoOrderItem> entityList) {
        clear();
        if (entityList != null && entityList.size() > 0) {
            for (InvSendIoOrderItem entity : entityList) {
                saveInvSendIoOrderItem(entity);
            }
        }
    }

    /**
     * 验收商品
     */
    public void inspect(DistributionSignEntity entity, Double price, Double quantity) {
        if (entity == null) {
            return;
        }

        if (!entityExistById(String.valueOf(entity.getId()))) {
            return;
        }

        if (price != null){
            entity.setPrice(price);
        }

        if (quantity == null) {
            quantity = 0D;
        }
        entity.setQuantityCheck(quantity);

        if (entity.getQuantityCheck() == null || entity.getPrice() == null) {
            entity.setAmount(0D);
        } else {
            entity.setAmount(entity.getQuantityCheck() * entity.getPrice());
        }

        if (ObjectsCompact.equals(entity.getTotalCount(), quantity)) {
            entity.setInspectStatus(DistributionSignEntity.INSPECT_STATUS_OK);
        } else {
            if (quantity == 0) {
                entity.setInspectStatus(DistributionSignEntity.INSPECT_STATUS_REJECT);
            } else {
                entity.setInspectStatus(DistributionSignEntity.INSPECT_STATUS_CONFLICT);
            }
        }
        entity.setUpdatedDate(new Date());
        saveOrUpdate(entity);
    }

    /**
     * 拒收商品
     */
    public void reject(DistributionSignEntity entity) {
        if (entity == null) {
            return;
        }

        if (entityExistById(String.valueOf(entity.getId()))) {
            entity.setQuantityCheck(0D);

            if (entity.getQuantityCheck() == null || entity.getPrice() == null) {
                entity.setAmount(0D);
            } else {
                entity.setAmount(entity.getQuantityCheck() * entity.getPrice());
            }
            entity.setInspectStatus(DistributionSignEntity.INSPECT_STATUS_REJECT);
            entity.setUpdatedDate(new Date());
            saveOrUpdate(entity);
        }
    }
}
