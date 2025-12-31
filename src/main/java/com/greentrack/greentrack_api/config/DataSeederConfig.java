package com.greentrack.greentrack_api.config;

import com.greentrack.greentrack_api.entity.*;
import com.greentrack.greentrack_api.repository.*;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeederConfig {

  // @Profile("demo")
  @Bean
  CommandLineRunner loadData(
      UserRepository userRepository,
      DeviceRepository deviceRepository,
      LoanRepository loanRepository,
      PasswordEncoder passwordEncoder) {
    return args -> {
      System.out.println("🌱 INICIANDO CARGA DE DATOS DE PRUEBA (DATA SEEDING)...");
      if (userRepository.count() > 0) {
        System.out.println("⚠️ Ya existen datos en la base de datos. Saltando carga.");
        return;
      }
      UserEntity admin = new UserEntity();
      // admin.setId(UUID.randomUUID());
      admin.setUsername("admin");
      admin.setFullName("Administrador del Sistema");
      admin.setEmail("admin@greentrack.com");
      admin.setPassword(passwordEncoder.encode("admin1234"));
      admin.setRole(RoleEnum.ADMIN);
      admin.setStatus(UserStatusEnum.ACTIVO);

      UserEntity employee = new UserEntity();
      // employee.setId(UUID.randomUUID());
      employee.setUsername("jdoe");
      employee.setFullName("John Doe");
      employee.setEmail("jdoe@greentrack.com");
      employee.setPassword(passwordEncoder.encode("123456"));
      employee.setRole(RoleEnum.USER);
      employee.setStatus(UserStatusEnum.ACTIVO);

      userRepository.save(admin);
      userRepository.save(employee);

      // 3. Crear Dispositivos
      DeviceEntity laptop = new DeviceEntity();
      // laptop.setId(UUID.randomUUID());
      laptop.setName("MacBook Pro M1");
      laptop.setBrand("Apple");
      laptop.setType(DeviceTypeEnum.LAPTOP); // Asumiendo que tienes este enum
      laptop.setStatus(DeviceStatusEnum.DISPONIBLE);

      DeviceEntity phone = new DeviceEntity();
      // phone.setId(UUID.randomUUID());
      phone.setName("Samsung Galaxy S23");
      phone.setBrand("Samsung");
      phone.setType(DeviceTypeEnum.CELULAR);
      phone.setStatus(DeviceStatusEnum.PRESTADO);

      deviceRepository.save(laptop);
      deviceRepository.save(phone);

      // 4. Crear un Préstamo (Loan) activo
      LoanEntity loan = new LoanEntity();
      // loan.setId(UUID.randomUUID());
      loan.setEmployee(employee);
      loan.setDevice(phone);
      loan.setIssuedAt(LocalDateTime.now().minusDays(5)); // Se prestó hace 5 días
      loan.setStatus(LoanStatusEnum.ACTIVO);

      loanRepository.save(loan);

      System.out.println("✅ DATOS DE PRUEBA CARGADOS EXITOSAMENTE");
    };
  }
}
