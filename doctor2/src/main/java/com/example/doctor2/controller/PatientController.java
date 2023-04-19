package com.example.doctor2.controller;

import com.example.doctor2.model.Doctor;
import com.example.doctor2.model.Patient;
import com.example.doctor2.repository.DoctorRepository;
import com.example.doctor2.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class PatientController {

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @PostMapping("/addPatient")
    public ResponseEntity setPateint(@RequestBody Patient patient){


        if(patient.getName().length() < 3){
            return new ResponseEntity("Error Name less than 3 characters",HttpStatus.OK);
        }
        if(patient.getCity().length() > 20){
            return new ResponseEntity("Error City length greater than 20 characters",HttpStatus.OK);
        }
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$");


        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(patient.getEmail());

        if(!matcher.matches()){
            return new ResponseEntity("Error Email",HttpStatus.OK);
        }
        if(patient.getPhone().toString().length() < 10){
            return new ResponseEntity("Error phone number",HttpStatus.OK);
        }
        patientRepository.save(patient);

        return new ResponseEntity(HttpStatus.OK);

    }

    @GetMapping("/patient/getAll")
    public List<Patient> getAllPatient(){
        List<Patient> patientList = new ArrayList<>();
        Iterable<Patient> iterable = patientRepository.findAll();
        iterable.forEach(patientList::add);
        return patientList;
    }

    @DeleteMapping("/deletePatient/{id}")
    public ResponseEntity<Patient> deletePatient(@PathVariable(value = "id") Long id){
        Optional<Patient> patient=patientRepository.findById(id);
        System.out.println(patient.get().getName());
        if(patient.isPresent()){
            patientRepository.delete(patient.get());
            return new ResponseEntity("Patient has been deleted successfully.",HttpStatus.OK);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getDoctorBySymptom/{id}")
    public ResponseEntity<List<Doctor>> getName(@PathVariable(value = "id") Long id){

        Optional<Patient> patient = patientRepository.findById(id);
        HashMap<String,List<String>> map = new HashMap<>();
        List<String> ortho = new ArrayList<>();
        List<String> gyno = new ArrayList<>();
        List<String> dermo = new ArrayList<>();
        List<String> ent = new ArrayList<>();

        ortho.add("Arthritis");
        ortho.add("Backpain");
        ortho.add("Tissue injuries");

        gyno.add("Dysmenorrhea");

        dermo.add("Skin infection");
        dermo.add("skin burn");

        ent.add("Ear Pain");
//
        map.put("Orthopedic",ortho);
        map.put("Gynecology",gyno);
        map.put("Dermatology",dermo);
        map.put("ENT",ent);

        String symptom = patient.get().getSymptom();
        Iterator iterator = map.entrySet().iterator();
        String speciality ="";

        while(iterator.hasNext()){
            Map.Entry element = (Map.Entry)iterator.next();
            List<String> newList = (List<String>) element.getValue();
            if(newList.contains(symptom)){
                speciality = element.getKey().toString();
                break;
            }
            else{
                speciality = "none";
            }


        }
        List<Doctor> doctorList = null;
        if(!speciality.equals("none")){
            doctorList = doctorRepository.findDoctorByCityAndSpeciality(patient.get().getCity().toString(),speciality);
        }

        if(doctorList.isEmpty()){
            String message = "There isn’t any doctor present at your location for your symptom";
           throw new ResponseStatusException(HttpStatus.NOT_FOUND,message);
        }
        else {
            return new ResponseEntity<>(doctorList, HttpStatus.OK);
        }
    }

//    public ResponseEntity<String> errorDisplay(String messsage){
//        return new ResponseEntity<>(messsage,HttpStatus.OK);
//    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "There isn’t any doctor present at your location for your symptom")
    public class DoctorNotFoundException extends Exception {
        // ...
    }
}
