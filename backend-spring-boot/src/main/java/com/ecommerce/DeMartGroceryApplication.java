package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Serializable;

@SpringBootApplication
public class DeMartGroceryApplication implements Serializable {

	public static void main(String[] args) {
		SpringApplication.run(DeMartGroceryApplication.class, args);
	}

}
