package com.greentrack.greentrack_api;

import org.springframework.boot.SpringApplication;

public class TestGreentrackApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(GreentrackApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
