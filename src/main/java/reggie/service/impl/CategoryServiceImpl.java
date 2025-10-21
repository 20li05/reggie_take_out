package reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reggie.common.CustomException;
import reggie.entity.Category;
import reggie.entity.Dish;
import reggie.entity.Setmeal;
import reggie.mapper.CategoryMapper;
import reggie.service.CategoryService;
import reggie.service.DishService;
import reggie.service.SetmealService;
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category > implements CategoryService {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishService dishService;
    @Override
    public void remove(Long id) {
        log.info("使用了自定义remove方法");
        LambdaQueryWrapper<Dish> dishWrapper=new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId,id);
        long dishCount = dishService.count(dishWrapper);
        //如果关联菜品
        if (dishCount>0){
            throw new CustomException("关联了菜品,不能删除");
        }
        //如果关联套餐
        LambdaQueryWrapper<Setmeal> setmealWrapper=new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId,id);
        long setmealCount = setmealService.count(setmealWrapper);
        if (setmealCount>0){
            throw new CustomException("关联了套餐,不能删除");
        }
        super.removeById(id);
    }
}
