package com.tydic.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author 鸭屮
 * @version 1.0
 * @date 2021/8/9 10:13
 */
@Data
public class XinshipuEntity {

    /**
     * 食品一级分类
     */
    @ExcelProperty("食品一级分类")
    String category;

//    /**
//     * 食品二级分类
//     */
//    @ExcelProperty("食品二级分类")
//    String category2;

    /**
     * 食品名称
     */
    @ExcelProperty("食品名称")
    String foodName;

    /**
     * 食品材料
     */
    @ExcelProperty("食品材料")
    String material;

    /**
     * 做法
     */
    @ExcelProperty("食品做法")
    String cook;
}
