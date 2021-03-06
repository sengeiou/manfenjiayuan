package org.century.schemas;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
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
public class GoodsInfoEXArray implements KvmSerializable, Serializable{
    public static final String NAMESPACE = "http://schemas.datacontract.org/2004/07/CENTURY_ESL.EntityEX";

    private ArrayOfGoodsInfoEX ArrayGoodsInfoEx;


    public ArrayOfGoodsInfoEX getArrayGoodsInfoEx() {
        return ArrayGoodsInfoEx;
    }

    public void setArrayGoodsInfoEx(ArrayOfGoodsInfoEX arrayGoodsInfoEx) {
        ArrayGoodsInfoEx = arrayGoodsInfoEx;
    }

    @Override
    public Object getProperty(int i) {
        if (i == 0){
            return ArrayGoodsInfoEx;
        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 1;
    }

    @Override
    public void setProperty(int i, Object o) {
        if (i == 0){
            ArrayGoodsInfoEx = (ArrayOfGoodsInfoEX) o;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        if (i == 0){
            propertyInfo.type = ArrayOfGoodsInfoEX.class;
            propertyInfo.name = "ArrayGoodsInfoEx";
            propertyInfo.namespace = NAMESPACE;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for(int i = 0; i < this.getPropertyCount(); ++i) {
            Object prop = getProperty(i);
            if (prop == null){
                continue;
            }
            if(prop instanceof PropertyInfo) {
                sb.append("").append(((PropertyInfo)prop).getName()).append("=").append(this.getProperty(i)).append("; ");
            } else {
                sb.append(prop.toString());
            }
        }
        sb.append("]");

        return sb.toString();
    }
}
