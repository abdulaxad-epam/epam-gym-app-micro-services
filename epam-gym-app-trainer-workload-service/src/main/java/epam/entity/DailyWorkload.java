package epam.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DailyWorkload {

    @Field(targetType = FieldType.INT32)
    private Integer day;

    @Field(targetType = FieldType.INT32)
    private Integer dailyTrainingDuration;
}
