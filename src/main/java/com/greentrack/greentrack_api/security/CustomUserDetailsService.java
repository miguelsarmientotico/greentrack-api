package com.greentrack.greentrack_api.security;

import com.greentrack.greentrack_api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);

  private final UserRepository repository;

  public CustomUserDetailsService(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    LOG.debug("Iniciando búsqueda de usuario para autenticación: {}", username);

    return repository
        .findByUsername(username)
        .map(
            userEntity -> {
              LOG.info("Usuario encontrado exitosamente: {}", username);
              return new UserEntityDetails(userEntity);
            })
        .orElseThrow(
            () -> {
              LOG.warn(
                  "Fallo de autenticación: El usuario '{}' no existe en la base de datos",
                  username);
              return new UsernameNotFoundException("User not found...");
            });
  }
}
