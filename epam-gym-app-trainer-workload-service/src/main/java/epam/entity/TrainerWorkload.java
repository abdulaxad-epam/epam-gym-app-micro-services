package epam.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "trainer_workload")
@CompoundIndex(name = "lf_index", def = "{'trainerFirstName' : 1, 'trainerLastName': 1}")
public class TrainerWorkload {

    @Id
    private String id;

    @Field(targetType = FieldType.STRING, write = Field.Write.NON_NULL)
    private String trainerUsername;

    @Field(targetType = FieldType.STRING, write = Field.Write.NON_NULL)
    private String trainerFirstName;

    @Field(targetType = FieldType.STRING, write = Field.Write.NON_NULL)
    private String trainerLastName;

    @Field(targetType = FieldType.BOOLEAN, write = Field.Write.NON_NULL)
    private Boolean isActive;

    @Field(targetType = FieldType.ARRAY, write = Field.Write.NON_NULL)
    private List<YearlyWorkload> years;

}
