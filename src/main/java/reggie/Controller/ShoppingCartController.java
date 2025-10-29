package reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggie.common.BaseContext;
import reggie.common.R;
import reggie.entity.ShoppingCart;
import reggie.service.ShoppingCartService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;


    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        return  R.success(shoppingCartService.list());
    }

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("shoppingCart"+shoppingCart);
        Long userId = BaseContext.getThreadLocal();
        shoppingCart.setUserId(userId);
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper=new LambdaQueryWrapper<>();
        shoppingCartWrapper.eq(ShoppingCart::getUserId,userId);
        if (dishId!=null){
            //是菜品
            shoppingCartWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //是套餐
            shoppingCartWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(shoppingCartWrapper);
        if (one!=null){
            int number = one.getNumber();
            number+=1;
            one.setNumber(number);
            shoppingCartService.updateById(one);
        }else {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            one=shoppingCart;
        }

        return  R.success(one);
    }
}
