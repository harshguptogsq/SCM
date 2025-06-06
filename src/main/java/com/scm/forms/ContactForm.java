package com.scm.forms;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ContactForm {

    @NotBlank(message = "name is requied")
    private String name;

    @NotBlank(message = "email is requied")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "phone no. is requied")
    @Pattern(regexp = "^[0-9]{10}$" , message = "invalid phone number")
    private String phoneNumber;

    @NotBlank(message = "address is requied")
    private String address;

    private String description;

    private boolean favorite;

    private String websiteLink;

    private String linkedInLink;

    private MultipartFile contactImage;

    private String picture;
}
