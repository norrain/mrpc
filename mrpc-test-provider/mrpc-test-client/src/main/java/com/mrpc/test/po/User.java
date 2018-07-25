package com.mrpc.test.po;

import java.io.Serializable;

/**
 * 所有的PO类必须实现序列化Serializable
 * @author mark.z
 */
public class User implements Serializable {

    private String name;

    private Integer age;

    private Boolean sex;

    public User(String name,Integer age,Boolean sex){
        this.name = name;
        this.age = age;
        this.sex = sex;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean isSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                '}';
    }
}
