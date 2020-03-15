package com.welfare.eumn;

import java.math.BigDecimal;

/**
 * @author:
 * @date: 2020/2/29 3:46
 * @Description: 关系占比
 */
public enum  RelationEnum {

    family(5,new BigDecimal(0.2)),
    relative(4,new BigDecimal(0.2)),
    friend(3,new BigDecimal(0.2)),
    Classmates(2,new BigDecimal(0.2)),
    lover(1,new BigDecimal(0.2));

    public int type;
    public BigDecimal bigDecimal;
    RelationEnum(int type,BigDecimal bigDecimal){
        this.bigDecimal = bigDecimal;
        this.type = type;
    }
    public BigDecimal getValue() {
        return bigDecimal;
    }
    public int getType(){return type;}

    public static RelationEnum valueToEnum(int value){
        for (RelationEnum obj : RelationEnum.values()) {
            if (obj.getType()==(value)) {
                return obj;
            }
        }
        return null;
    }
    public static int getValueToType(int value){
        for (RelationEnum obj : RelationEnum.values()) {
            if (obj.getType()==(value)) {
                return obj.getType();
            }
        }
        return 0;
    }
}
