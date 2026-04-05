package com.codingshuttle.TestingApp.services.impl;

import com.codingshuttle.TestingApp.dto.EmployeeDto;
import com.codingshuttle.TestingApp.entities.Employee;
import com.codingshuttle.TestingApp.exceptions.ResourceNotFoundException;
import com.codingshuttle.TestingApp.repositories.EmployeeRepository;
import com.codingshuttle.TestingApp.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // No @SpringBootTest, no Docker
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository; // Mocked — no real DB

    @Spy
    private ModelMapper modelMapper; // Mock this too if your service uses it

    @InjectMocks
    private EmployeeServiceImpl employeeService; // Real service, fake dependencies

    private Employee mockEmployee;
    private EmployeeDto mockEmployeeDto;

    @BeforeEach
    void setUp() {
        mockEmployee = Employee.builder()
                .id(1L)
                .name("Vedant")
                .email("vedant@gmail.com")
                .salary(100L)
                .build();

        mockEmployeeDto = modelMapper.map(mockEmployee,EmployeeDto.class);
    }

    @Test
    void testGetEmployeeById_WhenEmployeeIdIsPresent_ThenReturnEmployeeDto() {
        // Arrange — tell the mock what to return
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
//        when(modelMapper.map(mockEmployee, EmployeeDto.class)).thenReturn(mockEmployeeDto);

        // assign
         when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        //act
        EmployeeDto employeeDto = employeeService.getEmployeeById(1l);

        //assert
        assertThat(employeeDto.getId()).isEqualTo(1L);

        //check if method is called on same mock or not
        verify(employeeRepository).findById(1L);

        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.getId()).isEqualTo(1L);
//        assertThat(result.getEmail()).isEqualTo("vedant@gmail.com");
//
//        // Verify repository was actually called
//        verify(employeeRepository).findById(1L);
//        verify(employeeRepository, never()).save(any()); // ensure no accidental saves
    }

    @Test
    void testCreateNewEmployee_WhenValidEmployee_ThenCreateNewEmployee(){
        //assign ->set values of mock
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);


        //act->get or save some value
        EmployeeDto employeeDto = employeeService.createNewEmployee(mockEmployeeDto);

        //assert->verify if it is working according to you
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployeeDto.getEmail());

        //argument capture to capture object which we are passing
        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);

        verify(employeeRepository).save(employeeArgumentCaptor.capture());

        Employee capturedEmploye = employeeArgumentCaptor.getValue();
        assertThat(capturedEmploye.getEmail()).isEqualTo(mockEmployee.getEmail());
    }

    @Test
    void testGetEmployeeById_whenEmployeeIsNotPresent_thenThrowException(){

        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(()->employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1l);

    }

    @Test
    void testCreateNewEmployee_WhenAttemptingToCreateEmployeeWithExistingEmail_ThrowException(){
        when(employeeRepository.findByEmail("vedant@gmail.com")).thenReturn(List.of(mockEmployee));

        assertThatThrownBy(()->employeeService.createNewEmployee(mockEmployeeDto)).isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: "+mockEmployee.getEmail());

        verify(employeeRepository).findByEmail(mockEmployeeDto.getEmail());
        verify(employeeRepository,never()).save(any());

    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException(){
        //arrage
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        //act and assert
        assertThatThrownBy(()->employeeService.updateEmployee(1L,mockEmployeeDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
        verify(employeeRepository,never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateEmail_thenThrowException(){
        when(employeeRepository.findById(mockEmployeeDto.getId())).thenReturn(Optional.of(mockEmployee));

        mockEmployeeDto.setEmail("random@gmail.com");

        assertThatThrownBy(()->employeeService.updateEmployee(mockEmployeeDto.getId(),mockEmployeeDto)).isInstanceOf(RuntimeException.class).hasMessage("The email of the employee cannot be updated");

        verify(employeeRepository).findById(mockEmployeeDto.getId());
        verify(employeeRepository,never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenValidEmployee_thenUpdateEmployee(){
        //arrange
        when(employeeRepository.findById(mockEmployeeDto.getId())).thenReturn(Optional.of(mockEmployee));

        mockEmployeeDto.setName("Random name");
        mockEmployeeDto.setSalary(199L);

        Employee newEmployeee = modelMapper.map(mockEmployeeDto,Employee.class);

        when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployeee);

        EmployeeDto updatedEmployeeDto = employeeService.updateEmployee(mockEmployeeDto.getId(),mockEmployeeDto);

        assertThat(updatedEmployeeDto).isEqualTo(mockEmployeeDto);
        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any());


    }

    @Test
    void testDeleteemployee_whenEmployeeDoesNotExists_thenThrowException(){
        when(employeeRepository.existsById(1L)).thenReturn(false);

        //act
        assertThatThrownBy(()->employeeService.deleteEmployee(1l))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: "+1L);

        verify(employeeRepository,never()).deleteById(anyLong());
    }

    @Test
    void testDeleteemployee_whenEmployeeDoesExists_thenDeleteEmployee(){
        when(employeeRepository.existsById(1L)).thenReturn(true);

        //act
        assertThatCode(()->employeeService.deleteEmployee(1l)).doesNotThrowAnyException();


        verify(employeeRepository).deleteById(anyLong());
    }

//    @Test
//    void testGetEmployeeById_WhenEmployeeIdIsNotPresent_ThenThrowException() {
//        // Arrange
//        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ResourceNotFoundException.class, () -> {
//            employeeService.getEmployeeById(99L);
//        });
//
//        verify(employeeRepository).findById(99L);
//    }
}