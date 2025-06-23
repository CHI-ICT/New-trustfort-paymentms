package com.chh.trustfort.payment.service;

public interface UserClient {
    void updateTransactionPin(Long userId, String hashedPin);
    String getHashedTransactionPin(Long userId);
}




//@FeignClient(name = "user-service", url = "http://user-service")
//public interface UserClient {
//    @PostMapping("/users/{userId}/transaction-pin")
//    void updateTransactionPin(@PathVariable Long userId, @RequestBody String hashedPin);
//
//    @GetMapping("/users/{userId}/transaction-pin")
//    String getHashedTransactionPin(@PathVariable Long userId);
//}