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
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Remainder> saveRemainder(@RequestBody RemainderRequest remainder)
    {
        System.out.println(remainder.getStartDate() + " " + remainder.getEndDate());
        Remainder newRemainder = remainderRepository.save(new Remainder(
                Date.valueOf(remainder.getStartDate()),
                Date.valueOf(remainder.getEndDate()),
                Time.valueOf(LocalTime.parse(remainder.getTime())),
                remainder.getCount(),
                medicamentRepository.findById(remainder.getMedicamentId()).get(),
                userRepository.findByUsername(remainder.getUsername()).get()
        ));
        return new ResponseEntity<>(newRemainder, HttpStatus.OK);
    }

    @DeleteMapping("/deleteRemainder/{id}")
    public ResponseEntity<List<Remainder>> deleteRemainder(@PathVariable(name="id") int id){
        Optional<Remainder> remainderOptional = remainderRepository.findById(id);
        if (remainderOptional.isPresent())
        {
            remainderRepository.delete(remainderOptional.get());
            return getRemainderByUser(remainderOptional.get().getUsr().getUsername());
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/editRemainder")
    public ResponseEntity<Remainder> editRemainder(@RequestBody RemainderRequest remainder)
    {
        Remainder newRemainder = remainderRepository.findById(remainder.getId()).get();
        newRemainder.setStartDate(Date.valueOf(remainder.getStartDate()));
        newRemainder.setEndDate(Date.valueOf(remainder.getEndDate()));
        newRemainder.setTime(Time.valueOf(LocalTime.parse(remainder.getTime())));
        newRemainder.setCount(remainder.getCount());
        newRemainder.setMedicament(medicamentRepository.findById(remainder.getMedicamentId()).get());
        newRemainder.setUsr(userRepository.findByUsername(remainder.getUsername()).get());

        newRemainder = remainderRepository.save(newRemainder);
        return new ResponseEntity<>(newRemainder, HttpStatus.OK);
    }
}

