package com.greentrack.greentrack_api.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponseDTO {

  private UserStats users;
  private DeviceStats devices;
  private LoanStats loans;

  @Data
  @Builder
  public static class UserStats {
    private long total;
  }

  @Data
  @Builder
  public static class DeviceStats {
    private long total;
    private long available;
    private long borrowed;
  }

  @Data
  @Builder
  public static class LoanStats {
    private long total;
    private long active;
    private long returned;
  }
}
