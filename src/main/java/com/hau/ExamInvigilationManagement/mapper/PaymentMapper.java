package com.hau.ExamInvigilationManagement.mapper;

import com.hau.ExamInvigilationManagement.dto.response.PaymentResponse;
import com.hau.ExamInvigilationManagement.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "lecturerId", source = "lecturer.id")
    @Mapping(target = "lecturerName", expression = "java(payment.getLecturer() != null ? (payment.getLecturer().getFullName() != null ? payment.getLecturer().getFullName() : payment.getLecturer().getUser().getFirstName() + \" \" + payment.getLecturer().getUser().getLastName()) : \"Unknown\")")
    PaymentResponse toResponse(Payment payment);
}
