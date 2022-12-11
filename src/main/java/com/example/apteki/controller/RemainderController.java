package com.example.apteki.controller;

import com.example.apteki.model.Remainder;
import com.example.apteki.model.Usr;
import com.example.apteki.payload.RemainderRequest;
import com.example.apteki.repository.MedicamentRepository;
import com.example.apteki.repository.RemainderRepository;
import com.example.apteki.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/remainders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RemainderController {
    private final RemainderRepository remainderRepository;
    private final UserRepository userRepository;
    private final MedicamentRepository medicamentRepository;

    public RemainderController(RemainderRepository remainderRepository, UserRepository userRepository, MedicamentRepository medicamentRepository) {
        this.remainderRepository = remainderRepository;
        this.userRepository = userRepository;
        this.medicamentRepository = medicamentRepository;
    }

    @GetMapping("/byUser/{username}")
    public ResponseEntity<List<Remainder>> getRemainderByUser(@PathVariable(name="username") String username)
    {
        Usr user = userRepository.findByUsername(username).get();
        List<Remainder> remainders = remainderRepository.findByUsr(user);

        return new ResponseEntity<>(remainders, HttpStatus.OK);
    }

    @PostMapping("/saveRemainder")
    public ResponseEntity<List<Remainder>> saveRemainder(@RequestBody RemainderRequest remainder)
    {
        System.out.println(remainder.getStartDate() + " " + remainder.getEndDate());
        Remainder newRemainder = remainderRepository.save(new Remainder(
                Date.valueOf(remainder.getStartDate()),
                Date.valueOf(remainder.getEndDate()),
                remainder.getCount(),
                medicamentRepository.findById(remainder.getMedicamentId()).get(),
                userRepository.findByUsername(remainder.getUsername()).get()
        ));
        return getRemainderByUser(newRemainder.getUsr().getUsername());
    }
}
