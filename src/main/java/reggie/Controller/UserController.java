package reggie.Controller;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reggie.common.BaseContext;
import reggie.common.R;
import reggie.entity.User;
import reggie.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/code")
    public R<String> sendMsg(String phone, HttpSession httpSession){
        //String phone=user.getPhone();
        if (!StringUtils.isEmpty(phone)){
            //String code = Integer.toString(ThreadLocalRandom.current().nextInt(1000, 10000));
            String code ="5555";
            httpSession.setAttribute(phone,code);
            log.info("验证码为="+code);
            return R.success("验证码为"+code);
        }
        return R.error("发送验证码失败");
    }

    @PostMapping("/login")
    @Transactional
    public R<User> login(@RequestBody Map<String, String> requestMap,HttpSession httpSession){

        Long userId = BaseContext.getThreadLocal();
        String phone = requestMap.get("phone");
        String code = requestMap.get("code");
        //跳过验证
        httpSession.setAttribute(phone,"5555");
        log.info("userId="+userId);
        if (code!=null&&code.equals(httpSession.getAttribute(phone))){
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user==null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            log.info("userId="+user.getId());
            httpSession.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("验证码错误");
    }
}
