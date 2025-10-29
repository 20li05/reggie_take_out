package reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reggie.dto.DishDto;
import reggie.entity.Dish;
import reggie.entity.DishFlavor;
import reggie.mapper.DishMapper;
import reggie.service.CategoryService;
import reggie.service.DishFlavorService;
import reggie.service.DishService;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dto) {
        List<DishFlavor> flavors = dto.getFlavors();
        for (DishFlavor dishFlavor:flavors){
            dishFlavor.setDishId(dto.getId());
        }
        dishFlavorService.saveBatch(flavors);
        this.save(dto);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish byId = this.getById(id);
        DishDto dto=new DishDto();
        BeanUtils.copyProperties(byId,dto);
        dto.setCategoryName(categoryService.getById(byId.getCategoryId()).getName());
        LambdaQueryWrapper<DishFlavor> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,byId.getId());
        dto.setFlavors(dishFlavorService.list(wrapper));
        return dto;
    }

    @Override
    public void updateWithFlavor(DishDto dto) {
        this.updateById(dto);
        List<DishFlavor> flavors = dto.getFlavors();
        for (DishFlavor dishFlavor:flavors){
            dishFlavor.setDishId(dto.getId());
        }
        dishFlavorService.updateBatchById(flavors);
    }


}
