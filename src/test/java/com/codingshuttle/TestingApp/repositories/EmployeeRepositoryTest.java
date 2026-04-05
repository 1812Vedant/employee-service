package com.codingshuttle.TestingApp.repositories;


import com.codingshuttle.TestingApp.TestcontainerConfiguration;
import com.codingshuttle.TestingApp.entities.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.List;
import java.util.TimeZone;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Slf4j
@Testcontainers
@Import(TestcontainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {


    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }
//    @Container
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//    }

    @Autowired
    private EmployeeRepository employeeRepository;

    Employee employee;

    @BeforeEach
    void createEmployee(){
         employee = com.codingshuttle.TestingApp.entities.Employee.builder().email("vedant@gmail.com").name("vedant").salary(100l).build();
    }

    @Test
    void testfindByEmail_whenEmailIsValid_thenReturnEmployee() {
        //Arrange,Given
        employeeRepository.save(employee);
        //Act,When
        List<Employee> employeeList = employeeRepository.findByEmail(employee.getEmail());
        //Assert,Then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList.get(0).getEmail()).isEqualTo(employee.getEmail()) ;
    }

    @Test
    void testfindByEmail_whenEmailIsNotFound_thenReturnEmptyEmployeeList(){
        log.info("email of employee {}",employee.getEmail());
        List<Employee> employeeList = employeeRepository.findByEmail(employee.getEmail());
        assertThat(employeeList).isNotNull();

      //  assertThat(employeeList.get(0).getEmail()).isEqualTo("vedantnotemail") ;
    }
}