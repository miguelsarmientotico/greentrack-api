package com.greentrack.greentrack_api.service;

import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.mapper.DeviceMapper;
import com.greentrack.greentrack_api.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) 
class DeviceServiceTest {

    @Mock
    private DeviceRepository repository;

    @Mock
    private DeviceMapper mapper; 

    @InjectMocks
    private DeviceService deviceService; 

    @Test
    void getDeviceById_ShouldReturnDevice_WhenExists() {
        UUID deviceId = UUID.randomUUID();
        DeviceEntity expectedDevice = new DeviceEntity();
        expectedDevice.setId(deviceId);
        expectedDevice.setName("Test Device");
        when(repository.findById(deviceId)).thenReturn(Optional.of(expectedDevice));
        Optional<DeviceEntity> result = deviceService.getDeviceById(deviceId);
        assertTrue(result.isPresent(), "El optional debería tener valor");
        assertEquals(expectedDevice, result.get(), "El dispositivo retornado debe ser el mismo que el del repo");
        assertEquals(deviceId, result.get().getId());
        verify(repository, times(1)).findById(deviceId);
    }

    @Test
    void getDeviceById_ShouldReturnEmpty_WhenNotExists() {
        UUID deviceId = UUID.randomUUID();
        when(repository.findById(deviceId)).thenReturn(Optional.empty());
        Optional<DeviceEntity> result = deviceService.getDeviceById(deviceId);
        assertTrue(result.isEmpty(), "El optional debería estar vacío si no existe el ID");
        verify(repository, times(1)).findById(deviceId);
    }
}
