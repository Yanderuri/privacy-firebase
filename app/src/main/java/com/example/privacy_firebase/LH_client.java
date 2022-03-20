package com.example.privacy_firebase;

import java.util.Random;

public class LH_client {
    private final Random rand = new Random();
    float epsilon;
    int domain_size;
    int g;
    double p,q;

    public LH_client(float epsilon, int domain_size) {
        this.epsilon = epsilon;
        this.domain_size = domain_size;
        g = (int) Math.round(Math.exp(epsilon)) + 1;
        p = Math.exp(epsilon) / (Math.exp(epsilon) + g - 1);
        q = 1.0 / (Math.exp(epsilon) + g - 1);
    }
    public int perturb(int data){
        if (data == -1)
            return -1;
        int x = -1;
        // TODO: x needs to be hashed, or a random number
        int y = x;

        if (rand.nextFloat() > (p-q)){
            y = rand.nextInt(g);
        }
        return y;
    }
}
