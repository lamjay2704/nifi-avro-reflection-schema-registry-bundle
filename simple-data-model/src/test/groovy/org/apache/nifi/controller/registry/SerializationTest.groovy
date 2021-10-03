package org.apache.nifi.controller.registry

import org.apache.avro.Schema
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import org.apache.avro.reflect.ReflectData
import org.apache.avro.reflect.ReflectDatumReader
import org.apache.avro.reflect.ReflectDatumWriter
import org.junit.jupiter.api.Test
import org.schema.base.Person
import org.schema.base.Text
import org.schema.base.Thing

class SerializationTest {
    private byte[] write(Class test, Object testObject) {
        def writer = new ReflectDatumWriter(test)
        def out = new ByteArrayOutputStream()
        def encoder = EncoderFactory.get().binaryEncoder(out, null)
        writer.write(testObject, encoder)
        encoder.flush()
        out.close()

        out.toByteArray()
    }

    private GenericRecord getGenericRecord(Schema schema, byte[] input) {
        def genericReader = new GenericDatumReader(schema)
        def genericDecoder = DecoderFactory.get().binaryDecoder(input, null)
        genericReader.read(null, genericDecoder)
    }

    @Test
    void test() {
        def schema = ReflectData.get().getSchema(Thing.class)
        assert schema
        def testObject = new Thing("test name", "blah blah blah", Text.builder().text("inner text object").build())
        def out = write(Thing.class, testObject)

        def genericRecord = getGenericRecord(schema, out)
        assert genericRecord
        assert genericRecord.getAt("alternateName").toString() == "test name"
        assert genericRecord.getAt("description").toString() == "blah blah blah"
        assert genericRecord.getAt("disambiguatingDescription")?.getAt("text")?.toString() == "inner text object"

        def reflectionReader = new ReflectDatumReader(schema)
        def decoder = DecoderFactory.get().binaryDecoder(out, null)
        def object = (Thing)reflectionReader.read(null, decoder)
        assert object
        assert object == testObject
    }

    @Test
    void serializePerson() {
        def person = Person.builder()
            .additionalName("Jonny")
            .familyName("Quest")
            .givenName("Jonathan")
            .id(new URI("quest:jonny"))
            .build()
        def schema = ReflectData.get().getSchema(Person.class)
        assert schema && schema.getFields().size() == 7

        def out = write(Person.class, person)

        def reflectionReader = new ReflectDatumReader(schema)
        def decoder = DecoderFactory.get().binaryDecoder(out, null)
        def object = (Person)reflectionReader.read(null, decoder)
        assert object
        assert object == person
        assert object.id == person.id
    }
}