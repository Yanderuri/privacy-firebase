package com.example.privacy_firebase;

import java.util.Random;

public class LH_client {
    private Random rand = new Random();
    float epsilon;
    int domain_size;
    int g = 2;
    double p,q;

    public LH_client(float epsilon, int domain_size) {
        this.epsilon = epsilon;
        this.domain_size = domain_size;
        p = Math.exp(epsilon) / (Math.exp(epsilon) + g - 1);
        q = 1.0 / (Math.exp(epsilon) + g - 1);
    }
    public void hash(int data, int seed){

    }
    public int perturb(int data, int seed){
        int x = 0;
        int y = x;

        if (rand.nextFloat() > (p-q)){
            y = rand.nextInt(g);
        }
        return y;
    }
}
