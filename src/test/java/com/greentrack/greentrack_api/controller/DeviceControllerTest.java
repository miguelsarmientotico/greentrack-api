package com.greentrack.greentrack_api.controller;

import com.greentrack.greentrack_api.entity.DeviceEntity;
import com.greentrack.greentrack_api.entity.DeviceStatusEnum;
import com.greentrack.greentrack_api.entity.DeviceTypeEnum;
import com.greentrack.greentrack_api.exception.NotFoundException; 
import com.greentrack.greentrack_api.service.DeviceService;
import com.greentrack.greentrack_api.service.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DeviceController.class)
@AutoConfigureMockMvc(addFilters = false) 
class DeviceControllerTest {

    private static final UUID DEVICE_ID_OK = UUID.randomUUID();
    private static final UUID DEVICE_ID_NOT_FOUND = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceService deviceService;

    @MockitoBean
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        DeviceEntity mockDevice = new DeviceEntity();
        mockDevice.setId(DEVICE_ID_OK);
        mockDevice.setName("Sensor Test");
        mockDevice.setBrand("Sony");
        mockDevice.setDeviceStatus(DeviceStatusEnum.DISPONIBLE);
        mockDevice.setDeviceType(DeviceTypeEnum.LAPTOP);

        when(deviceService.getDeviceById(DEVICE_ID_OK))
            .thenReturn(Optional.of(mockDevice));
        when(deviceService.getDeviceById(DEVICE_ID_NOT_FOUND))
            .thenThrow(new NotFoundException("Equipo no encontrado"));
    }

    @Test
    void getDeviceById() throws Exception {
        mockMvc.perform(get("/devices/" + DEVICE_ID_OK)
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(DEVICE_ID_OK.toString()))
            .andExpect(jsonPath("$.name").value("Sensor Test"))
            .andExpect(jsonPath("$.brand").value("Sony"));
    }

    @Test
    void getDeviceNotFound() throws Exception {
        mockMvc.perform(get("/devices/" + DEVICE_ID_NOT_FOUND)
            .contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.path").value("/devices/" + DEVICE_ID_NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Equipo no encontrado"));
    }
}
