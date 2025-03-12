package com.chh.trustfort.gateway;

/**
 *
 * @author dofoleta
 */
/**
 *
 * @author dofoleta
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <D>
 */
public class Quadruple <A,B,C,D> {
    public A isError;
    public B token;
    public C idToken;
    public D payload;
    public Quadruple(A isError,B token,C idToken,D payload){
        this.isError=isError;
        this.token=token;
        this.idToken=idToken;
        this.payload=payload;
    }
}
