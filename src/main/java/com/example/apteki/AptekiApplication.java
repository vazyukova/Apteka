package com.example.apteki;

import com.example.apteki.controller.MedicamentController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class AptekiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AptekiApplication.class, args);
	}

}
