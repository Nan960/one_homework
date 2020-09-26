package com.lagou.edu.utils;

import com.alibaba.druid.util.StringUtils;
import com.lagou.edu.annotation.MyAutowired;
import com.lagou.edu.annotation.MyService;
import com.lagou.edu.annotation.MyTransactional;
import com.lagou.edu.factory.ProxyFactory;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static com.lagou.edu.factory.BeanFactory.getBean;

/**
 * @author yusheng.gan
 * @description: TODO
 * @date 2020/9/22 9:37
 */
public class MyApplicationContext {

    private HashMap<String, Object> beans = new HashMap<>();


    public MyApplicationContext(String packageName) {
        initBeans(packageName);
    }

    private void initBeans(String packageName){
        // 扫描包，通过反射技术实例化对象并且存储待用（map集合）
        try{
            //扫描获取反射对象集合
            Reflections reflections = new Reflections(packageName);
            // 获取使用MyService的注解类集合
            Set<Class<?>> servicesTypesAnnotatedWith = reflections.getTypesAnnotatedWith(MyService.class);
            for (Class<?> c : servicesTypesAnnotatedWith) {
                // 通过反射技术实例化对象
                Object bean = c.newInstance();
                MyService annotation = c.getAnnotation(MyService.class);

                //对象ID在service注解有value时用value，没有时用类名
                if(StringUtils.isEmpty(annotation.value())){
                    //由于getName获取的是全限定类名，所以要分割去掉前面包名部分
                    String[] names = c.getName().split("\\.");
                    beans.put(names[names.length-1], bean);
                }else{
                    // 将MyService里面的注解 首字母转换成大写放到beans中 在下面MyAutowired中setter方法赋值中得到体现
                    String valueName = annotation.value();
                    valueName = valueName.substring(0,1).toUpperCase() + valueName.substring(1);
                    beans.put(valueName, bean);
                }
            }
            // 实例化完成之后维护对象的依赖关系 MyAutowired，检查哪些对象需要传值进入，
            for(Map.Entry<String, Object> service : beans.entrySet()){
                Object o = service.getValue();
                Class clazz = o.getClass();
                //获取所有的变量
                Field[] fields = clazz.getDeclaredFields();
                //遍历属性，若持有 MyAutowired 注解则注入
                for (Field field : fields) {
                    //判断是否是使用注解的参数
                    if (field.isAnnotationPresent(MyAutowired.class)) {
                        String[] names = field.getType().getName().split("\\.");
                        String name = names[names.length-1];
                        // MyAutowired 注解的位置需要set方法，方便c.getMethods()获取
                        Method[] methods = clazz.getMethods();
                        for (int j = 0; j < methods.length; j++) {
                            Method method = methods[j];
                            // 该方法就是 setAccountDao(AccountDao accountDao)
                            if(method.getName().equalsIgnoreCase("set" + name)) {
                                method.invoke(o,beans.get(name));
                                break;
                            }
                        }
                    }
                }
                //判断对象类是否持有 MyTransactional 注解，若有则修改对象为代理对象
                if(clazz.isAnnotationPresent(MyTransactional.class)){
                    // 获取代理工厂
                    ProxyFactory proxyFactory = (ProxyFactory) getBeans("ProxyFactory");

                    // 获取类c实现的所有接口
                    Class[] face = clazz.getInterfaces();
                    // 判断对象是否实现接口
                    if(face!=null && face.length>0){
                        // 实现使用JDK
                        o = proxyFactory.getJdkProxy(o);
                    }else{
                        // 没实现使用CGLIB
                        o = proxyFactory.getCglibProxy(o);
                    }
                }

                // 把处理之后的object重新放到map中
                beans.put(service.getKey(),o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象
     * @param beanName
     * @return
     */
    public Object getBeans(String beanName){
        Object o = beans.get(beanName);
        return o;
    }
}
