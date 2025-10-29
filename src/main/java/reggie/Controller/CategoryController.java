package reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggie.common.R;
import reggie.entity.Category;
import reggie.service.CategoryService;
import reggie.service.DishFlavorService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("创建/修改菜品");
        categoryService.save(category);
        return R.success("创建成功");
    }
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize){
        Page<Category> pageinfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> wrapper=new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        categoryService.page(pageinfo,wrapper);
        return R.success(pageinfo);
    }
    @DeleteMapping
    public R<String> delete(Long ids){
        categoryService.remove(ids);
        return R.success("删除成功");
    }
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改菜品");
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    @GetMapping("/list")
    public  R<List<Category>> list(Category category){
        List<Category> list=new ArrayList<>();
        if (category!=null){
            LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
            queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
            list=categoryService.list(queryWrapper);
        }else {
            list = categoryService.list();
            log.info("http://localhost:8080/category/list");
        }

        return R.success(list);
    }
}
