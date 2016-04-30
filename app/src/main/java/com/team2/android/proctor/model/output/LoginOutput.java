package com.team2.android.proctor.model.output;

/**
 * Created by ashekar on 3/26/2016.
 */

public class LoginOutput {

  private long userId;
  private String isStudent;

  public LoginOutput(){

  }
  public LoginOutput(long userId, String isStudent) {
    this.userId = userId;
    this.isStudent = isStudent;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getIsStudent() {
    return isStudent;
  }

  public void setIsStudent(String isStudent) {
    this.isStudent = isStudent;
  }

  @Override
  public String toString() {
    return "LoginOutput{" +
            "userId=" + userId +
            ", isStudent='" + isStudent + '\'' +
            '}';
  }
}
