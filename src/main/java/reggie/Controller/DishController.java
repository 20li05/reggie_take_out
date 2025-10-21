package reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggie.common.R;
import reggie.dto.DishDto;
import reggie.entity.Category;
import reggie.entity.Dish;
import reggie.service.CategoryService;
import reggie.service.DishFlavorService;
import reggie.service.DishService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dto) {
        log.info(dto.toString());
        dishService.saveWithFlavor(dto);
        return R.success("成功");
    }

    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        //wrapper.like(!name.isEmpty(), Dish::getName, name);
        wrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, wrapper);
        Page<DishDto> dtoInfo = new Page<>(page, page);

        BeanUtils.copyProperties(pageInfo, dtoInfo, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = new ArrayList<>();


        for (Dish dish : records) {
            Long id = dish.getCategoryId();
            Category category = categoryService.getById(id);
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(dish, dto);
            dto.setCategoryName(category.getName());
            list.add(dto);
        }

        dtoInfo.setRecords(list);
        return R.success(dtoInfo);
    }
}
