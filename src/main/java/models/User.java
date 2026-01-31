package models;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private int id;

 private String login;
 private String password;
 private String role;

 @Column(name = "first_name")
 private String firstName;

 @Column(name = "last_name")
 private String lastName;

 public User() {}

 public User(String login, String password, String role, String firstName, String lastName) {
  this.login = login;
  this.password = password;
  this.role = role;
  this.firstName = firstName;
  this.lastName = lastName;
 }

 // Gettery i Settery
 public int getId() { return id; }
 public String getLogin() { return login; }
 public String getPassword() { return password; }
 public String getRole() { return role; }
 public String getFirstName() { return firstName; }
 public String getLastName() { return lastName; }
 public void setLastName(String lastName) { this.lastName = lastName; }
 public void setId(int id) { this.id = id; }
 public void setLogin(String login) { this.login = login; }
 public void setPassword(String password) { this.password = password; }
 public void setRole(String role) { this.role = role; }
 public void setFirstName(String firstName) { this.firstName = firstName; }


}