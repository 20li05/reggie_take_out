package reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggie.common.BaseContext;
import reggie.entity.AddressBook;
import reggie.mapper.AddressBookMapper;
import reggie.service.AddressBookService;
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Override
    public void setDefault(AddressBook addressBook) {
        // 首相把所有地址的 isDefault设置为 0
        Long userId = BaseContext.getThreadLocal();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId,userId);
        AddressBook ad = new AddressBook();
        ad.setIsDefault(false);
        update(ad, wrapper);
        // 把当前id地址设置为1
        addressBook.setIsDefault(true);
        updateById(addressBook);
    }
}
