package reggie.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggie.common.R;
import reggie.dto.DishDto;
import reggie.dto.SetmealDto;
import reggie.entity.Category;
import reggie.entity.Dish;
import reggie.entity.Setmeal;
import reggie.entity.SetmealDish;
import reggie.service.CategoryService;
import reggie.service.SetmealDishService;
import reggie.service.SetmealService;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/setmeal")
@RestController
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto dto) {
        setmealService.saveWithDish(dto);
        return R.success("新增成功");

    }

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();

        setmealWrapper.like(name != null, Setmeal::getName, name);
        setmealWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, setmealWrapper);

        Page<SetmealDto> dtoInfo = new Page<>(page, pageSize);
        BeanUtils.copyProperties(pageInfo, dtoInfo, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> dtoList = new ArrayList<>();
        for (Setmeal setmeal : records) {
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, dto);
            dto.setCategoryName(categoryService.getById(setmeal.getCategoryId()).getName());
            dtoList.add(dto);
        }
        dtoInfo.setRecords(dtoList);
        return R.success(dtoInfo);
    }

    @DeleteMapping
    public R<String> remove(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }
    @GetMapping("/list")
    public R<List<SetmealDto>> list(Setmeal now){
        LambdaQueryWrapper<Setmeal>setmealWrapper =new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId,now.getCategoryId()).eq(Setmeal::getStatus,1);
        List<Setmeal> setmeals = setmealService.list(setmealWrapper);
        List<SetmealDto> dtoList=new ArrayList<>();
        for (Setmeal setmeal:setmeals){
            SetmealDto dto=new SetmealDto();
            BeanUtils.copyProperties(setmeal,dto);
            dto.setCategoryName(setmeal.getName());
            LambdaQueryWrapper<SetmealDish> setmealDishWrapper=new LambdaQueryWrapper<>();
            setmealDishWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
            setmealDishWrapper.orderByDesc(SetmealDish::getSort).orderByAsc(SetmealDish::getCreateTime);
            List<SetmealDish> dishes = setmealDishService.list(setmealDishWrapper);
            dto.setSetmealDishes(dishes);
            dtoList.add(dto);
        }
        return R.success(dtoList);
    }
    @GetMapping("/dish/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto dto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,dto);
        dto.setCategoryName(setmeal.getName());
        LambdaQueryWrapper<SetmealDish> setmealDishWrapper=new LambdaQueryWrapper<>();
        setmealDishWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        setmealDishWrapper.orderByDesc(SetmealDish::getSort).orderByAsc(SetmealDish::getCreateTime);
        List<SetmealDish> dishes = setmealDishService.list(setmealDishWrapper);
        dto.setSetmealDishes(dishes);
        return R.success(dto);
    }















}
