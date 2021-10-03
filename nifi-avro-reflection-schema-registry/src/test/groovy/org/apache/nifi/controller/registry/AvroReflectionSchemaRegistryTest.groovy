package org.apache.nifi.controller.registry

import org.apache.nifi.processor.AbstractProcessor
import org.apache.nifi.processor.ProcessContext
import org.apache.nifi.processor.ProcessSession
import org.apache.nifi.processor.exception.ProcessException
import org.apache.nifi.schemaregistry.services.SchemaRegistry
import org.apache.nifi.serialization.record.RecordSchema
import org.apache.nifi.serialization.record.SchemaIdentifier
import org.apache.nifi.serialization.record.StandardSchemaIdentifier
import org.apache.nifi.util.TestRunner
import org.apache.nifi.util.TestRunners
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AvroReflectionSchemaRegistryTest {
    TestRunner runner
    SchemaRegistry registry

    @BeforeEach
    void setup() {
        runner = TestRunners.newTestRunner(new AbstractProcessor() {
            @Override
            void onTrigger(ProcessContext processContext, ProcessSession processSession) throws ProcessException {

            }
        })
        registry = new AvroReflectionSchemaRegistry()
        runner.addControllerService("registry", registry)
        runner.setProperty(registry, AvroReflectionSchemaRegistry.JAR, "target/test-lib/simple-data-model-1.0.0.jar")
        runner.setProperty(registry, "org.schema.base.Text", "Text")
        runner.setProperty(registry, "org.schema.base.Thing", "Thing")
        runner.enableControllerService(registry)
        runner.assertValid()
    }

    @Test
    void test() {
        def identifier = new StandardSchemaIdentifier.Builder().name("Thing").build()
        RecordSchema thing = registry.retrieveSchema(identifier)
        assert thing
        assert thing.getFields()?.size() == 6
    }
}
