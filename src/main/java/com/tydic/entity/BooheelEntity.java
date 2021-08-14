package com.tydic.entity;

import cn.hutool.core.annotation.Alias;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BooheelEntity {

    /**
     * 食品分类
     */
    @ExcelProperty("食品分类")
    String sort;

//    /**
//     * 食物名称
//     */
//    @ExcelProperty("食物名称")
//    String name;

    /**
     * 薄荷食物名称
     */
    @ExcelProperty("薄荷食物名称")
    String boolheelName;

//    /**
//     * 热量和减肥功效
//     */
//    String content;
@ExcelProperty("别名")
    String alias;
    @ExcelProperty("热量")
    String heat;
    @ExcelProperty("分类")
    String classification;
//    /**
//     * 营养信息
//     */
//    String nutrTag;
@ExcelProperty("热量(大卡)")
    String reliang;
    @ExcelProperty("碳水化合物(克)")
    String tanshui;
    @ExcelProperty("脂肪(克)")
    String zhifang;
    @ExcelProperty("蛋白质(克)")
    String danbai;
    @ExcelProperty("纤维素(克)")
    String qianwei;

//    /**
//     * 度量单位
//     */
//    String widgetUnit;

//    /**
//     * 原料
//     */
//    String widgetMore;
//@ExcelProperty("主料")
//    String zhuliao;
//    @ExcelProperty("辅料")
//    String fuliao;
//    @ExcelProperty("调料")
//    String tiaoliao;



}
