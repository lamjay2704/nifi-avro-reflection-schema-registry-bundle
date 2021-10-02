package org.apache.nifi.controller.registry;

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnEnabled;
import org.apache.nifi.avro.AvroTypeUtil;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.schema.access.SchemaField;
import org.apache.nifi.schemaregistry.services.SchemaRegistry;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.SchemaIdentifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Tags({"schema", "registry", "avro", "reflection"})
public class AvroReflectionSchemaRegistry extends AbstractControllerService implements SchemaRegistry {
    private static final Set<SchemaField> FIELDS = Collections.unmodifiableSet(new HashSet<SchemaField>(Arrays.asList(SchemaField.SCHEMA_NAME)));

    public static final PropertyDescriptor JAR = new PropertyDescriptor.Builder()
            .name("avro-reflection-jar")
            .displayName("POJO JAR")
            .description("The JAR file that contains the classes to be converted into Avro schemas.")
            .required(true)
            .dynamicallyModifiesClasspath(true)
            .addValidator(StandardValidators.NON_EMPTY_EL_VALIDATOR)
            .addValidator(StandardValidators.FILE_EXISTS_VALIDATOR)
            .build();

    public static final List<PropertyDescriptor> PROPERTIES = Collections.unmodifiableList(Arrays.asList(
            JAR
    ));

    @Override
    protected PropertyDescriptor getSupportedDynamicPropertyDescriptor(String propertyDescriptorName) {
        try {
            Class.forName(propertyDescriptorName);
        } catch (Exception ex) {
            throw new ProcessException(String.format("Could not add property because %s is not on the classpath.", propertyDescriptorName),
                    ex);
        }

        return new PropertyDescriptor.Builder()
                .name(propertyDescriptorName)
                .displayName(propertyDescriptorName)
                .build();
    }

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return PROPERTIES;
    }

    public RecordSchema retrieveSchema(SchemaIdentifier schemaIdentifier) {
        Optional<String> name = schemaIdentifier.getName();
        if (name.isPresent() && schemas.containsKey(name.get())) {
            return schemas.get(name.get());
        } else if (name.isPresent() && !schemas.containsKey(name.get())) {
            throw new ProcessException(String.format("Could not find a schema named %s", name.get()));
        } else {
            throw new ProcessException("Unsupported schema identifier method used.");
        }
    }

    public Set<SchemaField> getSuppliedSchemaFields() {
        return FIELDS;
    }

    public Map<String, RecordSchema> schemas;

    @OnEnabled
    public void onEnabled(final ConfigurationContext context) {
        Map<String, RecordSchema> temp = new HashMap<>();
        List<PropertyDescriptor> dynamic = context.getProperties().keySet()
                .stream().filter(descriptor -> descriptor.isDynamic())
                .collect(Collectors.toList());
        for (PropertyDescriptor descriptor : dynamic) {
            try {
                Class clz = Class.forName(descriptor.getName());
                Schema schema = ReflectData.get().getSchema(clz);
                temp.put(clz.getSimpleName(), AvroTypeUtil.createSchema(schema));
            } catch (Exception ex) {
                throw new ProcessException(String.format("Exception caught while processing %s", descriptor.getName()), ex);
            }
        }

        schemas = new ConcurrentHashMap<>(temp);
    }
}
