package com.raycast.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

/**
 * Created by Lucas on 29/08/2014.
 */
@JsonSerialize(using = CoordinatesSerializer.class)
@JsonDeserialize(using = CoordinatesDeserializer.class)
public class Coordinates implements Serializable{
    private double latitude;
    private double longitude;

    public Coordinates(){
    }

    public Coordinates(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

class CoordinatesSerializer extends JsonSerializer<Coordinates> {
    @Override
    public void serialize(Coordinates value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        jgen.writeNumber(value.getLongitude());
        jgen.writeNumber(value.getLatitude());
        jgen.writeEndArray();
    }
}

class CoordinatesDeserializer extends JsonDeserializer<Coordinates> {
    @Override
    public Coordinates deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        Iterator<double[]> iterator = jp.readValuesAs(double[].class);
        double[] rawCoordinates = iterator.next();
        Coordinates c = new Coordinates();
        c.setLongitude(rawCoordinates[0]);
        c.setLatitude(rawCoordinates[1]);
        return c;
    }
}