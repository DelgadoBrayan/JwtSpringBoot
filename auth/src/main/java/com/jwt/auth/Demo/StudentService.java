package com.jwt.auth.Demo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jwt.auth.User.UserRole;
@Service
public class StudentService {
    private final StudentRepository studentRepository;
    public StudentService(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }
    public List<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    public Student getByEmailStudent(String email){
        return studentRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: "+ email));

    }

    public String registerStudent(Student student){
        if(student.getEmail() == null || student.getEmail().isEmpty()){
            throw new IllegalArgumentException("Email no puede ser nulo o vacio ");
        }
        if(studentRepository.findByEmail(student.getEmail()).isPresent()){
            throw new IllegalArgumentException("Ya existe un estudiante registrado con este correo");
        }
        if (student.getRole() == null) {
            student.setRole(UserRole.STUDENT);
        }
        studentRepository.save(student);

        return "Estudiante con email" + student.getEmail() + "a sido registrado correctamente";
    }

    public String updateStudentPatch(String email, Student updatedStudent) {
        Student existingStudent = studentRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Estudiente con el email " + email + " no encontrado"));

            if (updatedStudent.getName() != null) existingStudent.setName(updatedStudent.getName());
            if(updatedStudent.getEmail() != null) existingStudent.setEmail(updatedStudent.getEmail());
            if (updatedStudent.getAge() > 0) existingStudent.setAge(updatedStudent.getAge());
           
        studentRepository.save(existingStudent);

        return "Estudiante con el email" + email + "actualizado correctamente";
    }

    public String deleteStudent(String email) {
        Student student = studentRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Estudiante con el " + email + " no a sido encontrado"));

        studentRepository.delete(student);

        return "Estudiante " + email + " eliminado correctamente";
    }
}
