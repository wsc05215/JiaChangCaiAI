package com.jcx.jiachangcai.module.ingredient.controller;


import com.jcx.jiachangcai.module.ingredient.entity.Ingredient;
import com.jcx.jiachangcai.module.ingredient.service.IIngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ingredient")
public class IngredientController {
    @Autowired
    private IIngredientService service;

    @PostMapping("/addIngredient")
    public void addIngredient(Long userId, String name, String category, LocalDateTime createTime) {
        service.addIngredient(userId, name, category, createTime);
    }

    @GetMapping("/list")
    public List<Ingredient> listIngredients(Long userId) {
        return service.listByUserId(userId);
    }

    @GetMapping("/stats")
    public Map<String, Object> stats(@RequestParam Long userId) {
        long total = service.countByUserId(userId);
        long nearExpiry = service.countNearExpiry(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("nearExpiry", nearExpiry);
        return result;
    }

    @DeleteMapping("/{id}")
    public String deleteIngredient(@PathVariable Long id) {
        service.deleteIngredient(id);
        return "ok";
    }
}
