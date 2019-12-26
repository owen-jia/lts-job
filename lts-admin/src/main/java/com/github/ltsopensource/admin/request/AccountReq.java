package com.github.ltsopensource.admin.request;

/**
 * 账户参数
 * @author: Owen Jia
 * @time: 2019/3/19 13:42
 */
public class AccountReq extends PaginationReq {
    Integer id;
    String username;
    String password;
    String oldPassword;
    String email;
    String searchKeys;
    String authorization;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSearchKeys() {
        return searchKeys;
    }

    public void setSearchKeys(String searchKeys) {
        this.searchKeys = searchKeys;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    @Override
    public String toString() {
        return "AccountReq{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", oldPassword='" + oldPassword + '\'' +
                ", email='" + email + '\'' +
                ", searchKeys='" + searchKeys + '\'' +
                ", authorization='" + authorization + '\'' +
                '}';
    }
}
