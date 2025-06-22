//package com.chh.trustfort.payment.controller;
//
//import com.chh.trustfort.payment.constant.Channel;
//import com.chh.trustfort.payment.dto.AuthRequest;
//import com.chh.trustfort.payment.dto.AuthResponse;
//import com.chh.trustfort.payment.dto.RegisterRequest;
//import com.chh.trustfort.payment.jwt.JwtTokenUtil;
//import com.chh.trustfort.payment.model.AppUser;
//import com.chh.trustfort.payment.repository.AppUserRepository;
//import org.jasypt.encryption.StringEncryptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.util.Arrays;
//
//import static com.chh.trustfort.payment.constant.ApiPath.BASE_API;
//
//@RestController
//@Qualifier("jasyptStringEncryptor")
//@RequestMapping(BASE_API + "/auth")
//public class AppUserController {
//
//    @Autowired
//    private AppUserRepository appUserRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JwtTokenUtil jwtTokenUtil;
//
//    @Autowired
//    private StringEncryptor encryptor; // ✅ Inject Jasypt Encryptor

    // ✅ Register new user
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
//        if (appUserRepository.getAppUserByUserName(request.getUserName()) != null) {
//            return ResponseEntity.badRequest().body("User already exists!");
//        }
//
//        // Validate Channel
//        boolean isValidChannel = Arrays.stream(Channel.values())
////                .anyMatch(c -> c.getChannelCode().equalsIgnoreCase(request.getChannel()));
//
//        if (!isValidChannel) {
//            return ResponseEntity.badRequest().body("Invalid channel! Use one of: MOBILE, USSD, IBANKING");
//        }
//
//
//        // 🔹 Generate a secure encryption key
//        String rawEncryptionKey = request.getUserName() + "_secureKey"; // Base encryption key
//        String encryptedKey = encryptor.encrypt(rawEncryptionKey); // ✅ Encrypt with Jasypt
//
//        // ✅ Construct new user
//        AppUser newUser = new AppUser();
//        newUser.setUserName(request.getUserName());
//        newUser.setPassword(passwordEncoder.encode(request.getPassword()));  // Hash password
//        newUser.setChannel(request.getChannel());
//
//        newUser.setEncryptionKey(encryptedKey); // ✅ Store encrypted key
//        newUser.setEcred(encryptedKey + "/1234567890abcdef"); // Example IV
//        newUser.setPadding("AES/CBC/PKCS5Padding"); // Default padding
//        newUser.setCreatedBy("admin");  // Default admin user
//        newUser.setEnabled(true);
//        newUser.setLocked(false);
//        newUser.setExpired(false);
//        newUser.setAuthenticateSession(false);
//        newUser.setAuthenticateDevice(false);
//        newUser.setAuthenticateIpAddress(false);
//
//        try {
//            appUserRepository.createAppUser(newUser);
//            return ResponseEntity.ok("User registered successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user: " + e.getMessage());
//        }
//    }
//
//
//
//    // ✅ Authenticate user and get token
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody AuthRequest request) {
//        AppUser user = appUserRepository.getAppUserByUserName(request.getUserName());
//        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            return ResponseEntity.badRequest().body(new AuthResponse("Invalid credentials", null));
//        }
//
//        String token = jwtTokenUtil.doGenerateToken(user.getUserName(), "127.0.0.1");
//        return ResponseEntity.ok(new AuthResponse("Login successful", token));
//    }
//}
