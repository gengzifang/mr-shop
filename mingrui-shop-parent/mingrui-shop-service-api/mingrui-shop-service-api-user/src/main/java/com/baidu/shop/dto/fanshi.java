package com.baidu.shop.dto;

/**
 * @ClassName fanshi
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/10/13
 * @Version V1.0
 **/
public class fanshi {

    public static void main(String[] args) {

        //第一种方式获取Class对象
        UserDTO userDTO1 = new UserDTO();
        Class<? extends UserDTO> aClass = userDTO1.getClass();
        System.out.println(aClass + "111");

        //第二种方式获取Class对象
        Class<UserDTO> userDTOC2 = UserDTO.class;
        System.out.println(aClass == userDTOC2);

        //第三种方式获取Class对象
        try {
            Class stuClass3 = Class.forName("fanshi.UserDTO");//注意此字符串必须是真实路径，就是带包名的类路径，包名.类名
            System.out.println(stuClass3 == userDTOC2);//判断三种方式是否获取的是同一个Class对象
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
