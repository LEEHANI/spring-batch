package com.example.springbatch.job.itemWriter.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer2 {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String birthdate;

}
