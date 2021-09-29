package com.silvericarus.parroquiasanpedropovedajaen.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

public class RandomColors {
    private final Stack<String> recycle;
    private final Stack<String> colors;

    public RandomColors() {
        colors = new Stack<>();
        recycle =new Stack<>();

        colors.addAll(Arrays.asList(
            "#FF673A",
                "#5AD4F2","#329682",
                "#FF47B3","#FF4C4C","#68C3FB",
                "#F2C887","#2A9D8F","#7BC8F6","#A7FFB5",
                "#FEB2D0","#13BBAF","#FFE5AD","#D3494E","#FDDC5C"

        ));
    }

    public String getColor() {
        if (colors.size()==0) {
            while(!recycle.isEmpty())
                colors.push(recycle.pop());

        }
        Collections.shuffle(colors);
        String c= colors.pop();
        recycle.push(c);
        return c;
    }
}
