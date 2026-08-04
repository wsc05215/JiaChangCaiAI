package com.jcx.jiachangcai.module.order.mealplan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.order.mealplan.entity.MealPlan;
import com.jcx.jiachangcai.module.order.mealplan.entity.MealPlanVO;
import com.jcx.jiachangcai.module.order.mealplan.mapper.MealPlanMapper;
import com.jcx.jiachangcai.module.order.mealplan.service.IMealPlanService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MealPlanServiceImpl extends ServiceImpl<MealPlanMapper, MealPlan> implements IMealPlanService {

    @Autowired
    private MealPlanMapper mapper;

    @Override
    public MealPlan addPlan(Long userId, String planDate, String mealType, Long recipeId) {
        // 检查是否已添加
        LambdaQueryWrapper<MealPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MealPlan::getUserId, userId)
               .eq(MealPlan::getPlanDate, LocalDate.parse(planDate))
               .eq(MealPlan::getMealType, mealType)
               .eq(MealPlan::getRecipeId, recipeId);
        MealPlan exist = mapper.selectOne(wrapper);
        if (exist != null) {
            return exist;
        }

        MealPlan plan = new MealPlan();
        plan.setUserId(userId);
        plan.setPlanDate(LocalDate.parse(planDate));
        plan.setMealType(mealType);
        plan.setRecipeId(recipeId);
        plan.setSortOrder(0);
        plan.setCreateTime(LocalDateTime.now());
        mapper.insert(plan);
        return plan;
    }

    @Override
    public void removePlan(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public List<MealPlanVO> getPlanList(Long userId, String date) {
        return mapper.selectPlanWithRecipe(userId, date);
    }

    @Override
    public List<String> getPlanDates(Long userId, Integer year, Integer month) {
        String yearMonth = String.format("%d-%02d", year, month);
        return mapper.selectPlanDates(userId, yearMonth);
    }
}
