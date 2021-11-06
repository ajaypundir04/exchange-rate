package com.scalable.capital.exchange.rate.model.ecb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeCube {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Cube")
    private List<Cube> timeCubes;
    @JacksonXmlProperty(isAttribute = true, localName = "time")
    private String time;


    public List<Cube> getTimeCubes() {
        return timeCubes;
    }

    public void setTimeCubes(List<Cube> timeCubes) {
        this.timeCubes = timeCubes;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
