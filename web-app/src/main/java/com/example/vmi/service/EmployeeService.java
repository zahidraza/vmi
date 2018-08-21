package com.example.vmi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.vmi.dto.User;
import com.example.vmi.entity.Employee;
import com.example.vmi.entity.Role;
import com.example.vmi.repository.BuyerRepository;
import com.example.vmi.repository.EmployeeRepository;
import com.example.vmi.util.MiscUtil;
import com.example.vmi.util.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

@Service
@Transactional(readOnly = true)
public class EmployeeService {
    private final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    
    @Autowired EmployeeRepository employeeRepository;

    @Autowired BuyerRepository buyerRepository;

    public Employee findOne(Long id) {
        return employeeRepository.findOne(id);
    }

    public Employee findOne(String email) {
        return employeeRepository.findByEmail(email);
    }

    public Employee authenticate(String email, String password) {
        logger.info("authenticate(), email:" + email);
        Employee employee = employeeRepository.findByEmail(email);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (employee != null && encoder.matches(password, employee.getPassword())) {
            return employee;
        }
        logger.info("Either Employee with email:" + email + " does not exist or incorrect credential.");
        return null;
    }
    
    @Transactional
    public Employee changePassword(Long id, String oldPassword, String newPassword){
        logger.info("changePassword(), id:" + id);
        Employee employee = employeeRepository.findOne(id);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (employee != null && encoder.matches(oldPassword, employee.getPassword())) {
            employee.setPassword(newPassword);
            return employee;
        }
        logger.info("Either Employee with id:" + id + " does not exist or incorrect credential.");
        return null;
    }

    @Transactional
    public Employee save(User user) {
        logger.info("save(): " + user);
        Employee employee = new Employee(user.getId(), user.getName(), user.getEmail(), user.getMobile(), Role.parse(user.getRole()), user.getMobile());
        if (user.getBuyer() != null) {
            employee.setBuyer(buyerRepository.findByName(user.getBuyer()));
        }
        return employeeRepository.save(employee);
    }
    
    @Transactional
    public boolean forgotPassword(Long id){
        logger.info("forgotPassword(), id:" + id);
        Employee employee = employeeRepository.findOne(id);
        
        String generatedPassword = new RandomString(8).nextString();

        //Sending Email
        String subject = "Password Reset";
        String message =    "\nYou accessed the Password Reset Service of Vendor Managed Inventory App. \n\n"+
                            "New Password is: " + generatedPassword + " \n\n";
        
        message += "Note: Do change this password immediately.";

        InputStream is = this.getClass().getClassLoader().getResourceAsStream("application.properties");

        Boolean result = MiscUtil.sendMail(is, employee.getEmail(), subject, message);
        
        if(result){
            employee.setPassword(generatedPassword);
            return true;
        }
        return false;
    }
    
    @Transactional
    public void delete(Long id){
        employeeRepository.delete(id);
    }
}
