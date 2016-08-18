package com.bingshanguxue.pda.database.service;

import com.bingshanguxue.pda.database.entity.InvSendIoGoodsEntity;
import com.mfh.framework.api.invFindOrder.InvFindOrderItem;
import com.mfh.framework.api.scGoodsSku.ScGoodsSku;
import com.bingshanguxue.pda.database.dao.InvSendIoGoodsDao;
import com.mfh.comn.bean.PageInfo;
import com.mfh.framework.core.logger.ZLogger;
import com.mfh.framework.core.logic.ServiceFactory;
import com.mfh.framework.core.service.BaseService;
import com.mfh.framework.core.service.DataSyncStrategy;
import com.mfh.framework.core.utils.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 发货拣货－商品
 * Created by Nat.ZZN(bingshanguxue) on 15-09-06..
 */
public class InvSendIoGoodsService extends BaseService<InvSendIoGoodsEntity, String, InvSendIoGoodsDao> {
    @Override
    protected Class<InvSendIoGoodsDao> getDaoClass() {
        return InvSendIoGoodsDao.class;
    }

    @Override
    public DataSyncStrategy getDataSyncStrategy() {
        return null;
    }

    private static InvSendIoGoodsService instance = null;
    /**
     * 返回 IMConversationService 实例
     * @return
     */
    public static InvSendIoGoodsService get() {
        String lsName = InvSendIoGoodsService.class.getName();
        if (ServiceFactory.checkService(lsName))
            instance = ServiceFactory.getService(lsName);
        else {
            instance = new InvSendIoGoodsService();//初始化登录服务
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

    public InvSendIoGoodsEntity getEntityById(String id) {
        try {
            return getDao().getEntityById(id);
        } catch (Exception e) {
            ZLogger.e(e.toString());
            return null;
        }
    }

//    public void save(InvSendIoGoodsEntity entity) {
//        getDao().save(entity);
//    }

    public void update(InvSendIoGoodsEntity entity) {
        getDao().update(entity);
    }

    public void saveOrUpdate(InvSendIoGoodsEntity entity) {
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
    public List<InvSendIoGoodsEntity> queryAll(PageInfo pageInfo) {
        return getDao().queryAll(pageInfo);
    }

    public List<InvSendIoGoodsEntity> queryAll() {
        return getDao().queryAll();
    }

    public List<InvSendIoGoodsEntity> queryAllBy(String strWhere) {
        return getDao().queryAllBy(strWhere);
    }

    public List<InvSendIoGoodsEntity> queryAllByDesc(String strWhere) {
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
    public InvSendIoGoodsEntity queryEntityBy(String barcode) {
        if (StringUtils.isEmpty(barcode)) {
            return null;
        }

        List<InvSendIoGoodsEntity> entityList = queryAllBy(String.format("barcode = '%s'", barcode));
        if (entityList != null && entityList.size() > 0) {
            return entityList.get(0);
        }

        return null;
    }



    /**
     * 保存拣货单明细
     */
    public void saveInvFindOrderItems(List<InvFindOrderItem> orderItems) {
        clear();

        if (orderItems == null || orderItems.size() <= 0){
            return;
        }

        for (InvFindOrderItem item : orderItems) {
            InvSendIoGoodsEntity entity = new InvSendIoGoodsEntity();
            entity.setCreatedDate(new Date());//使用当前日期，表示加入购物车信息
            entity.setUpdatedDate(new Date());

            entity.setProSkuId(item.getProSkuId());
            entity.setChainSkuId(item.getTenantSkuId());
            entity.setProductName(item.getProductName());
            entity.setUnit(item.getUnitSpec());

            entity.setPrice(item.getPrice());
            // TODO: 5/13/16 PC上还是用的totalCount字段
            entity.setQuantityCheck(item.getQuantityCheck());
            entity.setAmount(entity.getPrice() * entity.getQuantityCheck());

            entity.setBarcode(item.getBarcode());
            entity.setProviderId(item.getProviderId());
            entity.setIsPrivate(item.getIsPrivate());

            saveOrUpdate(entity);
        }
    }


    /**
     * 验收商品
     */
    public void inspect(InvSendIoGoodsEntity entity, Double price, Double quantity) {
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

        entity.setUpdatedDate(new Date());
        saveOrUpdate(entity);
    }

    /**
     * 拒收商品
     */
    public void reject(InvSendIoGoodsEntity entity) {
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
            entity.setUpdatedDate(new Date());
            saveOrUpdate(entity);
        }
    }

    /**
     * 填充商品批发价格
     */
    public void infusePriceList(List<ScGoodsSku> scGoodsSkus) {
        if (scGoodsSkus == null || scGoodsSkus.size() < 1) {
            return;
        }

        for (ScGoodsSku scGoodsSku : scGoodsSkus){
            InvSendIoGoodsEntity entity = queryEntityBy(scGoodsSku.getBarcode());
            if (entity == null){
                continue;
            }

            entity.setPrice(scGoodsSku.getCostPrice());
            entity.setAmount(entity.getQuantityCheck() * entity.getPrice());
            saveOrUpdate(entity);
        }
    }
}
