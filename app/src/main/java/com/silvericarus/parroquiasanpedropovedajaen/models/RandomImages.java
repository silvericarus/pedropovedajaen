package com.silvericarus.parroquiasanpedropovedajaen.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

public class RandomImages {
    private Stack<String> recycle, images;

    public RandomImages() {
        images = new Stack<>();
        recycle =new Stack<>();

        images.addAll(Arrays.asList(
                "a20210903_200438.jpg",
                "a20210903_200905.jpg",
                "a20210911_204622.jpg",
                "a20210911_204720.jpg"
        ));
    }

    public String getImage() {
        if (images.size()==0) {
            while(!recycle.isEmpty())
                images.push(recycle.pop());
        }
        Collections.shuffle(images);
        String c= images.pop();
        recycle.push(c);
        return c;
    }
}
