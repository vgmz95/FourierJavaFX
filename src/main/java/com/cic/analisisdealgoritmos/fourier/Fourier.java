package com.cic.analisisdealgoritmos.fourier;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.util.FastMath;

public class Fourier {       
    public static Complex[] fft(Complex[] a) {
        int n = a.length;
        if (n == 1) {
            return new Complex[] { a[0] };
        }

        Complex[] aEven = new Complex[n / 2];
        Complex[] aOdd = new Complex[n / 2];
        for (int i = 0; i < n / 2; i++) {
            // even indexed coefficients
            aEven[i] = a[i * 2];
            // odd indexed coefficients
            aOdd[i] = a[i * 2 + 1];
        }

        Complex[] e = fft(aEven);
        Complex[] d = fft(aOdd);
        Complex[] y = new Complex[n];
        for (int k = 0; k < n / 2; k++) {
            Complex w = ComplexUtils.polar2Complex(1.0, (2.0 * FastMath.PI * (double) k) / (double) n);
            Complex wk_dk = w.multiply(d[k]);
            y[k] = e[k].add(wk_dk);
            y[k + n / 2] = e[k].subtract(wk_dk);
        }

        return y;

    }

    // https://cp-algorithms.com/algebra/fft.html
    public static Complex[] ifft(Complex[] a) {
        int n = a.length;
        if (n == 1) {
            return new Complex[] { a[0] };
        }

        Complex[] aEven = new Complex[n / 2];
        Complex[] aOdd = new Complex[n / 2];
        for (int i = 0; i < n / 2; i++) {
            // even indexed coefficients
            aEven[i] = a[i * 2];
            // odd indexed coefficients
            aOdd[i] = a[i * 2 + 1];
        }

        Complex[] e = ifft(aEven);
        Complex[] d = ifft(aOdd);
        Complex[] y = new Complex[n];
        for (int k = 0; k < n / 2; k++) {
            Complex w = ComplexUtils.polar2Complex(1.0, - 2.0 * FastMath.PI * (double) k / (double) n);
            Complex wk_dk = w.multiply(d[k]);
            y[k] = (e[k].add(wk_dk)).divide((double) 2.0);
            y[k + n / 2] = (e[k].subtract(wk_dk)).divide((double) 2.0);
        }

        return y;

    }

}