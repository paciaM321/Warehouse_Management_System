package controllers;

import java.util.Date;

public class User {
 private int user_id;
 private String login;
 private String haslo;
 private String role;
 private String imie;
 private String nazwisko;
 private Date data_urodzenia;

 public User(String login, String haslo,String role){
  this.login = login;
  this.haslo = haslo;
  this.role = role;
 }
 //getery
 public String getLogin() {return login; }
 public String getHaslo() {return haslo; }
 public  String getRole() {return role; }





}