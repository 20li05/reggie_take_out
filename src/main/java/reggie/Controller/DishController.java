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
import reggie.entity.DishFlavor;
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
        wrapper.like(!name.isEmpty(), Dish::getName, name);
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

    @GetMapping("/{id}")
    public R<DishDto> byId(@PathVariable Long id){
        return  R.success(dishService.getByIdWithFlavor(id));
    }

    @PutMapping
    public R<String> byId(@RequestBody DishDto dto){
        dishService.updateWithFlavor(dto);
        return R.success("保存成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        log.info("list"+dish);
        LambdaQueryWrapper<Dish> wrapper=new LambdaQueryWrapper<>();
        if (dish!=null){
            wrapper.eq(Dish::getCategoryId,dish.getCategoryId());
        }
        wrapper.eq(Dish::getStatus,1);
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(wrapper);
        List<DishDto> dtoList=new ArrayList<>();
        for (Dish now:list){
            DishDto dto=new DishDto();
            BeanUtils.copyProperties(now,dto);
            Long id = now.getCategoryId();
            Category category = categoryService.getById(id);
            dto.setCategoryName(category.getName());
            LambdaQueryWrapper<DishFlavor> dishFlavorWrapper=new LambdaQueryWrapper<>();
            dishFlavorWrapper.eq(DishFlavor::getDishId,dto.getId());
            dto.setFlavors(dishFlavorService.list(dishFlavorWrapper));
            dtoList.add(dto);
        }
        return R.success(dtoList);
    }
}
