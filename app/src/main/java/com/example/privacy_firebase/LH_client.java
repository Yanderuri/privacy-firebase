package com.example.privacy_firebase;

import java.util.Random;

public class LH_client {
    private Random random = new Random();
    int epsilon = 3;
    int g = 2;
    double p = Math.exp(epsilon) / (Math.exp(epsilon) + g - 1);
    double q = 1.0 / (Math.exp(epsilon) + g - 1);

    public int perturb(int data, int seed){
        long x = hash(String.valueOf(data), seed) % g;
        long y = x;
        if (random.nextDouble() > (p - q)){
            y = random.nextInt(g);
        }
        return (int) y;
    }
    public static long hash(String s,int seed) {
        long hash = 0;
        for(int i = 0; i < s.length(); ++i) {
            hash = s.charAt(i) + (hash << 6) + (hash << 16) - hash + seed;
        }
        return hash;
    }
    public int[] privatize(int data){
        int seed = random.nextInt(4);
        return new int[]{this.perturb(data, seed), seed};
    }
}
