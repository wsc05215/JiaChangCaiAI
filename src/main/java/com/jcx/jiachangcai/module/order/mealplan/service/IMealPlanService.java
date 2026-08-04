package com.jcx.jiachangcai.module.order.mealplan.service;

import com.jcx.jiachangcai.module.order.mealplan.entity.MealPlan;
import com.jcx.jiachangcai.module.order.mealplan.entity.MealPlanVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IMealPlanService extends IService<MealPlan> {

    MealPlan addPlan(Long userId, String planDate, String mealType, Long recipeId);

    void removePlan(Long id);

    List<MealPlanVO> getPlanList(Long userId, String date);

    List<String> getPlanDates(Long userId, Integer year, Integer month);
}
