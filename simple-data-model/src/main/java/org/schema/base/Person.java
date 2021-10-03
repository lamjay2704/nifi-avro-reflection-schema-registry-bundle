package org.schema.base;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person extends Thing {
    private String additionalName;
    private String familyName;
    private String givenName;

    @Builder
    public Person(String alternateName, String description, Text disambiguatingDescription, String additionalName, String familyName, String givenName) {
        super(alternateName, description, disambiguatingDescription);
        this.additionalName = additionalName;
        this.familyName = familyName;
        this.givenName = givenName;
    }
}
