package com.jcx.jiachangcai.module.recipe.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcx.jiachangcai.module.recipe.entity.Recipe;
import com.jcx.jiachangcai.module.recipe.mapper.RecipeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
public class CoverImageMigration implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CoverImageMigration.class);
    private static final String UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads", "recipes").toString();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String VIDEO_DIR =
            Paths.get(System.getProperty("user.dir"), "uploads", "recipes", "video").toString();
    @Autowired
    private RecipeMapper recipeMapper;

    @Override
    public void run(String... args) {
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            List<Recipe> recipes = recipeMapper.selectAllRecipe();
            int converted = 0;
            for (Recipe recipe : recipes) {
                String coverImages = recipe.getCoverImages();
                if (coverImages == null || coverImages.isEmpty()) continue;
                if (!coverImages.contains("data:image/")) continue;

                try {
                    List<String> imageList = mapper.readValue(coverImages, List.class);
                    List<String> newUrls = new ArrayList<>();
                    for (Object item : imageList) {
                        String str = item.toString();
                        if (str.startsWith("data:image/")) {
                            String url = convertToFile(str);
                            newUrls.add(url);
                        } else {
                            newUrls.add(str);
                        }
                    }
                    recipe.setCoverImages(mapper.writeValueAsString(newUrls));
                    recipeMapper.updateById(recipe);
                    converted++;
                    log.info("已转换 recipeId={} title={}", recipe.getRecipeId(), recipe.getTitle());
                } catch (Exception e) {
                    log.warn("转换 recipeId={} 失败: {}", recipe.getRecipeId(), e.getMessage());
                }
            }
            log.info("CoverImageMigration 完成: 共转换 {} 条菜谱的封面图片", converted);
        } catch (Exception e) {
            log.error("CoverImageMigration 执行失败", e);
        }
    }

    private String convertToFile(String dataUri) throws Exception {
        // data:image/jpeg;base64,xxxxx
        String[] parts = dataUri.split(";base64,");
        String mimePart = parts[0];  // data:image/jpeg
        String base64Data = parts[1];

        String ext = mimePart.contains("png") ? ".png"
                : mimePart.contains("gif") ? ".gif"
                : mimePart.contains("webp") ? ".webp"
                : ".jpg";

        byte[] bytes = Base64.getDecoder().decode(base64Data);
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        Files.write(Paths.get(UPLOAD_DIR, filename), bytes);
        return "/uploads/recipes/" + filename;
    }
}
