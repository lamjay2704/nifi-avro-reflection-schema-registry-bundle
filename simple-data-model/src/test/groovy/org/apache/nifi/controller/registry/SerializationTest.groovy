package org.apache.nifi.controller.registry

import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import org.apache.avro.reflect.ReflectData
import org.apache.avro.reflect.ReflectDatumReader
import org.apache.avro.reflect.ReflectDatumWriter
import org.junit.jupiter.api.Test
import org.schema.base.Text
import org.schema.base.Thing

class SerializationTest {
    @Test
    void test() {
        def schema = ReflectData.get().getSchema(Thing.class)
        assert schema
        def testObject = new Thing("test name", "blah blah blah", Text.builder().text("inner text object").build())
        def writer = new ReflectDatumWriter(Thing.class)
        def out = new ByteArrayOutputStream()
        def encoder = EncoderFactory.get().binaryEncoder(out, null)
        writer.write(testObject, encoder)
        encoder.flush()
        out.close()

        def genericReader = new GenericDatumReader(schema)
        def genericInput = out.toByteArray()
        def genericDecoder = DecoderFactory.get().binaryDecoder(genericInput, null)
        def genericRecord = genericReader.read(null, genericDecoder)
        assert genericRecord
        assert genericRecord.getAt("alternateName").toString() == "test name"
        assert genericRecord.getAt("description").toString() == "blah blah blah"
        assert genericRecord.getAt("disambiguatingDescription")?.getAt("text")?.toString() == "inner text object"

        def reflectionReader = new ReflectDatumReader(schema)
        def decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null)
        def object = (Thing)reflectionReader.read(null, decoder)
        assert object
        assert object == testObject
    }
}