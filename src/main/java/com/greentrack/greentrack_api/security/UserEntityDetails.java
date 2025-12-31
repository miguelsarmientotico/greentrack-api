package com.greentrack.greentrack_api.security;

import com.greentrack.greentrack_api.entity.UserEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserEntityDetails implements UserDetails {

  private final String username;
  private final String password;
  private final List<GrantedAuthority> authorities;

  public UserEntityDetails(UserEntity userEntity) {
    this.username = userEntity.getUsername();
    this.password = userEntity.getPassword();
    this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name()));
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
