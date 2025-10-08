package reggie.common;

public class ThreadLocalUtil {

    //创建线程变量对象
    private static ThreadLocal<Long> threadLocal = new ThreadLocal();


    //存入数据的方法
    public static void setCurrentId(Long empId){
        threadLocal.set(empId);
    }
    //获取数据的方法
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
