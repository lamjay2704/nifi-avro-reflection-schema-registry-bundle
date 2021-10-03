package org.schema.base;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.avro.reflect.Nullable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Thing {
    @Nullable
    private String alternateName;
    @Nullable
    private String description;
    @Nullable
    private Text disambiguatingDescription;

    public Thing(String alternateName, String description, Text disambiguatingDescription) {
        this.alternateName = alternateName;
        this.description = description;
        this.disambiguatingDescription = disambiguatingDescription;
    }
}