//package com.chh.trustfort.payment.controller;
//
//import com.chh.trustfort.payment.dto.AuthRequest;
//import com.chh.trustfort.payment.model.Users;
//import com.chh.trustfort.payment.service.UsersService;
//import com.chh.trustfort.payment.dto.RegisterRequest;
//import com.chh.trustfort.payment.dto.AuthResponse;
//import com.chh.trustfort.payment.jwt.JwtTokenUtil;
//import org.jasypt.encryption.StringEncryptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//
//import static com.chh.trustfort.payment.constant.ApiPath.BASE_API;
//
//@RestController
//@RequestMapping(BASE_API + "/users")
//public class UsersController {
//
//    @Autowired
//    private UsersService usersService;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JwtTokenUtil jwtTokenUtil;
//
//    @Autowired
//    private StringEncryptor stringEncryptor;
//
//    // ✅ Register new user
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
//        if (usersService.getUserByUserName(request.getUserName()) != null) {
//            return ResponseEntity.badRequest().body("User already exists!");
//        }
//
//        // Generate encryption key
//        String rawEncryptionKey = request.getUserName() + "_secureKey";
//        String encryptedKey = stringEncryptor.encrypt(rawEncryptionKey);
//
//        // Create new user
//        Users newUser = new Users();
//        newUser.setUserName(request.getUserName());
//        newUser.setPasscode(passwordEncoder.encode(request.getPassword()));  // Hash password
//        newUser.setPin(passwordEncoder.encode(request.getPin()));  // Encrypt PIN
//        newUser.setEmailAddress(request.getEmailAddress());
//        newUser.setFirst_name(request.getFirstName());
//        newUser.setLast_name(request.getLastName());
//        newUser.setUserClass(request.getUserClass());
//        newUser.setDeviceId(request.getDeviceId());
//        newUser.setActive(true); // User is active by default
//        newUser.setApproved(false); // Needs admin approval
//        newUser.setCreatedBy("SYSTEM"); // Set default creator
//        newUser.setEncryptionKey(encryptedKey);
//        newUser.setEcred(encryptedKey + "/1234567890abcdef"); // Encryption Credentials
//
//        usersService.saveUser(newUser);
//
//        return ResponseEntity.ok("User registered successfully!");
//    }
//
//
//    // ✅ Authenticate user and get token
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody AuthRequest request) {
//        Users user = usersService.getUserByUserName(request.getUserName());
//        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasscode())) {
//            return ResponseEntity.badRequest().body(new AuthResponse("Invalid credentials", null));
//        }
//
//        String token = jwtTokenUtil.doGenerateToken(user.getUserName(), "127.0.0.1");
//        return ResponseEntity.ok(new AuthResponse("Login successful", token));
//    }
//}
