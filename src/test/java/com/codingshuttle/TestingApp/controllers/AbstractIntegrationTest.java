package com.codingshuttle.TestingApp.controllers;

import com.codingshuttle.TestingApp.TestcontainerConfiguration;
import com.codingshuttle.TestingApp.dto.EmployeeDto;
import com.codingshuttle.TestingApp.entities.Employee;
import com.codingshuttle.TestingApp.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.TimeZone;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainerConfiguration.class)
@AutoConfigureWebTestClient
public class AbstractIntegrationTest {

    @Autowired
     WebTestClient webTestClient;


    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }



    @Autowired
    EmployeeRepository employeeRepository;

    Employee employee;

    EmployeeDto employeeDto;


    @BeforeEach
    void setUp(){
        employeeRepository.deleteAll();
        employee = Employee.builder()
                .id(1L)
                .salary(1000L)
                .name("vedant")
                .email("vedantmore@gmail.com").build();

        employeeDto = EmployeeDto.builder()
                .id(1L)
                .salary(1000L)
                .name("vedant")
                .email("vedantmore@gmail.com").build();
    }
}
