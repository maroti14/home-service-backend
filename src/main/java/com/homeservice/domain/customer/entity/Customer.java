package com.homeservice.domain.customer.entity;



import com.homeservice.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@PrimaryKeyJoinColumn(name = "user_id")
public class Customer extends User {

    private String profilePhotoUrl;

    // customer's default city
    private String city;
}
