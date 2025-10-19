package reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reggie.common.R;
import reggie.entity.Employee;
import reggie.service.EmployeeService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("用户开始登录");
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        QueryWrapper<Employee> empWrapper = new QueryWrapper<>();
        empWrapper.eq("userName", employee.getUsername());
        Employee emp = employeeService.getOne(empWrapper);
        if (emp == null) {
            return R.error("不存在用户");
        }
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        if (emp.getStatus() == 0) {
            return R.error("用户禁用");
        }
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }



    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request) {
        log.info("增加员工" + employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        /*
        *employee.setCreateTime(LocalDateTime.now());
        *employee.setUpdateTime(LocalDateTime.now());
        */

        Long empId= (Long)request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        boolean save = employeeService.save(employee);
        if (save){
            return R.success("添加成功");
        }else {
            return R.error("添加sb");
        }
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pagesize{},name={}",page,pageSize,name);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        wrapper.orderByDesc(Employee::getUpdateTime);
        Page<Employee> pageInfo=new Page<>(page,pageSize);
        employeeService.page(pageInfo,wrapper);
        return R.success(pageInfo);
    }
    @PutMapping
    public R<String> update(@RequestBody Employee employee,HttpServletRequest request){
        employee.setStatus(employee.getStatus().equals(0l)?1:0);
        log.info(employee.getStatus()+"9999999999");
        employee.setUpdateTime(LocalDateTime.now());
        Long id = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(id);
        employeeService.updateById(employee);
        return R.success("修改成功");
    }
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee byId = employeeService.getById(id);
        return R.success(byId);
    }
}
