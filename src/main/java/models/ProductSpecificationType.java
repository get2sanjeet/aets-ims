package main.java.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductSpecificationType {
    Integer productSpecTypeID;
    String productSpecTypeName;
    String productSpecTypeModels; //Total items of this type
    int productSpecTypePrice; //Total items of this type
}
