package com.example.doctor2.repository;

import com.example.doctor2.model.Doctor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends CrudRepository<Doctor,Long> {

    List<Doctor> findDoctorByCityAndSpeciality(String city, String speciality);

}
