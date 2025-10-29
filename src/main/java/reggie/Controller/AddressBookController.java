package reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggie.common.BaseContext;
import reggie.common.R;
import reggie.entity.AddressBook;
import reggie.service.AddressBookService;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public R add(@RequestBody AddressBook addressBook){
        Long userId = BaseContext.getThreadLocal();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success("添加地址成功！");
    }

    /**
     * 查询当前用户所有地址
     * @return
     */
    @GetMapping("/list")
    public R getList(){
        Long userId = BaseContext.getThreadLocal();
        List<AddressBook> list = addressBookService.list(new LambdaQueryWrapper<AddressBook>()
                .orderByDesc(AddressBook::getIsDefault)
                .eq(AddressBook::getUserId,userId));
        return R.success(list);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R changeDefault(@RequestBody AddressBook addressBook){
        addressBookService.setDefault(addressBook);
        return R.success("默认地址设置成功!");
    }

    /**
     * 根据id获取地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R getAddress(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    /**
     * 获取默认地址
     * @return
     */
    @GetMapping("/default")
    public R getDefault() {
        Long userId = BaseContext.getThreadLocal();
        AddressBook one = addressBookService.getOne(
                new LambdaQueryWrapper<AddressBook>()
                        .eq(AddressBook::getIsDefault, true)
                        .eq(AddressBook::getUserId, userId));
        return R.success(one);
    }
}
