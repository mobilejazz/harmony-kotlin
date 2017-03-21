package com.worldreader.core.domain.model.user;

public class ReadToKidsProviderData
    implements RegisterProviderData<ReadToKidsProviderData.DomainReadToKidsData> {

  private final DomainReadToKidsData registerData;

  public ReadToKidsProviderData(String username, String password, String email,
      String activatorCode, int gender, int age) {
    this.registerData =
        new DomainReadToKidsData(username, password, email, activatorCode, gender, age);
  }

  @Override public DomainReadToKidsData get() {
    return registerData;
  }

  public static class DomainReadToKidsData {

    private final String username;
    private final String password;
    private final String email;
    private final String activatorCode;
    private final int gender;
    private final int age;

    public DomainReadToKidsData(final String username, final String password, final String email,
        final String activatorCode, final int gender, final int age) {
      this.username = username;
      this.password = password;
      this.email = email;
      this.activatorCode = activatorCode;
      this.gender = gender;
      this.age = age;
    }

    public String getUsername() {
      return username;
    }

    public String getPassword() {
      return password;
    }

    public String getEmail() {
      return email;
    }

    public int getGender() {
      return gender;
    }

    public String getActivatorCode() {
      return activatorCode;
    }

    public int getAge() {
      return age;
    }
  }
}
