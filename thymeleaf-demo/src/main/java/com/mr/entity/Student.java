package com.mr.entity;

/**
 * @ClassName Student
 * @Description: TODO
 * @Author gengzifang
 * @Date 2020/9/15
 * @Version V1.0
 **/
public class Student {

    String code;

    String pass;

    int age;

    String likeColor;

    public Student() {

    }


    public String getCode() {
        return code;
    }

    public String getPass() {
        return pass;
    }

    public int getAge() {
        return age;
    }

    public String getLikeColor() {
        return likeColor;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setLikeColor(String likeColor) {
        this.likeColor = likeColor;
    }

    @Override
    public String toString() {
        return "Student{" +
                "code='" + code + '\'' +
                ", pass='" + pass + '\'' +
                ", age=" + age +
                ", likeColor='" + likeColor + '\'' +
                '}';
    }
    public Student(String code, String pass, int age, String likeColor) {
        this.code = code;
        this.pass = pass;
        this.age = age;
        this.likeColor = likeColor;
    }


}
