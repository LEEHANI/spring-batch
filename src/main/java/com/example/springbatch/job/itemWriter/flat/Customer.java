package com.example.springbatch.job.itemWriter.flat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {

    private int id;
    private String name;
    private int age;
}
