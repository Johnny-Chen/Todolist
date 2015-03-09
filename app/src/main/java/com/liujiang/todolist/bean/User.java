package com.liujiang.todolist.bean;

public class User {

	private String username;
	private String password;
	private String sex;
	private String age;
	private String photo;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public User(String username, String password, String sex, String age,
			String photo) {
		super();
		this.username = username;
		this.password = password;
		this.sex = sex;
		this.age = age;
		this.photo = photo;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

 
}
