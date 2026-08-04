package com.jcx.jiachangcai.module.order.mealplan.controller;

import com.jcx.jiachangcai.module.order.mealplan.entity.MealPlan;
import com.jcx.jiachangcai.module.order.mealplan.entity.MealPlanVO;
import com.jcx.jiachangcai.module.order.mealplan.service.IMealPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meal-plan")
public class MealPlanController {

    @Autowired
    private IMealPlanService service;

    @PostMapping("/add")
    public MealPlan addPlan(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String planDate = body.get("planDate").toString();
        String mealType = body.get("mealType").toString();
        Long recipeId = Long.valueOf(body.get("recipeId").toString());
        return service.addPlan(userId, planDate, mealType, recipeId);
    }

    @DeleteMapping("/remove")
    public String removePlan(@RequestParam Long id) {
        service.removePlan(id);
        return "ok";
    }

    @GetMapping("/list")
    public List<MealPlanVO> getPlanList(@RequestParam Long userId, @RequestParam String date) {
        return service.getPlanList(userId, date);
    }

    @GetMapping("/dates")
    public List<String> getPlanDates(@RequestParam Long userId, @RequestParam Integer year, @RequestParam Integer month) {
        return service.getPlanDates(userId, year, month);
    }
}
