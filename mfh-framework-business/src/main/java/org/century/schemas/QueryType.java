package org.century.schemas;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * 商品属性 Goods Property
 * <p>
 *     The maps between property ID and its meaning as follows:
 *     Property ID  Property Name   Mark

 <ul>
 <li>
 1--Code of merchandise.店内码(商品编码)<br>
 It’s a necessary property, and must be same as _goodscode field. It is the unique flag in the customer ’s retail system.
 必须有,而且与_goodscode 相同。该信息是指客户零售系统中商品的唯一标 识(比如条形码)
 </li>
 <li>
 2--Barcode.条形码
 Unnecessary.非必须
 </li>
 </ul>

 * </p>
 *
 * Created by bingshanguxue on 4/21/16.
 */
public class QueryType implements KvmSerializable{
    public static final String NAMESPACE = "http://schemas.datacontract.org/2004/07/CENTURY_ESL.EntityEX";

    public static final String _Tag = "Tag";
    public static final String _TagHistory = "TagHistory";
    public static final String _Goods = "Goods";
    public static final String _GoodsHistory = "GoodsHistory";
    public static final String _Reader = "Reader";
    public static final String _ReaderHistory = "ReaderHistory";
    public static final String _Log = "Log";
    public static final String _User = "User";
    public static final QueryType Tag = new QueryType(_Tag);
    public static final QueryType TagHistory = new QueryType(_TagHistory);
    public static final QueryType Goods = new QueryType(_Goods);
    public static final QueryType GoodsHistory = new QueryType(_GoodsHistory);
    public static final QueryType Reader = new QueryType(_Reader);
    public static final QueryType ReaderHistory = new QueryType(_ReaderHistory);
    public static final QueryType Log = new QueryType(_Log);
    public static final QueryType User = new QueryType(_User);


    public String value;

    public QueryType(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
//                return id;
//            case 1:
                return value;
            default:
                return null;
        }
    }

    @Override
    public int getPropertyCount() {
        return 1;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i) {
            case 0:
//                id = (Integer) o;
//                break;
//            case 1:
                value = (String) o;
                break;
            default:
                break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i) {
            case 0:
//                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
//                propertyInfo.name = "id";
//                break;
//            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "value";
                propertyInfo.namespace = NAMESPACE;
                break;
            default:
                break;
        }
    }
}
