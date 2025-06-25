package com.chh.trustfort.accounting;

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
 * @param <E>
 */
public class Quintuple<A, B, C, D, E> {


    public A isError;
    public B token;
    public C idToken;
    public D appUser;
    public E payload;

    public Quintuple(A isError, B token, C idToken, D appUser, E payload) {
        this.isError = isError;
        this.token = token;
        this.idToken = idToken;
        this.appUser = appUser;
        this.payload = payload;
    }
}
