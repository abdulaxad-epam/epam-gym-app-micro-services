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
public class MonthlyWorkload {

    @Field(targetType = FieldType.INT32)
    private Integer month;

    @Field(targetType = FieldType.INT32, name = "monthlyTrainingDuration")
    private Integer monthlyTrainingDuration;

    @Field(targetType = FieldType.ARRAY)
    private List<DailyWorkload> days;
}
