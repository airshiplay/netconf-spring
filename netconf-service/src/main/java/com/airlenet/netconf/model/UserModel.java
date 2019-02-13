package com.airlenet.netconf.model;

import com.airlenet.data.domain.Userable;
import com.airlenet.data.jpa.DataEntity;
import org.apache.catalina.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@Entity
public class UserModel extends DataEntity<UserModel, Long> implements Userable {
    @Id
    private Long id;
    private String name;
    private String password;
    private String username;
    private boolean enable;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }
}
