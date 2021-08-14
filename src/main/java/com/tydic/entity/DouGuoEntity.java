package com.tydic.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author 鸭屮
 * @version 1.0
 * @date 2021/8/11 11:20
 */
@Data
public class DouGuoEntity {
    /**
     * 豆果食物菜名
     */
    @ExcelProperty("食物名称")
    String foodName;
}
