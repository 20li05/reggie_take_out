package reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggie.dto.DishDto;
import reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dto);
    DishDto getByIdWithFlavor(Long id);
    void updateWithFlavor(DishDto dto);
}
