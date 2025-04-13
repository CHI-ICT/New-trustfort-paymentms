package com.chh.trustfort.payment;

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
    public D Users;
    public E payload;

    public Quintuple(A isError, B token, C idToken, D users, E payload) {
        this.isError = isError;
        this.token = token;
        this.idToken = idToken;
        this.Users = users;
        this.payload = payload;
    }
}
