package org.schema.base;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URI;

@Getter
@Setter
@NoArgsConstructor
public class Person extends Thing {
    private URI id;
    private String additionalName;
    private String familyName;
    private String givenName;

    @Builder
    public Person(URI id, String alternateName, String description, Text disambiguatingDescription, String additionalName, String familyName, String givenName) {
        super(alternateName, description, disambiguatingDescription);
        this.id = id;
        this.additionalName = additionalName;
        this.familyName = familyName;
        this.givenName = givenName;
    }
}
