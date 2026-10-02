package gr.aueb.cf.schoolapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record TeacherEditReadOnlyDTO(

        UUID uuid,

        String firstname,

        String lastname,

        String vat,

        Long regionId
) {
}
