package com.bookmyride.models;

/**
 * Created by vinod on 2/9/2017.
 */
public class GDColor {

    /**
     *  value for the color of path (int) resource
     */
    public Integer colorLine;
    /**
     *  value for the color of pin (float) BitmapDescriptorFactory.X
     */
    public Float colorPin;


    /**
     * Constructor of GDColor
     * @param colorLine : the color of path
     * @param colorPin : color of pin
     */
    public GDColor(Integer colorLine, Float colorPin) {
        super();
        this.colorLine = colorLine;
        this.colorPin = colorPin;
    };
}
