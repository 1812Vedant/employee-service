package com.codingshuttle.TestingApp.controllers;

import com.codingshuttle.TestingApp.TestcontainerConfiguration;
import com.codingshuttle.TestingApp.dto.EmployeeDto;
import com.codingshuttle.TestingApp.entities.Employee;
import com.codingshuttle.TestingApp.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class EmployeeControllerTestIT extends AbstractIntegrationTest{


    @Test
    void testGetEmployeeById_success(){
        Employee savedEmployee = employeeRepository.save(employee);

        webTestClient.get()
                .uri("employees/{id}",savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .value(employeeDto1 -> {
                    assertThat(employeeDto1.getEmail()).isEqualTo(savedEmployee.getEmail());
                    assertThat(employeeDto1.getId()).isEqualTo(savedEmployee.getId());
                    assertThat(employeeDto1.getSalary()).isEqualTo(savedEmployee.getSalary());


                });
    }

    @Test
    void testGetEmployeeById_faliure(){
        webTestClient.get().uri("employees/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeAlreadyExists_thenThrowException(){
        Employee savedEmployee = employeeRepository.save(employee);
        webTestClient.post()
                .uri("/employees")
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().is5xxServerError();

    }

    @Test
    void testCreateNewEmployee_whenEmployeeNotExists_thenCreateEmployee(){
        webTestClient.post()
                .uri("/employees")
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(employeeDto.getEmail());

    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExist_thenThrowException(){
        webTestClient.put()
                .uri("/employees/999")
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateEmployee_whenEmployeeExistAndUpdatingEmail_thenThrowException(){
        Employee savedEmployee = employeeRepository.save(employee);
        employeeDto.setName("random name");
        employeeDto.setEmail("random@gmail.com");
        webTestClient.put()
                .uri("/employees/1")
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateEmployee_whenEmployeeIsValid_thenSaveEmployee(){
        Employee savedEmployee = employeeRepository.save(employee);
        employeeDto.setName("random name");
        webTestClient.put()
                .uri("/employees/1")
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .isEqualTo(employeeDto);
    }

    @Test
    void testDeletingEmployee_whenEmployeeDoestNotExits_thenThrowExcepetion(){
        webTestClient.delete()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeletingEmployee_whenEmployeeDoesExits_thenDelete(){
        employeeRepository.save(employee);
        webTestClient.delete()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}