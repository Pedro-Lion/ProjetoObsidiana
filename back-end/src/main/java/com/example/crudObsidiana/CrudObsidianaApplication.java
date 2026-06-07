package com.example.crudObsidiana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


// ✅ @EnableCaching: ativa o mecanismo de cache do Spring Framework.
// Com isso, as anotações @Cacheable, @CacheEvict e @CachePut passam a funcionar.
@EnableCaching
@SpringBootApplication
public class CrudObsidianaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudObsidianaApplication.class, args);
	}

}
