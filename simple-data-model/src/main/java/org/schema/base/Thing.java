package org.schema.base;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Thing {
    private String alternateName;
    private String description;
    private Text disambiguatingDescription;

    public Thing(String alternateName, String description, Text disambiguatingDescription) {
        this.alternateName = alternateName;
        this.description = description;
        this.disambiguatingDescription = disambiguatingDescription;
    }
}