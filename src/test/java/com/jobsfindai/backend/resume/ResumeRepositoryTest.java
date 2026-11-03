package com.jobsfindai.backend.resume;

import com.jobsfindai.backend.user.User;
import com.jobsfindai.backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
public class ResumeRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Test
    void shouldFindResumesBelongingToUser() {

        User user = new User("Alex@gmail.com", "Password");
        User savedUser = userRepository.save(user);

        Resume resume1 = new Resume("backend-resume.pdf", "123backend", savedUser);
        Resume savedResume = resumeRepository.save(resume1);

        Resume resume2 = new Resume("frontend-resume.pdf", "123frontend", savedUser);
        Resume savedResume2 = resumeRepository.save(resume2);



        List<Resume> list = resumeRepository.findByUserId(savedUser.getId());

        assertEquals(2, list.size());

        for (Resume resume : list) {
            assertEquals(savedUser.getId(), resume.getUser().getId());
        }

    }

    @Test
    void shouldOnlyFindResumesBelongingToSpecifiedUser() {
        User alex = userRepository.save(
                new User("alex-isolation@example.com", "test-hash")
        );

        User sara = userRepository.save(
                new User("sara-isolation@example.com", "test-hash")
        );

        Resume alexResume1 = new Resume(
                "backend-cv.pdf",
                "test/alex-backend.pdf",
                alex
        );

        Resume alexResume2 = new Resume(
                "backend-cv.pdf",
                "test/alex2-backend.pdf",
                alex
        );

        Resume saraResume1 = new Resume(
                "backend-cv.pdf",
                "test/sara-backend.pdf",
                sara
        );

        Resume savedResume1 = resumeRepository.save(alexResume1);
        Resume savedResume2 = resumeRepository.save(alexResume2);
        Resume savedResume3 = resumeRepository.save(saraResume1);

        List<Resume> alexResumes = resumeRepository.findByUserId(alex.getId());
        List<Resume> saraResumes = resumeRepository.findByUserId(sara.getId());

        assertEquals(2, alexResumes.size());
        assertEquals(1, saraResumes.size());

        for (Resume each : alexResumes){
            assertEquals(alex.getId(), each.getUser().getId());
        }

        for (Resume each : saraResumes){
            assertEquals(sara.getId(), each.getUser().getId());
        }
    }

}
