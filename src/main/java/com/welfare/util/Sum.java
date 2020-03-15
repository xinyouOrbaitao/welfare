package com.welfare.util;

import com.welfare.eumn.RelationEnum;

import java.math.BigDecimal;

/**
 * @author: chenxinyou
 * @date: 2020/2/29 3:55 下午
 * @Description:
 */
public class Sum {

    public void sum(Integer totalAmount,Integer total){
        new BigDecimal(totalAmount).divide(new BigDecimal(total)).multiply(new BigDecimal(0.6));
    }

    public void sumtwo(Integer totalAmount, RelationEnum relationEnum){
        new BigDecimal(totalAmount).multiply(relationEnum.bigDecimal).multiply(new BigDecimal(0.4));
    }
}
