package com.example.doctor2.controller;

import com.example.doctor2.model.Doctor;
import com.example.doctor2.model.Patient;
import com.example.doctor2.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class DoctorController {

    @Autowired
    DoctorRepository doctorRepository;

    @PostMapping("/addDoctor")
    public ResponseEntity setDoctor(@RequestBody Doctor doctor){
        if(doctor.getName().length() < 3){
            return new ResponseEntity("Error Name less than 3 characters",HttpStatus.OK);
        }
        if(doctor.getCity().length() > 20){
            return new ResponseEntity("Error City length greater than 20 characters",HttpStatus.OK);
        }
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$");


        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(doctor.getEmail());

        if(!matcher.matches()){
            return new ResponseEntity("Error Email",HttpStatus.OK);
        }
        if(doctor.getPhone().toString().length() < 10){
            return new ResponseEntity("Error phone number",HttpStatus.OK);
        }
        doctorRepository.save(doctor);


        return new ResponseEntity(HttpStatus.OK);

    }

    @GetMapping("/doctor/getAll")
    public List<Doctor> getAllDoctor(){
        List<Doctor> doctorList = new ArrayList<>();
        Iterable<Doctor> iterable = doctorRepository.findAll();
        iterable.forEach(doctorList::add);
        return  doctorList;
    }

    @DeleteMapping("/deleteDoctor/{id}")
    public ResponseEntity<Doctor> deleteDoctor(@PathVariable(value = "id") Long id){
        Optional<Doctor> doctor=doctorRepository.findById(id);

        if(doctor.isPresent()){
            doctorRepository.delete(doctor.get());
            return new ResponseEntity("Doctor has been deleted successfully.",HttpStatus.OK);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}
