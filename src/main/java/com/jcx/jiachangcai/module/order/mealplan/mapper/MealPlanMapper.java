package com.jcx.jiachangcai.module.order.mealplan.mapper;

import com.jcx.jiachangcai.module.order.mealplan.entity.MealPlan;
import com.jcx.jiachangcai.module.order.mealplan.entity.MealPlanVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MealPlanMapper extends BaseMapper<MealPlan> {

    List<MealPlanVO> selectPlanWithRecipe(@Param("userId") Long userId, @Param("planDate") String planDate);

    List<String> selectPlanDates(@Param("userId") Long userId, @Param("yearMonth") String yearMonth);
}
