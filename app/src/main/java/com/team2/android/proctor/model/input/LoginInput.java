package com.team2.android.proctor.model.input;

/**
 * Created by ashekar on 3/26/2016.
 */
public class LoginInput {
  private String username;
  private String password;

  public LoginInput(String username, String password) {
    this.username = username;
    this.password = password;
  }

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

  @Override
  public String toString() {
    return "LoginInput{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            '}';
  }
}
