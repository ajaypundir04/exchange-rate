package com.scalable.capital.exchange.rate.model.ecb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "Envelope")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Envelope {

    @JacksonXmlElementWrapper(localName = "Cube")
    private List<TimeCube> timeCubes;

    public List<TimeCube> getTimeCubes() {
        return timeCubes;
    }

    public void setTimeCubes(List<TimeCube> timeCubes) {
        this.timeCubes = timeCubes;
    }
}
