package com.medilabo.patient.document;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document("patients")
public class PatientDocument {

    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String phone;
    private Instant createdAt;
    private Instant updatedAt;

    public String getId() { return id; }
    public PatientDocument setId(String id) { this.id = id; return this; }
    public String getFirstName() { return firstName; }
    public PatientDocument setFirstName(String firstName) { this.firstName = firstName; return this; }
    public String getLastName() { return lastName; }
    public PatientDocument setLastName(String lastName) { this.lastName = lastName; return this; }
    public LocalDate getBirthDate() { return birthDate; }
    public PatientDocument setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; return this; }
    public Gender getGender() { return gender; }
    public PatientDocument setGender(Gender gender) { this.gender = gender; return this; }
    public String getAddress() { return address; }
    public PatientDocument setAddress(String address) { this.address = address; return this; }
    public String getPhone() { return phone; }
    public PatientDocument setPhone(String phone) { this.phone = phone; return this; }
    public Instant getCreatedAt() { return createdAt; }
    public PatientDocument setCreatedAt(Instant createdAt) { this.createdAt = createdAt; return this; }
    public Instant getUpdatedAt() { return updatedAt; }
    public PatientDocument setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
}