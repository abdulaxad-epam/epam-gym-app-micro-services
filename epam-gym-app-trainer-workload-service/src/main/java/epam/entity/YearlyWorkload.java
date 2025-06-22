package epam.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class YearlyWorkload {

    @Field(targetType = FieldType.INT32)
    private Integer year;

    @Field(targetType = FieldType.ARRAY)
    private List<MonthlyWorkload> months;
}
