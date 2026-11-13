package com.jobsfindai.backend.resume;

import com.jobsfindai.backend.ai.ClaudeAiService;
import com.jobsfindai.backend.dto.ResumeProfile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.jobsfindai.backend.user.User;
import com.jobsfindai.backend.user.UserRepository;
import tools.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.List;


@Service
public class ResumeService {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private final PdfTextService pdfTextService;
    private final ClaudeAiService claudeAiService;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ObjectMapper objectMapper;

    public ResumeService(PdfTextService pdfTextService,
                         ClaudeAiService claudeAiService,
                         ResumeRepository resumeRepository,
                         UserRepository userRepository,
                         FileStorageService fileStorageService,
                         ResumeAnalysisRepository resumeAnalysisRepository,
                         ObjectMapper objectMapper){

        this.pdfTextService = pdfTextService;
        this.claudeAiService = claudeAiService;
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.objectMapper = objectMapper;
    }


    public void validateResume(MultipartFile file){

        if (file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File cannot be empty"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE){
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "File exceeds 5 MB"
            );
        }

        String filename = file.getOriginalFilename();

        if (filename == null || !filename.toLowerCase().endsWith(".pdf") || !"application/pdf".equals(file.getContentType())){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The uploaded file was not a pdf"
            );
        }

    }

    public List<Resume> getResumesByUser(Long userId) {
        return resumeRepository.findByUserId(userId);
    }

    @Transactional(rollbackFor = IOException.class)
    public ResumeProfile processResume(MultipartFile file, Long userId) throws IOException {
        validateResume(file);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));


        String resumeText = pdfTextService.extractText(file);
        ResumeProfile profile = claudeAiService.analyzeResume(resumeText);

        String storageKey = fileStorageService.store(file);

        try {
            Resume resume = new Resume(
                    file.getOriginalFilename(),
                    storageKey,
                    user
            );

            resumeRepository.save(resume);

            String profileJson = objectMapper.writeValueAsString(profile);

            ResumeAnalysis analysis = new ResumeAnalysis(resume, profileJson);

            resumeAnalysisRepository.save(analysis);

        } catch (RuntimeException exception) {
            fileStorageService.delete(storageKey);
            throw exception;
        }

        return profile;
    }

    public List<ResumeResponse> getResumeResponsesForUser(Long userId) {

        List<Resume> resumes = resumeRepository.findByUserId(userId);

        List<ResumeResponse> responses = resumes.stream().map(
                resume -> new ResumeResponse(
                        resume.getId(),
                        resume.getFilename()
                )
        ).toList();

        return responses;
    }

    public Resume getResumeForUser(Long resumeId, Long userId) {

        return resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Resume not found"
                ));
    }


    public void deleteResume(Long resumeId, Long userId) throws IOException {

        Resume resume = getResumeForUser(resumeId, userId);

        fileStorageService.delete(resume.getStorageKey());

        resumeRepository.delete(resume);
    }


    public String getResumeAnalysis(Long resumeId, Long userId) {

        getResumeForUser(resumeId, userId);

        ResumeAnalysis analysis = resumeAnalysisRepository
                .findByResumeId(resumeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Resume analysis not found"
                ));

        return analysis.getProfileJson();
    }

    public ResumeProfile getResumeProfile(Long resumeId, Long userId) {

        String json = getResumeAnalysis(resumeId, userId);

        return objectMapper.readValue(json, ResumeProfile.class);
    }


}
