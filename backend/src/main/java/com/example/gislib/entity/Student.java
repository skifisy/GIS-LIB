package com.example.gislib.entity;

import javax.persistence.*;

@Entity @Table(name = "student") public class Student {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

 private
  String name;
 private
  String pwd;
 private
  String phone;
 private
  Boolean sex;

 public
  Student() {}

  // getters/setters
 public
  Long getId() { return id; }
 public
  void setId(Long id) { this.id = id; }
 public
  String getName() { return name; }
 public
  void setName(String name) { this.name = name; }
 public
  String getPwd() { return pwd; }
 public
  void setPwd(String pwd) { this.pwd = pwd; }
 public
  String getPhone() { return phone; }
 public
  void setPhone(String phone) { this.phone = phone; }
 public
  Boolean getSex() { return sex; }
 public
  void setSex(Boolean sex) { this.sex = sex; }
}
