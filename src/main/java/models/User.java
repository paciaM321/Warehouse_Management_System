package models;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "users")

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
 public int getUser_id() {return user_id; }
 public String getLogin() {return login; }
 public String getHaslo() {return haslo; }
 public  String getRole() {return role; }
 public  String getImie() {return imie; }
 public  String getNazwisko() {return nazwisko; }
 public  Date getData_urodzenia() {return data_urodzenia; }


 public void setUser_id(int user_id) {
  this.user_id = user_id;
 }

 public void setLogin(String login) {
  this.login = login;
 }

 public void setHaslo(String haslo) {
  this.haslo = haslo;
 }

 public void setRole(String role) {
  this.role = role;
 }

 public void setImie(String imie) {
  this.imie = imie;
 }

 public void setNazwisko(String nazwisko) {
  this.nazwisko = nazwisko;
 }

 public void setData_urodzenia(Date data_urodzenia) {
  this.data_urodzenia = data_urodzenia;
 }

 public User(int user_id, String login, String haslo, String role, String imie, String nazwisko, Date data_urodzenia) {
  this.user_id = user_id;
  this.login = login;
  this.haslo = haslo;
  this.role = role;
  this.imie = imie;
  this.nazwisko = nazwisko;
  this.data_urodzenia = data_urodzenia;
 }
}