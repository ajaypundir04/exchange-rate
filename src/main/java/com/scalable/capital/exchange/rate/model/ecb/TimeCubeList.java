package com.scalable.capital.exchange.rate.model.ecb;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;


public class TimeCubeList {

    @JacksonXmlProperty(localName = "Cube")
    private List<TimeCube> timeCubes;

    public List<TimeCube> getTimeCubes() {
        return timeCubes;
    }

    public void setTimeCubes(List<TimeCube> timeCubes) {
        this.timeCubes = timeCubes;
    }

}
